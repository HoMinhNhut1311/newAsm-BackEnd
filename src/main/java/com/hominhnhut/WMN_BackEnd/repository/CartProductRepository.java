package com.hominhnhut.WMN_BackEnd.repository;

import com.hominhnhut.WMN_BackEnd.domain.enity.CartProduct;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.Set;

public interface CartProductRepository extends JpaRepository<CartProduct, Long> {

    @Query(value = "SELECT cp.*\n" +
            "            FROM cart\n" +
            "            INNER JOIN cart_product cp ON cart.cart_id = cp.cart_id\n" +
            "            INNER JOIN product p ON p.product_id = cp.product_id\n" +
            "        WHERE cart.local_date = :localDate",
            nativeQuery = true)
    Set<CartProduct> getProductSoldByLocalDate(@Param("localDate") LocalDate localDate);
}
