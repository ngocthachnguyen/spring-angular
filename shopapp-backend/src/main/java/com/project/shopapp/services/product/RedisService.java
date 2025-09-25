package com.project.shopapp.services.product;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.project.shopapp.models.Product;

import java.util.List;

public interface RedisService {
    public void cacheProduct(Product product) throws JsonProcessingException;
    public Product getProduct(Long id) throws JsonProcessingException;
    public void cacheProductPage(int page, int limit, List<Product> products) throws JsonProcessingException;
    public List<Long> getProductIdsForPage(int page, int limit);
}
