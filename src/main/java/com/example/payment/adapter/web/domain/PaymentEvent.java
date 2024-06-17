package com.example.payment.adapter.web.domain;

import com.example.payment.adapter.web.domain.enums.PaymentMethod;
import com.example.payment.adapter.web.domain.enums.PaymentType;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import org.springframework.cglib.core.Local;
import org.springframework.data.annotation.CreatedDate;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "payment_event")
public class PaymentEvent {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String orderId;

    private Long buyerId;

    @Enumerated(EnumType.STRING)
    private PaymentType paymentType;

    private String paymentKey;

    private String orderName;

    @Enumerated(EnumType.STRING)
    private PaymentMethod paymentMethod;


    private LocalDateTime approvedAt;
    private boolean isPaymentDone;
    private Long totalAmount;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false, columnDefinition = "TIMESTAMP")
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at", columnDefinition = "TIMESTAMP")
    private LocalDateTime updatedAt;



    @Builder.Default
    @OneToMany(mappedBy = "paymentEvent", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<PaymentOrder> paymentOrders = new ArrayList<>();


    public void addPaymentOrder(PaymentOrder paymentOrder) {
        paymentOrders.add(paymentOrder);
        paymentOrder.setPaymentEvent(this);
    }


}
