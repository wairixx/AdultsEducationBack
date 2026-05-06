package com.wairixx.AdultsEducation.util;

import com.wairixx.AdultsEducation.model.dto.payment.PaymentResponse;
import com.wairixx.AdultsEducation.model.entity.Payment;
import org.springframework.stereotype.Component;

@Component
public class PaymentMapper {
    public PaymentResponse toResponse(Payment p) {
        return new PaymentResponse(p.getId(), p.getEducation().getId(), p.getAmount(),
                p.getStatus(), p.getMethod(), p.getTransactionRef(), p.getCreatedAt());
    }
}