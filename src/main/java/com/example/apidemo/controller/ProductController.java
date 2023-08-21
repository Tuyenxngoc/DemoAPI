package com.example.apidemo.controller;

import com.example.apidemo.models.Product;
import com.example.apidemo.models.ResponseObject;
import com.example.apidemo.repositories.ProductRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/products")
public class ProductController {
    @Autowired
    private ProductRepository productRepository;

    private static final Logger logger = LoggerFactory.getLogger(Product.class);

    @GetMapping("")
    public ResponseEntity<ResponseObject> getAllProducts() {
        List<Product> foundProduct = productRepository.findAll();
        return ResponseEntity.status(HttpStatus.OK).body(new ResponseObject("ok", "Query all products successfully", foundProduct));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ResponseObject> findById(@PathVariable Long id) {
        Optional<Product> foundProduct = productRepository.findById(id);
        if (foundProduct.isPresent()) {
            return ResponseEntity
                    .status(HttpStatus.OK)
                    .body(new ResponseObject("ok", "Query product successfully", foundProduct));
        } else {
            return ResponseEntity
                    .status(HttpStatus.NOT_FOUND)
                    .body(new ResponseObject("failed", "Cannot find product with id = " + id, null));
        }
    }

    @PostMapping("/insert")
    public ResponseEntity<ResponseObject> insertProduct(@RequestBody Product product) {
        Optional<Product> foundProduct = productRepository.findByProductName(product.getProductName().trim());
        if (foundProduct.isPresent()) {
            return ResponseEntity.status(HttpStatus.NOT_IMPLEMENTED).body(new ResponseObject("failed", "Product name already exists", null));
        }
        return ResponseEntity.status(HttpStatus.OK).body(new ResponseObject("ok", "Insert product successfully", productRepository.save(product)));
    }

    @PutMapping("{id}")
    public ResponseEntity<ResponseObject> updateProduct(@RequestBody Product newproduct, @PathVariable Long id) {
        Optional<Product> existingProductOptional = productRepository.findById(id);

        if (existingProductOptional.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ResponseObject("failed", "Product not found", null));
        }

        Product existingProduct = existingProductOptional.get();

        if (newproduct.getProductName() != null) {
            existingProduct.setProductName(newproduct.getProductName());
        }
        if (newproduct.getYear() > 0) {
            existingProduct.setYear(newproduct.getYear());
        }
        if (newproduct.getPrice() > 0) {
            existingProduct.setPrice(newproduct.getPrice());
        }
        if (newproduct.getUrl() != null) {
            existingProduct.setUrl(newproduct.getUrl());
        }

        // Kiểm tra nếu sản phẩm đã tồn tại với tên đã cập nhật
        Optional<Product> foundProduct = productRepository.findByProductName(existingProduct.getProductName().trim());
        if (foundProduct.isPresent() && !foundProduct.get().getId().equals(existingProduct.getId())) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ResponseObject("failed", "Product name already exists", null));
        }

        Product updatedProduct = productRepository.save(existingProduct);
        return ResponseEntity.status(HttpStatus.OK).body(new ResponseObject("ok", "Update product successfully", updatedProduct));
    }


    @DeleteMapping("/{id}")
    public ResponseEntity<ResponseObject> deleteProduct(@PathVariable Long id) {
        boolean exists = productRepository.existsById(id);
        if (exists) {
            productRepository.deleteById(id);
            return ResponseEntity.status(HttpStatus.OK).body(new ResponseObject("ok", "Delete product successfully", null));
        } else {
            return ResponseEntity.status(HttpStatus.NOT_IMPLEMENTED).body(new ResponseObject("failed", "Cannot find product with id = " + id, null));
        }
    }

    @DeleteMapping()
    public ResponseEntity<ResponseObject> deleteAllProducts() {
        productRepository.deleteAll();
        return ResponseEntity.status(HttpStatus.OK).body(new ResponseObject("ok", "Delete all products successfully", null));
    }
}
