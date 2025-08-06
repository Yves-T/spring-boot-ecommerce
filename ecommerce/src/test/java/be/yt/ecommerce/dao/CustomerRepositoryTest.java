package be.yt.ecommerce.dao;

import be.yt.ecommerce.entity.Customer;
import be.yt.ecommerce.entity.Order;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
class CustomerRepositoryTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private CustomerRepository customerRepository;

    @Test
    void save_ShouldPersistCustomer() {
        // Arrange
        Customer customer = new Customer();
        customer.setFirstName("John");
        customer.setLastName("Doe");
        customer.setEmail("john.doe@example.com");

        // Act
        Customer savedCustomer = customerRepository.save(customer);

        // Assert
        assertNotNull(savedCustomer.getId());
        Customer foundCustomer = entityManager.find(Customer.class, savedCustomer.getId());
        assertEquals("John", foundCustomer.getFirstName());
        assertEquals("Doe", foundCustomer.getLastName());
        assertEquals("john.doe@example.com", foundCustomer.getEmail());
    }

    @Test
    void findById_ShouldReturnCustomer_WhenCustomerExists() {
        // Arrange
        Customer customer = new Customer();
        customer.setFirstName("Jane");
        customer.setLastName("Smith");
        customer.setEmail("jane.smith@example.com");
        Long id = entityManager.persistAndGetId(customer, Long.class);

        // Act
        Optional<Customer> result = customerRepository.findById(id);

        // Assert
        assertTrue(result.isPresent());
        assertEquals("Jane", result.get().getFirstName());
        assertEquals("Smith", result.get().getLastName());
        assertEquals("jane.smith@example.com", result.get().getEmail());
    }

    @Test
    void findById_ShouldReturnEmptyOptional_WhenCustomerDoesNotExist() {
        // Act
        Optional<Customer> result = customerRepository.findById(999L);

        // Assert
        assertFalse(result.isPresent());
    }

    @Test
    void save_ShouldPersistCustomerWithOrders() {
        // Arrange
        Customer customer = new Customer();
        customer.setFirstName("Alice");
        customer.setLastName("Johnson");
        customer.setEmail("alice.johnson@example.com");

        Order order = new Order();
        order.setStatus("created");
        order.setTotalQuantity(2);
        customer.add(order);

        // Act
        Customer savedCustomer = customerRepository.save(customer);
        
        // Clear persistence context to force a database read
        entityManager.clear();

        // Assert
        Customer foundCustomer = customerRepository.findById(savedCustomer.getId()).orElseThrow();
        assertNotNull(foundCustomer.getOrders());
        assertEquals(1, foundCustomer.getOrders().size());
        
        Order foundOrder = foundCustomer.getOrders().iterator().next();
        assertEquals("created", foundOrder.getStatus());
        assertEquals(2, foundOrder.getTotalQuantity());
        assertEquals(foundCustomer, foundOrder.getCustomer());
    }
}