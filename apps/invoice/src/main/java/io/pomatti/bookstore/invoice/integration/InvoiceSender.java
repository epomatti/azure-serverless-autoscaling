package io.pomatti.bookstore.invoice.integration;

import java.util.List;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;

import io.pomatti.bookstore.core.ServiceBusSender;

@Service
@Scope("singleton")
public class InvoiceSender {

  Logger logger = LoggerFactory.getLogger(InvoiceSender.class);

  @Autowired
  ServiceBusConfiguration config;

  private ServiceBusSender sender;

  private final static String INVOICE_PROCESS_QUEUE = "invoice-process";
  private final static String INVOICE_READY_QUEUE = "invoice-ready";

  public InvoiceSender() {
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

  // private List<ServiceBusSenderClient> senders = new ArrayList<>();

  // private ServiceBusSenderClient invoiceProcessSenderClient;
  // private ServiceBusSenderClient invoiceReadySenderClient;

  // private ServiceBusClientBuilder clientBuilder;

  // public void start() {
  // logger.info("Creating Service Bus client builder...");
  // createClientBuilder();
  // logger.info("Starting senders...");
  // CompletableFuture<Void> cf1 = CompletableFuture.runAsync(() ->
  // this.startInvoiceProcessSender());
  // CompletableFuture<Void> cf2 = CompletableFuture.runAsync(() ->
  // this.startInvoiceReadySender());
  // CompletableFuture.allOf(cf1, cf2).join();
  // logger.info("Senders started");
  // }

  // private void startInvoiceProcessSender() {
  // this.invoiceProcessSenderClient = buildSender(INVOICE_PROCESS_QUEUE);
  // }

  // private void startInvoiceReadySender() {
  // this.invoiceReadySenderClient = buildSender(INVOICE_READY_QUEUE);
  // }

  // private ServiceBusSenderClient buildSender(String queueName) {
  // return this.clientBuilder.sender()
  // .queueName(queueName)
  // .buildClient();
  // }

  // private void createClientBuilder() {
  // this.clientBuilder = new ServiceBusClientBuilder()
  // .connectionString(config.getConnectionString());
  // }

  // public void sendInvoiceConfirmation(Long orderId) {
  // invoiceReadySenderClient.sendMessage(new
  // ServiceBusMessage(orderId.toString()));
  // }

  // public void sendStartInvoiceProcess(Long orderId) {
  // invoiceProcessSenderClient.sendMessage(new
  // ServiceBusMessage(orderId.toString()));
  // }

  public void close() {
    sender.close();
  }

}
