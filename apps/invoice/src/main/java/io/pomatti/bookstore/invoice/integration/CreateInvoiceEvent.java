package io.pomatti.bookstore.invoice.integration;

import java.util.List;

import lombok.Data;

@Data
public class CreateInvoiceEvent {

  private Long orderId;
  private List<Long> items;

}