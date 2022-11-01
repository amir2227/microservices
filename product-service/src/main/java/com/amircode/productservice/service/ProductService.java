package com.amircode.productservice.service;

import com.amircode.productservice.dto.ProductRequest;
import com.amircode.productservice.dto.ProductResponse;
import com.amircode.productservice.model.Product;
import com.amircode.productservice.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class ProductService {

    private final ProductRepository productRepository;

    public ProductResponse create(ProductRequest request){
        Product product = Product.builder()
                .name(request.name())
                .description(request.description())
                .price(request.price())
                .build();
        productRepository.save(product);
        log.info("product with id {} saved!",product.getId());
        return mapToProductResponse(product);
    }
    public List<ProductResponse> getAll(){
        List<Product> products = productRepository.findAll();
        // products.stream().map(product -> mapToProductResponse(product)).toList();
        return products.stream().map(this::mapToProductResponse).toList();
    }
    public ProductResponse get(String id){
        Product product = productRepository.findById(id).orElseThrow(()-> new RuntimeException("Product not found"));
        return mapToProductResponse(product);
    }
    public ProductResponse mapToProductResponse(Product product){
        return ProductResponse.builder()
                .id(product.getId())
                .name(product.getName())
                .description(product.getDescription())
                .price(product.getPrice())
                .build();
    }
}
