package com.hominhnhut.WMN_BackEnd.service.impl;

import com.hominhnhut.WMN_BackEnd.domain.enity.Cart;
import com.hominhnhut.WMN_BackEnd.domain.enity.Product;
import com.hominhnhut.WMN_BackEnd.domain.enity.User;
import com.hominhnhut.WMN_BackEnd.domain.request.CartRequest;
import com.hominhnhut.WMN_BackEnd.domain.request.UserDtoRequest;
import com.hominhnhut.WMN_BackEnd.domain.response.CartResponse;
import com.hominhnhut.WMN_BackEnd.domain.response.UserDtoResponse;
import com.hominhnhut.WMN_BackEnd.exception.errorType;
import com.hominhnhut.WMN_BackEnd.exception.myException.AppException;
import com.hominhnhut.WMN_BackEnd.mapper.MyMapperInterFace;
import com.hominhnhut.WMN_BackEnd.repository.CartRespository;
import com.hominhnhut.WMN_BackEnd.repository.ProductRepository;
import com.hominhnhut.WMN_BackEnd.repository.UserRepository;
import com.hominhnhut.WMN_BackEnd.service.Interface.CartService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
@RequiredArgsConstructor
public class CartServiceImpl implements CartService {
    CartRespository cartRespository;
    ProductRepository productRepository;
    UserRepository userRepository;
    MyMapperInterFace<CartRequest, Cart, CartResponse> mapper;
    @Override
    public List<CartResponse> getAllCart() {
        System.out.println(cartRespository.findAll());
        return this.cartRespository.findAll().stream()
                .map(this.mapper::mapToResponese).
                collect(Collectors.toList());
    }

    @Override
    public CartResponse findCartById(String id) {
        return null;
    }

    @Override
    public CartResponse saveCart(CartRequest cartRequest) {
        Cart cart = mapper.mapFromRequest(cartRequest);
        Optional<User> user = userRepository.findUSerByUsername(cartRequest.getUsername());
        List<Product> products = cartRequest.getProductIds().stream().map(
                productRepository::getProductByProductId
        ).toList();
        if(products.get(0) == null){
            throw new AppException(errorType.notFoundRoleName);
        }
        cart.setProducts(new HashSet<>(products));
        cart.setUser(user.get());
        return mapper.mapToResponese(cartRespository.save(cart));
    }

    @Override
    public CartResponse updateCart(CartRequest cartRequest, String id) {
        Cart cartExist = cartRespository.findById(id).orElseThrow(
                () -> new AppException(errorType.notFound)
        );

        cartExist.setLocalDate(cartRequest.getLocalDate());
        cartExist.setStatus(cartRequest.isStatus());

        Optional<User> user = userRepository.findUSerByUsername(cartRequest.getUsername());
        if (user.isEmpty()) {
            throw new AppException(errorType.notFoundRoleName);
        }
        cartExist.setUser(user.get());

        List<Product> products = cartRequest.getProductIds().stream()
                .map(productRepository::getProductByProductId)
                .collect(Collectors.toList());

        if (products.contains(null)) {
            throw new AppException(errorType.notFoundRoleName);
        }
        cartExist.setProducts(new HashSet<>(products));

        Cart updatedCart = cartRespository.save(cartExist);

        return mapper.mapToResponese(updatedCart);
    }


    @Override
    public void deleteCart(String id) {
        Cart cartExist = cartRespository.findById(id).orElseThrow(
                ()-> new AppException(errorType.notFound)
        );
        cartRespository.deleteById(id);
    }
}
