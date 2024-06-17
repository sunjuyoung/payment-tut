package com.example.payment.adapter.web.repository;

import com.example.payment.adapter.web.domain.PaymentOrderHistories;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PaymentOrderHistoriesRepository extends JpaRepository<PaymentOrderHistories, Long> {
}
