package io.pomatti.bookstore.store.integration;

import javax.annotation.PreDestroy;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import io.pomatti.bookstore.core.ServiceBusSender;
import io.pomatti.bookstore.store.config.OrderConfiguration;
import io.pomatti.bookstore.store.services.order.Order;

@Service
@Scope("singleton")
public class OrderSender {

  Logger logger = LoggerFactory.getLogger(OrderSender.class);

  @Autowired
  OrderConfiguration config;

  private ServiceBusSender sender;

  private final static String CREATE_INVOICE_QUEUE = "create-invoices";

  public OrderSender() {
    this.sender = new ServiceBusSender(config.getConnectionString());
    sender.addAndInitSenderAsync(CREATE_INVOICE_QUEUE);
  }

  public void sendCreateInvoicesEvents(Order order) throws RuntimeException {
    var event = CreateInvoiceEvent.fromOrder(order);
    ObjectMapper objectMapper = new ObjectMapper();
    String body;
    try {
      body = objectMapper.writeValueAsString(event);
    } catch (JsonProcessingException e) {
      throw new RuntimeException(e);
    }
    sender.send(CREATE_INVOICE_QUEUE, body);
  }

  @PreDestroy
  public void close() {
    sender.close();
  }

}
