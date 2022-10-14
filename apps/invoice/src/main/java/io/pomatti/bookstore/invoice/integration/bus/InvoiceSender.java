package io.pomatti.bookstore.invoice.integration.bus;

import java.util.List;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;

import io.pomatti.bookstore.core.ServiceBusSender;
import io.pomatti.bookstore.invoice.config.InvoiceConfiguration;

@Service
@Scope("singleton")
public class InvoiceSender {

  Logger logger = LoggerFactory.getLogger(InvoiceSender.class);

  @Autowired
  InvoiceConfiguration config;

  private ServiceBusSender sender;

  public void start() {
    this.sender = new ServiceBusSender(config.getConnectionString());
    sender.addAndInitSenderAsync(EventQueues.AUTHORIZE_INVOICE_QUEUE);
    sender.addAndInitSenderAsync(EventQueues.INVOICE_READY_QUEUE);
  }

  public void sendInvoicesReadyEvent(Long orderId) {
    sender.send(EventQueues.INVOICE_READY_QUEUE, Long.toString(orderId));
  }

  public void sendAuthorizeInvoiceEvent(List<Long> invoicesIds) {
    List<String> strings = invoicesIds.stream().map(Object::toString)
        .collect(Collectors.toUnmodifiableList());
    sender.sendBatch(EventQueues.AUTHORIZE_INVOICE_QUEUE, strings);
  }

  public void close() {
    sender.close();
  }

}
