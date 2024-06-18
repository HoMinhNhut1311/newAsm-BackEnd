package com.hominhnhut.WMN_BackEnd.repository;

import com.hominhnhut.WMN_BackEnd.domain.enity.Product;
import com.hominhnhut.WMN_BackEnd.domain.enity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.Set;

public interface ProductRepository extends JpaRepository<Product, String> {

    Page<Product> findAllByProductNameContaining(String usernameContain, Pageable pageable);

    Product getProductByProductId(String s);
    @Query(value = "SELECT p FROM Product p " +
            "INNER JOIN p.category c ON p.category.categoryId = c.categoryId \n" +
            "WHERE c.categoryId = :categoryId")
    Page<Product> getProductByCategoryId(@Param("categoryId") String categoryId, Pageable pageable);

    @Query(value = "SELECT p.* FROM cart_product cp " +
            "INNER JOIN cart c ON c.cart_id = cp.cart_id " +
            "INNER JOIN product p ON p.product_id = cp.product_id " +
            "GROUP BY p.product_id " +
            "ORDER BY COUNT(cp.product_id) DESC " +
            "LIMIT 1", nativeQuery = true)
    Product getProductBestSeller();

    @Query(value = "SELECT p.* " +
            "FROM cart " +
            "INNER JOIN cart_product cp ON cart.cart_id = cp.cart_id " +
            "INNER JOIN product p ON p.product_id = cp.product_id " +
            "WHERE cart.local_date = :localDate",
            nativeQuery = true)
    Set<Product> getProductSoldByLocalDate(@Param("localDate") LocalDate localDate);


}
