package com.example.payment.adapter.web.repository;

import com.example.payment.adapter.web.domain.Product;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ProductRepository extends JpaRepository<Product,Long> {

}
