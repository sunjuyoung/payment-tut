package com.example.payment.adapter.web.domain;

import com.example.payment.adapter.web.domain.enums.PaymentMethod;
import com.example.payment.adapter.web.domain.enums.PaymentType;
import com.example.payment.adapter.web.service.in.PaymentStatusUpdateCommand;
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

    @Column(name = "psp_raw_data", columnDefinition = "json")
    private String pspRawData;  //PSP 로 부터 받은 원시 데이터

    @Column(name = "approved_at", columnDefinition = "TIMESTAMP")
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


    public void updateExtraDetails(PaymentStatusUpdateCommand command) {
        this.orderName = command.getExtraDetails().getOrderName();
        this.paymentMethod = command.getExtraDetails().getMethod();
        this.approvedAt = command.getExtraDetails().getApprovedAt();
        this.orderId = command.getOrderId();
        this.paymentType = command.getExtraDetails().getType();
        this.pspRawData = command.getExtraDetails().getPspRawData();


    }

}
