package io.pomatti.bookstore.store.integration;

import java.util.ArrayList;
import java.util.List;

import io.pomatti.bookstore.store.services.order.Order;
import lombok.Data;

@Data
public class CreateInvoiceEvent {

  private Long orderId;
  private List<Long> items = new ArrayList<>();

  protected void addItem(Long itemId) {
    items.add(itemId);
  }

  public static CreateInvoiceEvent fromOrder(Order order) {
    var event = new CreateInvoiceEvent();
    event.setOrderId(order.getId());
    order.getItems().stream().forEach(item -> event.addItem(item.getBookCode()));
    return event;
  }

}
