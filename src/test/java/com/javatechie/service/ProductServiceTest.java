package com.javatechie.service;

import com.javatechie.entity.Product;
import com.javatechie.respository.ProductRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

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
}
