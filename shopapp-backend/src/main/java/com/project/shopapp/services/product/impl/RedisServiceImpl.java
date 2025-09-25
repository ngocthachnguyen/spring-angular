package com.project.shopapp.services.product.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.project.shopapp.models.Product;
import com.project.shopapp.services.product.RedisService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class RedisServiceImpl implements RedisService {

    private final ObjectMapper objectMapper;
    private final RedisTemplate<String, Object> redisTemplate;
    private static Duration TTL = Duration.ofMinutes(5);
    private static String PRODUCT = "product:";
    // private final RedisTemplate redisTemplate1;

    @Override
    public void cacheProduct(Product product) throws JsonProcessingException {
        String key = PRODUCT + product.getId();
        redisTemplate.opsForValue().set(key, objectMapper.writeValueAsString(product), TTL);
    }

    @Override
    public Product getProduct(Long id) throws JsonProcessingException {
        String key = PRODUCT + id;
        String json = redisTemplate.opsForValue().get(key).toString();
        if (json == null) return null;
        return objectMapper.readValue(json, Product.class);
    }

    @Override
    public void cacheProductPage(int page, int limit, List<Product> products) throws JsonProcessingException {
        String pageKey = PRODUCT+":page:" + page + ":" + limit;
        List<String> ids = products.stream()
                .map(p -> String.valueOf(p.getId()))
                .collect(Collectors.toList());
        redisTemplate.opsForList().rightPushAll(pageKey, ids);
        redisTemplate.expire(pageKey, TTL);
        for(Product p: products ) {
            cacheProduct(p);
        }
    }

    @Override
    public List<Long> getProductIdsForPage(int page, int limit) {
        String pageKey = PRODUCT + ":page:" + page + ":" + limit;
        List<Object> ids = redisTemplate.opsForList().range(pageKey, 0, -1);
        return ids.stream()
                .filter(Objects::nonNull)
                .map(obj -> {
                    try {
                        if (obj instanceof Long) return (Long) obj;
                        if (obj instanceof Integer) return ((Integer) obj).longValue();
                        return Long.valueOf(obj.toString().trim());
                    } catch (NumberFormatException e) {
                        // Ghi log hoặc xử lý lỗi nếu cần
                        return null;
                    }
                })
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
    }
}
