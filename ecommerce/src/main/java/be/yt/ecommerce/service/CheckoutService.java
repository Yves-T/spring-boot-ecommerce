package be.yt.ecommerce.service;

import be.yt.ecommerce.dto.Purchase;
import be.yt.ecommerce.dto.PurchaseResponse;

public interface CheckoutService {
    PurchaseResponse placeOrder(Purchase purchase);
}
