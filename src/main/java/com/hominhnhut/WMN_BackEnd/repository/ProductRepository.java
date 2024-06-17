package com.hominhnhut.WMN_BackEnd.repository;

import com.hominhnhut.WMN_BackEnd.domain.enity.Product;
import com.hominhnhut.WMN_BackEnd.domain.enity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface ProductRepository extends JpaRepository<Product, String> {

    Page<Product> findAllByProductNameContaining(String usernameContain, Pageable pageable);

    Product getProductByProductId(String s);
    @Query(value = "SELECT p FROM Product p " +
            "INNER JOIN p.category c ON p.category.categoryId = c.categoryId \n" +
            "WHERE c.categoryId = :categoryId")
    Page<Product> getProductByCategoryId(@Param("categoryId") String categoryId, Pageable pageable);
}
