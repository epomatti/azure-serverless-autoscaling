package io.pomatti.bookstore.store.services.order;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import io.pomatti.bookstore.core.ResourceCore;

@RestController
@RequestMapping(value = "/api/orders")
public class OrderResource extends ResourceCore {

  @Autowired
  OrderService service;

  @PostMapping("/")
  public ResponseEntity<Order> post() {
    var order = service.createOrder();
    service.createDelivery(order);
    return created(order);
  }

}
