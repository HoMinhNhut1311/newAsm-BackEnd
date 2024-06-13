package com.hominhnhut.WMN_BackEnd.domain.enity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
public class Cart {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String cartId;

    private LocalDate localDate;

    private boolean status;

    @ManyToOne
    @JoinColumn(name = "userId")
    private User user;

    @ManyToMany
    @JoinTable(
            name = "cart_product",
            joinColumns = @JoinColumn(name = "cartId"),
            inverseJoinColumns = @JoinColumn(name = "productId")
    )
    private Set<Product> products = new HashSet<>();
}
