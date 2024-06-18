package com.example.payment.adapter.web.domain;

import com.example.payment.adapter.web.domain.enums.PaymentOrderStatus;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "payment_order_histories")
public class PaymentOrderHistories {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "payment_order_id")
    private PaymentOrder paymentOrder;

    private PaymentOrderStatus previousStatus;

    private PaymentOrderStatus newStatus;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false, columnDefinition = "TIMESTAMP")
    private LocalDateTime createdAt;

    private String changedBy;

    private String reason;



    public PaymentOrderHistories(PaymentOrder paymentOrder, PaymentOrderStatus previousStatus, PaymentOrderStatus newStatus, String reason) {
        this.paymentOrder = paymentOrder;
        this.previousStatus = previousStatus;
        this.newStatus = newStatus;
        this.reason = reason;
    }
}
