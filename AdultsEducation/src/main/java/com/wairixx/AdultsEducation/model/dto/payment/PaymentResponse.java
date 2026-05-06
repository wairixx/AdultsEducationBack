package com.wairixx.AdultsEducation.model.dto.payment;
import com.wairixx.AdultsEducation.model.enums.PaymentMethod;
import com.wairixx.AdultsEducation.model.enums.PaymentStatus;
import java.math.BigDecimal;
import java.time.LocalDateTime;
public record PaymentResponse(
        Long id, Long educationId, BigDecimal amount,
        PaymentStatus status, PaymentMethod method,
        String transactionRef, LocalDateTime createdAt) {}