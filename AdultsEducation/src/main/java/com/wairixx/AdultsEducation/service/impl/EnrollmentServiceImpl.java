package com.wairixx.AdultsEducation.service.impl;

import com.wairixx.AdultsEducation.aspect.Loggable;
import com.wairixx.AdultsEducation.exception.BusinessException;
import com.wairixx.AdultsEducation.exception.DuplicateResourceException;
import com.wairixx.AdultsEducation.exception.ResourceNotFoundException;
import com.wairixx.AdultsEducation.model.dto.education.EnrollmentRequest;
import com.wairixx.AdultsEducation.model.entity.*;
import com.wairixx.AdultsEducation.model.enums.EducationStatus;
import com.wairixx.AdultsEducation.model.enums.PaymentMethod;
import com.wairixx.AdultsEducation.model.enums.PaymentStatus;
import com.wairixx.AdultsEducation.model.enums.Role;
import com.wairixx.AdultsEducation.repository.CourseRepository;
import com.wairixx.AdultsEducation.repository.EducationRepository;
import com.wairixx.AdultsEducation.repository.PaymentRepository;
import com.wairixx.AdultsEducation.repository.StudentProfileRepository;
import com.wairixx.AdultsEducation.service.EnrollmentService;
import com.wairixx.AdultsEducation.util.SecurityUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;

@Service
@RequiredArgsConstructor
@Loggable
@Slf4j
public class EnrollmentServiceImpl implements EnrollmentService {

    private final CourseRepository courseRepository;
    private final EducationRepository educationRepository;
    private final PaymentRepository paymentRepository;
    private final StudentProfileRepository studentProfileRepository;

    /**
     * Unit of Work: Education + Payment створюються в одній транзакції.
     * Якщо будь-яка частина впаде — rollback всього.
     */
    @Override
    @Transactional
    public EnrollmentResult enroll(EnrollmentRequest request) {
        User currentUser = SecurityUtils.getCurrentUser()
                .orElseThrow(() -> new BusinessException("error.auth.required"));

        StudentProfile student;
        boolean isAdminOverride = currentUser.getRole() == Role.ADMIN && request.studentId() != null;

        if (isAdminOverride) {
            // Admin enrolls a specific student
            student = studentProfileRepository.findById(request.studentId())
                    .orElseThrow(() -> new BusinessException("error.enrollment.student.profile.missing"));
        } else {
            if (currentUser.getRole() != Role.STUDENT)
                throw new BusinessException("error.enrollment.only.students");
            student = studentProfileRepository.findById(currentUser.getId())
                    .orElseThrow(() -> new BusinessException("error.enrollment.student.profile.missing"));
        }

        Course course = courseRepository.findById(request.courseId())
                .orElseThrow(() -> new ResourceNotFoundException("error.course.not.found"));

        if (!isAdminOverride && !course.getVisible()) {
            throw new BusinessException("error.course.not.visible");
        }

        if (course.getTeacher().getId().equals(student.getId())) {
            throw new BusinessException("error.enrollment.teacher.self");
        }

        // Перевірка: немає активного запису
        educationRepository.findByStudentIdAndCourseId(student.getId(), course.getId())
                .ifPresent(e -> {
                    throw new DuplicateResourceException("error.enrollment.already.exists");
                });

        // Валідація оплати для платних курсів
        BigDecimal price = course.getPrice() == null ? BigDecimal.ZERO : course.getPrice();
        boolean isFree = price.compareTo(BigDecimal.ZERO) == 0;

        if (isFree && request.paymentMethod() != PaymentMethod.FREE) {
            throw new BusinessException("error.enrollment.free.method");
        }
        if (!isFree && request.paymentMethod() == PaymentMethod.FREE) {
            throw new BusinessException("error.enrollment.paid.method.required");
        }

        // 1. Education
        Education education = new Education();
        education.setStudent(student);
        education.setCourse(course);
        education.setStatus(EducationStatus.ACTIVE);
        education.setEnrolledDate(LocalDate.now());
        education.setProgress(0);
        education = educationRepository.save(education);

        // 2. Payment (симуляція — у реальному проєкті тут був би виклик платіжного шлюзу)
        Payment payment = new Payment();
        payment.setEducation(education);
        payment.setAmount(price);
        payment.setMethod(request.paymentMethod());
        payment.setStatus(isFree ? PaymentStatus.SUCCESS : simulateGateway(request));
        payment.setTransactionRef(request.transactionRef());
        payment = paymentRepository.save(payment);

        if (payment.getStatus() == PaymentStatus.FAILED) {
            log.warn("Payment failed for student {} course {}, rolling back", student.getId(), course.getId());
            // Кинувши виняток ми гарантуємо rollback усієї транзакції (і Education, і Payment).
            throw new BusinessException("error.enrollment.payment.failed");
        }

        log.info("Enrollment SUCCESS: student {} → course {}, payment {} UAH",
                student.getId(), course.getId(), price);

        return new EnrollmentResult(education, payment);
    }

    /** Симуляція шлюзу — у реальному коді тут інтеграція зі Stripe/LiqPay/etc. */
    private PaymentStatus simulateGateway(EnrollmentRequest request) {
        // Поки вважаємо, що всі реальні платежі успішні.
        return PaymentStatus.SUCCESS;
    }
}