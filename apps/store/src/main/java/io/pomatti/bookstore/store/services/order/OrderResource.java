package io.pomatti.bookstore.store.services.order;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import io.pomatti.bookstore.core.ResourceCore;

@RestController
@RequestMapping(value = "/api/orders")
public class OrderResource extends ResourceCore {

  @Autowired
  OrderService service;

  @PostMapping("/")
  public ResponseEntity<Order> post(@RequestBody CreateOrderRequest request) {
    var order = service.createOrder(request.getBooks());
    service.createInvoices(order);
    return created(order);
  }

}
