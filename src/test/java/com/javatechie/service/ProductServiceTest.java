package com.javatechie.service;

import com.javatechie.entity.Product;
import com.javatechie.respository.ProductRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.concurrent.ConcurrentMapCacheManager;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Arrays;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class ProductServiceTest {

    @Mock
    private ProductRepository repository;

    @InjectMocks
    private ProductService service;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void getProductsReturnsRepositoryResults() {
        Product product = new Product(1, "item", 10, "description", "type");
        when(repository.findAll()).thenReturn(Arrays.asList(product));

        assertEquals(Arrays.asList(product), service.getProducts());
        verify(repository).findAll();
    }

    @Test
    void getProductByIdReturnsMatchingProduct() {
        Product product = new Product(1, "item", 10, "description", "type");
        when(repository.findById(1)).thenReturn(Optional.of(product));

        assertSame(product, service.getProductById(1));
        verify(repository).findById(1);
    }

    @Test
    void deleteProductDeletesAndReturnsRemainingCount() {
        when(repository.count()).thenReturn(2L);

        assertEquals(2L, service.deleteProduct(1));
        verify(repository).deleteById(1);
        verify(repository).count();
    }
    @Test
    void createCachesUsingGeneratedIdAndInvalidatesCachedList() {
        try (AnnotationConfigApplicationContext context =
                     new AnnotationConfigApplicationContext(CacheTestConfiguration.class)) {
            ProductRepository cacheRepository = context.getBean(ProductRepository.class);
            ProductService cachedService = context.getBean(ProductService.class);
            when(cacheRepository.findAll()).thenReturn(java.util.List.of());

            assertTrue(cachedService.getProducts().isEmpty());
            Product unsaved = new Product(0, "item", 10, "description", "type");
            Product persisted = new Product(42, "item", 10, "description", "type");
            when(cacheRepository.save(unsaved)).thenReturn(persisted);

            assertSame(persisted, cachedService.saveProduct(unsaved));
            assertSame(persisted, cachedService.getProductById(42));
            verify(cacheRepository, never()).findById(42);

            cachedService.getProducts();
            verify(cacheRepository, times(2)).findAll();
        }
    }

    @Test
    void updateChangesProductTypeAndRefreshesBothCaches() {
        try (AnnotationConfigApplicationContext context =
                     new AnnotationConfigApplicationContext(CacheTestConfiguration.class)) {
            ProductRepository cacheRepository = context.getBean(ProductRepository.class);
            ProductService cachedService = context.getBean(ProductService.class);
            Product existing = new Product(1, "old", 10, "description", "old-type");
            Product request = new Product(1, "new", 20, "updated", "new-type");
            when(cacheRepository.findById(1)).thenReturn(Optional.of(existing));
            when(cacheRepository.save(existing)).thenReturn(existing);
            when(cacheRepository.findAll()).thenReturn(java.util.List.of(existing));

            cachedService.getProducts();
            Product updated = cachedService.updateProduct(1, request);
            assertEquals("new-type", updated.getProductType());
            assertEquals("new", updated.getName());
            assertSame(updated, cachedService.getProductById(1));
            verify(cacheRepository, times(1)).findById(1);

            cachedService.getProducts();
            verify(cacheRepository, times(2)).findAll();
        }
    }

    @Test
    void deleteEvictsProductAndListEntries() {
        try (AnnotationConfigApplicationContext context =
                     new AnnotationConfigApplicationContext(CacheTestConfiguration.class)) {
            ProductRepository cacheRepository = context.getBean(ProductRepository.class);
            ProductService cachedService = context.getBean(ProductService.class);
            Product product = new Product(1, "item", 10, "description", "type");
            when(cacheRepository.findById(1)).thenReturn(Optional.of(product));
            when(cacheRepository.findAll()).thenReturn(java.util.List.of(product));

            cachedService.getProducts();
            cachedService.getProductById(1);
            cachedService.deleteProduct(1);
            cachedService.getProducts();
            cachedService.getProductById(1);

            verify(cacheRepository, times(2)).findAll();
            verify(cacheRepository, times(2)).findById(1);
            verify(cacheRepository).deleteById(1);
        }
    }

    @Configuration
    @EnableCaching
    static class CacheTestConfiguration {
        @Bean
        CacheManager cacheManager() {
            return new ConcurrentMapCacheManager("products");
        }

        @Bean
        ProductRepository productRepository() {
            return mock(ProductRepository.class);
        }

        @Bean
        ProductService productService() {
            return new ProductService();
        }
    }

}
