package com.hominhnhut.WMN_BackEnd.domain.enity;

import jakarta.persistence.*;
import lombok.*;

import java.util.HashSet;
import java.util.Set;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
public class Product {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String productId;

    private String productName;

    private double productPrice;

    private String productDes;

    @OneToOne
    private MediaFile image;

    @ManyToOne
    @JoinColumn(name = "categorId")
    private Category category;


    @ManyToMany(mappedBy = "products")
    private Set<Cart> carts = new HashSet<>();

}
