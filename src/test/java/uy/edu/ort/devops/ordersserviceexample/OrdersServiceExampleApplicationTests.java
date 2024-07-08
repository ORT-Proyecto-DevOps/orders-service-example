package uy.edu.ort.devops.ordersserviceexample;

import java.nio.charset.Charset;
import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.assertEquals;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import uy.edu.ort.devops.ordersserviceexample.domain.OrderStatus;
import uy.edu.ort.devops.ordersserviceexample.dtos.PaymentStatus;
import uy.edu.ort.devops.ordersserviceexample.dtos.Product;
import uy.edu.ort.devops.ordersserviceexample.logic.OrdersLogic;

public class OrdersServiceExampleApplicationTests {

 	@InjectMocks
    private OrdersLogic ordersLogic;

    @Mock
    private RestTemplate restTemplate;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        OrdersLogic.setPaymentsServiceUrl("http://mocked/payments");
        OrdersLogic.setShippingServiceUrl("http://mocked/shipping");
        OrdersLogic.setProductsServiceUrl("http://mocked/products");
    }

	@Test
    public void testBuy_successfulOrder() {
        // Mocking a product with stock
        Product product = new Product("1", "Product1", 10, "Description1");
        when(restTemplate.getForObject(anyString(), eq(Product.class))).thenReturn(product);

        // Mocking successful payment
        PaymentStatus paymentStatus = new PaymentStatus("orderId1", true, "Payment successful");
        when(restTemplate.postForObject(anyString(), any(), eq(PaymentStatus.class))).thenReturn(paymentStatus);

        // Executing the order logic
        OrderStatus orderStatus = ordersLogic.buy(Arrays.asList("1", "2"));

        // Verifying the results
        assertEquals(true, orderStatus.isSuccess());
        assertEquals("Ok.", orderStatus.getDescription());

        // Verifying the method calls
        verify(restTemplate, times(2)).getForObject(anyString(), eq(Product.class));
        verify(restTemplate).postForObject(anyString(), any(), eq(PaymentStatus.class));
        verify(restTemplate).postForEntity(anyString(), any(), eq(String.class));
    }

    @Test
    public void testBuy_outOfStock() {
        // Mocking a product with no stock
        Product product = new Product("1", "Product1", 0, "Description1");
        when(restTemplate.getForObject(anyString(), eq(Product.class))).thenReturn(product);

        // Executing the order logic
        OrderStatus orderStatus = ordersLogic.buy(Arrays.asList("1"));

        // Verifying the results
        assertEquals(false, orderStatus.isSuccess());
        assertEquals("No stock: 1.", orderStatus.getDescription());

        // Verifying the method calls
        verify(restTemplate).getForObject(anyString(), eq(Product.class));
        verify(restTemplate, never()).postForObject(anyString(), any(), eq(PaymentStatus.class));
        verify(restTemplate, never()).postForEntity(anyString(), any(), eq(String.class));
    }

    @Test
    public void testBuy_paymentFailure() {
        // Mocking a product with stock
        Product product = new Product("1", "Product1", 10, "Description1");
        when(restTemplate.getForObject(anyString(), eq(Product.class))).thenReturn(product);

        // Mocking payment failure
        PaymentStatus paymentStatus = new PaymentStatus("orderId1", false, "Payment declined.");
        when(restTemplate.postForObject(anyString(), any(), eq(PaymentStatus.class))).thenReturn(paymentStatus);

        // Executing the order logic
        OrderStatus orderStatus = ordersLogic.buy(Arrays.asList("1"));

        // Verifying the results
        assertEquals(false, orderStatus.isSuccess());
        assertEquals("Payment declined.", orderStatus.getDescription());

        // Verifying the method calls
        verify(restTemplate).getForObject(anyString(), eq(Product.class));
        verify(restTemplate).postForObject(anyString(), any(), eq(PaymentStatus.class));
        verify(restTemplate, never()).postForEntity(anyString(), any(), eq(String.class));
    }

    @Test
    public void testBuy_missingProduct() {
       // Mocking product not found (HTTP 404)
        when(restTemplate.getForObject(anyString(), eq(Product.class))).thenThrow(new HttpClientErrorException(
                HttpStatus.NOT_FOUND, "Product not found", HttpHeaders.EMPTY, null, Charset.defaultCharset()));

        // Executing the order logic
        OrderStatus orderStatus = ordersLogic.buy(Arrays.asList("1"));

        // Verifying the results
        assertEquals(false, orderStatus.isSuccess());
        assertEquals("Missing: 1.", orderStatus.getDescription());

        // Verifying the method calls
        verify(restTemplate).getForObject(anyString(), eq(Product.class));
        verify(restTemplate, never()).postForObject(anyString(), any(), eq(PaymentStatus.class));
        verify(restTemplate, never()).postForEntity(anyString(), any(), eq(String.class));
    }
}
