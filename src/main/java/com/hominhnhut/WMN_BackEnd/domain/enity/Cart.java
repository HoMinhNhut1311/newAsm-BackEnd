package com.hominhnhut.WMN_BackEnd.domain.enity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
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

    private LocalDateTime localDateTime;

    private boolean status;

    @ManyToOne
            @JoinColumn(name = "userId")
    User customer;

    @OneToMany
    @JoinColumn(name = "productId")
    Set<Product> products;
}
