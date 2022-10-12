package io.pomatti.bookstore.store.services.order;

import java.util.List;

import lombok.Data;

@Data
public class CreateOrderRequest {

  private List<Long> books;

}
