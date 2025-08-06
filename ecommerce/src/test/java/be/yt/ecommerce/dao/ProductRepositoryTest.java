package be.yt.ecommerce.dao;

import be.yt.ecommerce.entity.Product;
import be.yt.ecommerce.entity.ProductCategory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
class ProductRepositoryTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private ProductRepository productRepository;

    private ProductCategory category1;
    private ProductCategory category2;

    @BeforeEach
    void setUp() {
        // Create product categories
        category1 = new ProductCategory();
        category1.setCategoryName("Electronics");
        entityManager.persist(category1);

        category2 = new ProductCategory();
        category2.setCategoryName("Books");
        entityManager.persist(category2);

        // Create products in category 1
        createProduct("P001", "Smartphone", "High-end smartphone", new BigDecimal("799.99"), "smartphone.jpg", true, 100, category1);
        createProduct("P002", "Laptop", "Powerful laptop", new BigDecimal("1299.99"), "laptop.jpg", true, 50, category1);
        createProduct("P003", "Tablet", "Portable tablet", new BigDecimal("499.99"), "tablet.jpg", true, 75, category1);
        
        // Create products in category 2
        createProduct("B001", "Java Programming", "Learn Java programming", new BigDecimal("39.99"), "java-book.jpg", true, 200, category2);
        createProduct("B002", "Spring Boot Guide", "Guide to Spring Boot", new BigDecimal("44.99"), "spring-book.jpg", true, 150, category2);

        entityManager.flush();
    }

    @Test
    void findByCategoryId_ShouldReturnProductsInCategory() {
        // Act
        Page<Product> result = productRepository.findByCategoryId(category1.getId(), PageRequest.of(0, 10));
        
        // Assert
        assertEquals(3, result.getTotalElements());
        List<String> productNames = result.getContent().stream()
                .map(Product::getName)
                .toList();
        assertTrue(productNames.containsAll(Arrays.asList("Smartphone", "Laptop", "Tablet")));
    }

    @Test
    void findByCategoryId_ShouldReturnEmptyPage_WhenNoProductsInCategory() {
        // Create a new category with no products
        ProductCategory emptyCategory = new ProductCategory();
        emptyCategory.setCategoryName("Empty Category");
        entityManager.persist(emptyCategory);
        
        // Act
        Page<Product> result = productRepository.findByCategoryId(emptyCategory.getId(), PageRequest.of(0, 10));
        
        // Assert
        assertEquals(0, result.getTotalElements());
        assertTrue(result.getContent().isEmpty());
    }

    @Test
    void findByNameContaining_ShouldReturnMatchingProducts() {
        // Act
        Page<Product> result = productRepository.findByNameContaining("top", PageRequest.of(0, 10));
        
        // Assert
        assertEquals(1, result.getTotalElements());
        assertEquals("Laptop", result.getContent().get(0).getName());
    }

    @Test
    void findByNameContaining_ShouldFindPartialMatches() {
        // Act
        Page<Product> result = productRepository.findByNameContaining("mart", PageRequest.of(0, 10));
        
        // Assert
        assertEquals(1, result.getTotalElements());
        assertEquals("Smartphone", result.getContent().get(0).getName());
    }

    @Test
    void findByNameContaining_ShouldReturnMultipleProducts_WhenMultipleMatches() {
        // Act
        Page<Product> result = productRepository.findByNameContaining("o", PageRequest.of(0, 10));
        
        // Assert
        assertTrue(result.getTotalElements() > 1);
        List<String> productNames = result.getContent().stream()
                .map(Product::getName)
                .toList();
        assertTrue(productNames.contains("Smartphone"));
        assertTrue(productNames.contains("Laptop"));
    }

    @Test
    void findByNameContaining_ShouldReturnEmptyPage_WhenNoMatches() {
        // Act
        Page<Product> result = productRepository.findByNameContaining("nonexistent", PageRequest.of(0, 10));
        
        // Assert
        assertEquals(0, result.getTotalElements());
        assertTrue(result.getContent().isEmpty());
    }

    private Product createProduct(String sku, String name, String description, BigDecimal price, 
                                 String imageUrl, boolean active, int stock, ProductCategory category) {
        Product product = new Product();
        product.setSku(sku);
        product.setName(name);
        product.setDescription(description);
        product.setUnitPrice(price);
        product.setImageUrl(imageUrl);
        product.setActive(active);
        product.setUnitsInStock(stock);
        product.setCategory(category);
        
        return entityManager.persist(product);
    }
}