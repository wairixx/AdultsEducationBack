package com.wairixx.AdultsEducation.model.entity;

import com.wairixx.AdultsEducation.model.enums.PaymentMethod;
import com.wairixx.AdultsEducation.model.enums.PaymentStatus;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;

@Entity
@Table(name = "payments")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor
public class Payment extends BaseEntity {

    @OneToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "education_id", nullable = false, unique = true)
    private Education education;

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal amount;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private PaymentStatus status;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private PaymentMethod method;

    /** Masked card "**** 4242" or reference */
    @Column(name = "transaction_ref", length = 100)
    private String transactionRef;
}