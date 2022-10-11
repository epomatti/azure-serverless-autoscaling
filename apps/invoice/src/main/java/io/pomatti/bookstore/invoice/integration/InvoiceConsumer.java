package io.pomatti.bookstore.invoice.integration;

import java.util.function.Consumer;

import javax.annotation.PreDestroy;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;

import com.azure.messaging.servicebus.ServiceBusClientBuilder;
import com.azure.messaging.servicebus.ServiceBusErrorContext;
import com.azure.messaging.servicebus.ServiceBusProcessorClient;
import com.azure.messaging.servicebus.ServiceBusReceivedMessageContext;

import io.pomatti.bookstore.invoice.services.InvoiceService;

@Service
@Scope("singleton")
public class InvoiceConsumer {

  Logger logger = LoggerFactory.getLogger(InvoiceConsumer.class);

  @Autowired
  ApplicationContext context;

  @Autowired
  ServiceBusConfiguration config;

  private final static String ORDERS_QUEUE = "invoices-issue";

  private static ServiceBusProcessorClient processorClient;

  public void start() {
    Consumer<ServiceBusReceivedMessageContext> processMessage = messageContext -> {
      try {
        var payload = messageContext.getMessage().getBody().toString();
        var orderId = Long.parseLong(payload);
        var service = context.getBean(InvoiceService.class);
        service.createDelivery(orderId);
        messageContext.complete();
      } catch (Exception ex) {
        messageContext.abandon();
      }
    };

    Consumer<ServiceBusErrorContext> processError = errorContext -> {
      logger.error("Error occurred while receiving message", errorContext.getException());
    };

    processorClient = new ServiceBusClientBuilder()
        .connectionString(config.getConnectionString())
        .processor()
        .maxConcurrentCalls(config.getMaxConcurrentCalls())
        // .maxAutoLockRenewDuration(Duration.ofSeconds(60))
        .prefetchCount(config.getPrefetchCount())
        .queueName(ORDERS_QUEUE)
        .processMessage(processMessage)
        .processError(processError)
        .disableAutoComplete()
        .buildProcessorClient();

    processorClient.start();
  }

  @PreDestroy
  public void close() {
    logger.info("Closing Service Bus Processor");
    if (processorClient != null) {
      processorClient.close();
      logger.info("Closed Service Bus Processor");
    } else {
      logger.info("Service Bus processor was null");
    }
  }

}
