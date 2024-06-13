package com.hominhnhut.WMN_BackEnd.repository;

import com.hominhnhut.WMN_BackEnd.domain.enity.Product;
import com.hominhnhut.WMN_BackEnd.domain.enity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProductRepository extends JpaRepository<Product, String> {

    Page<Product> findAllByProductNameContaining(String usernameContain, Pageable pageable);

    Product getProductByProductId(String s);
}
