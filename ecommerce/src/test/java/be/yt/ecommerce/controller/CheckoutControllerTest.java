package be.yt.ecommerce.controller;

import be.yt.ecommerce.dto.Purchase;
import be.yt.ecommerce.dto.PurchaseResponse;
import be.yt.ecommerce.service.CheckoutService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
class CheckoutControllerTest {

    private MockMvc mockMvc;
    private ObjectMapper objectMapper;

    @Mock
    private CheckoutService checkoutService;
    
    @InjectMocks
    private CheckoutController checkoutController;
    
    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(checkoutController).build();
        objectMapper = new ObjectMapper();
    }

    @Test
    void placeOrder_ShouldReturnPurchaseResponse() throws Exception {
        // Arrange
        Purchase purchase = new Purchase();
        String orderTrackingNumber = "27b95829-4f3f-4ddf-8983-151ba010e35b";
        PurchaseResponse expectedResponse = new PurchaseResponse(orderTrackingNumber);
        
        when(checkoutService.placeOrder(any(Purchase.class))).thenReturn(expectedResponse);

        // Act & Assert
        mockMvc.perform(post("/api/checkout/purchase")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(purchase)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.orderTrackingNumber").value(orderTrackingNumber));
    }
    
    @Test
    void placeOrder_ShouldDelegateToCheckoutService() throws Exception {
        // Arrange
        Purchase purchase = new Purchase();
        String orderTrackingNumber = "27b95829-4f3f-4ddf-8983-151ba010e35b";
        
        when(checkoutService.placeOrder(any(Purchase.class)))
                .thenReturn(new PurchaseResponse(orderTrackingNumber));

        // Act & Assert
        mockMvc.perform(post("/api/checkout/purchase")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(purchase)))
                .andExpect(status().isOk());
    }
}