package com.example.apidemo.repositories;

import com.example.apidemo.models.Product;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ProductRepository extends JpaRepository<Product, Long> {
    public Optional<Product> findByProductName(String productName);
}
