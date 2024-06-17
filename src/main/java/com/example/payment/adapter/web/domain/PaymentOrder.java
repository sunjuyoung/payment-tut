package com.example.payment.adapter.web.domain;

import com.example.payment.adapter.web.domain.enums.PaymentOrderStatus;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

@Entity
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "payment_order")
public class PaymentOrder {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private Long buyerId;

    private int amount;
    private String orderId;
    private Long sellerId;

    @Enumerated(EnumType.STRING)
    private PaymentOrderStatus paymentOrderStatus;


    private boolean isLedgerUpdated;
    private boolean isWalletUpdated;
    private boolean isPaymentDone;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "payment_event_id")
    private PaymentEvent paymentEvent;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id")
    private Product product;


    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false, columnDefinition = "TIMESTAMP")
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at", columnDefinition = "TIMESTAMP")
    private LocalDateTime updatedAt;

    public void setPaymentEvent(PaymentEvent paymentEvent) {
        this.paymentEvent = paymentEvent;
    }

    public void setPaymentStatus(PaymentOrderStatus paymentOrderStatus) {
        this.paymentOrderStatus = paymentOrderStatus;
    }
}
