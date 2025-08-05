package be.yt.ecommerce.dto;

import be.yt.ecommerce.entity.Address;
import be.yt.ecommerce.entity.Customer;
import be.yt.ecommerce.entity.Order;
import be.yt.ecommerce.entity.OrderItem;
import lombok.Data;

import java.util.Set;

@Data
public class Purchase {
    private Customer customer;
    private Address shippingAddress;
    private Address billingAddress;
    private Order order;
    private Set<OrderItem> orderItems;
}
