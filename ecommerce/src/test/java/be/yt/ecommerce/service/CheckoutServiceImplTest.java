package be.yt.ecommerce.service;

import be.yt.ecommerce.dao.CustomerRepository;
import be.yt.ecommerce.dto.Purchase;
import be.yt.ecommerce.dto.PurchaseResponse;
import be.yt.ecommerce.entity.Address;
import be.yt.ecommerce.entity.Customer;
import be.yt.ecommerce.entity.Order;
import be.yt.ecommerce.entity.OrderItem;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.HashSet;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CheckoutServiceImplTest {

    @Mock
    private CustomerRepository customerRepository;

    private CheckoutServiceImpl checkoutService;

    @BeforeEach
    void setUp() {
        checkoutService = new CheckoutServiceImpl(customerRepository);
    }

    @Test
    void placeOrder_ShouldGenerateOrderTrackingNumber() {
        // Arrange
        Purchase purchase = createSamplePurchase();
        
        // Act
        PurchaseResponse response = checkoutService.placeOrder(purchase);
        
        // Assert
        assertNotNull(response);
        assertNotNull(response.orderTrackingNumber());
        assertFalse(response.orderTrackingNumber().isEmpty());
    }
    
    @Test
    void placeOrder_ShouldSetOrderTrackingNumberOnOrder() {
        // Arrange
        Purchase purchase = createSamplePurchase();
        
        // Act
        checkoutService.placeOrder(purchase);
        
        // Assert
        Order order = purchase.getOrder();
        assertNotNull(order.getOrderTrackingNumber());
        assertFalse(order.getOrderTrackingNumber().isEmpty());
    }
    
    @Test
    void placeOrder_ShouldAddOrderItemsToOrder() {
        // Arrange
        Purchase purchase = createSamplePurchase();
        Order order = purchase.getOrder();
        Set<OrderItem> orderItems = purchase.getOrderItems();
        
        // Act
        checkoutService.placeOrder(purchase);
        
        // Assert
        assertEquals(orderItems.size(), order.getOrderItems().size());
        for (OrderItem item : orderItems) {
            assertEquals(order, item.getOrder());
        }
    }
    
    @Test
    void placeOrder_ShouldSetBillingAndShippingAddresses() {
        // Arrange
        Purchase purchase = createSamplePurchase();
        Address billingAddress = purchase.getBillingAddress();
        Address shippingAddress = purchase.getShippingAddress();
        
        // Act
        checkoutService.placeOrder(purchase);
        
        // Assert
        Order order = purchase.getOrder();
        assertEquals(billingAddress, order.getBillingAddress());
        assertEquals(shippingAddress, order.getShippingAddress());
    }
    
    @Test
    void placeOrder_ShouldAddOrderToCustomer() {
        // Arrange
        Purchase purchase = createSamplePurchase();
        Customer customer = purchase.getCustomer();
        Order order = purchase.getOrder();
        
        // Act
        checkoutService.placeOrder(purchase);
        
        // Assert
        assertTrue(customer.getOrders().contains(order));
        assertEquals(customer, order.getCustomer());
    }
    
    @Test
    void placeOrder_ShouldSaveCustomer() {
        // Arrange
        Purchase purchase = createSamplePurchase();
        Customer customer = purchase.getCustomer();
        
        // Act
        checkoutService.placeOrder(purchase);
        
        // Assert
        verify(customerRepository, times(1)).save(customer);
    }
    
    @Test
    void placeOrder_ShouldReturnOrderTrackingNumber() {
        // Arrange
        Purchase purchase = createSamplePurchase();
        
        // Act
        PurchaseResponse response = checkoutService.placeOrder(purchase);
        
        // Assert
        assertEquals(purchase.getOrder().getOrderTrackingNumber(), response.orderTrackingNumber());
    }

    private Purchase createSamplePurchase() {
        Customer customer = new Customer();
        customer.setFirstName("John");
        customer.setLastName("Doe");
        customer.setEmail("john.doe@example.com");
        
        Order order = new Order();
        order.setTotalPrice(new BigDecimal("100.00"));
        order.setTotalQuantity(2);
        order.setStatus("created");
        
        OrderItem item1 = new OrderItem();
        item1.setImageUrl("item1.jpg");
        item1.setQuantity(1);
        item1.setUnitPrice(new BigDecimal("50.00"));
        item1.setProductId(1L);
        
        OrderItem item2 = new OrderItem();
        item2.setImageUrl("item2.jpg");
        item2.setQuantity(1);
        item2.setUnitPrice(new BigDecimal("50.00"));
        item2.setProductId(2L);
        
        Set<OrderItem> orderItems = new HashSet<>();
        orderItems.add(item1);
        orderItems.add(item2);
        
        Address billingAddress = new Address();
        billingAddress.setCity("New York");
        billingAddress.setCountry("United States");
        billingAddress.setState("NY");
        billingAddress.setStreet("123 Billing St");
        billingAddress.setZipCode("10001");
        
        Address shippingAddress = new Address();
        shippingAddress.setCity("New York");
        shippingAddress.setCountry("United States");
        shippingAddress.setState("NY");
        shippingAddress.setStreet("456 Shipping St");
        shippingAddress.setZipCode("10002");
        
        Purchase purchase = new Purchase();
        purchase.setCustomer(customer);
        purchase.setOrder(order);
        purchase.setOrderItems(orderItems);
        purchase.setBillingAddress(billingAddress);
        purchase.setShippingAddress(shippingAddress);
        
        return purchase;
    }
}