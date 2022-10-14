package io.pomatti.bookstore.invoice.integration;

import java.util.List;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import io.pomatti.bookstore.core.ServiceBusSender;
import io.pomatti.bookstore.invoice.config.InvoiceConfiguration;

@Service
@Scope("singleton")
public class InvoiceSender {

  Logger logger = LoggerFactory.getLogger(InvoiceSender.class);

  @Autowired
  InvoiceConfiguration config;

  private ServiceBusSender sender;

  private final static String INVOICE_PROCESS_QUEUE = "process-invoices";
  private final static String INVOICE_READY_QUEUE = "invoices-ready";

  public void start() {
    this.sender = new ServiceBusSender(config.getConnectionString());
    sender.addAndInitSenderAsync(INVOICE_PROCESS_QUEUE);
    sender.addAndInitSenderAsync(INVOICE_READY_QUEUE);
  }

  public void sendInvoicesReadyEvent(Long orderId) {
    sender.send(INVOICE_READY_QUEUE, Long.toString(orderId));
  }

  public void sendProcessesInvoicesEvent(List<Long> invoicesIds) {
    List<String> strings = invoicesIds.stream().map(Object::toString)
        .collect(Collectors.toUnmodifiableList());
    sender.sendBatch(INVOICE_PROCESS_QUEUE, strings);
  }

  protected String fromObjectToJson(Object object) {
    ObjectMapper objectMapper = new ObjectMapper();
    try {
      return objectMapper.writeValueAsString(object);
    } catch (JsonProcessingException e) {
      throw new RuntimeException(e);
    }
  }

  public void close() {
    sender.close();
  }

}
