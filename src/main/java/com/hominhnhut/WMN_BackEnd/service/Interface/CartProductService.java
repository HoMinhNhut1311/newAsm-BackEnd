package com.hominhnhut.WMN_BackEnd.service.Interface;

import com.hominhnhut.WMN_BackEnd.domain.enity.CartProduct;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.Set;

@Service
public interface CartProductService {

    Set<CartProduct> StatisticalByLocalDate(LocalDate localDate);
}
