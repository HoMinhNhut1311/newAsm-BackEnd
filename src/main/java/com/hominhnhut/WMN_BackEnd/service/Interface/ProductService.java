package com.hominhnhut.WMN_BackEnd.service.Interface;

import com.hominhnhut.WMN_BackEnd.domain.enity.CartProduct;
import com.hominhnhut.WMN_BackEnd.domain.request.ProductRequest;
import com.hominhnhut.WMN_BackEnd.domain.response.ProductResponse;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.Set;

@Service
public interface ProductService {

    ProductResponse saveProduct(ProductRequest productRequest);

    void deleteProduct(String productId);

    ProductResponse updateProduct(String productId, ProductRequest productRequest);

    Set<ProductResponse> findAll();

    ProductResponse getProductBestSeller();

    ProductResponse findProductById(String productId);

    Set<ProductResponse> getProductByProductNameContaining(String productName);

}
