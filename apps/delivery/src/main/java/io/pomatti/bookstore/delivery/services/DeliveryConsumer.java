package io.pomatti.bookstore.delivery.services;

import java.util.function.Consumer;

import javax.annotation.PreDestroy;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;

import com.azure.messaging.servicebus.ServiceBusClientBuilder;
import com.azure.messaging.servicebus.ServiceBusErrorContext;
import com.azure.messaging.servicebus.ServiceBusProcessorClient;
import com.azure.messaging.servicebus.ServiceBusReceivedMessageContext;

@Service
@Scope("singleton")
public class DeliveryConsumer {

  Logger logger = LoggerFactory.getLogger(DeliveryConsumer.class);

  @Autowired
  ApplicationContext context;

  @Value("${azure.servicebus.connectionstring}")
  private String connectionString;

  @Value("${azure.servicebus.prefetchCount}")
  private Integer prefetchCount;

  @Value("${azure.servicebus.maxConcurrentCalls}")
  private Integer maxConcurrentCalls;

  private final static String ORDERS_QUEUE = "orders";

  private static ServiceBusProcessorClient processorClient;

  public void start() {
    Consumer<ServiceBusReceivedMessageContext> processMessage = messageContext -> {
      try {
        var payload = messageContext.getMessage().getBody().toString();
        var orderId = Long.parseLong(payload);
        var service = context.getBean(DeliveryService.class);
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
        .connectionString(connectionString)
        .processor()
        .maxConcurrentCalls(maxConcurrentCalls)
        // .maxAutoLockRenewDuration(Duration.ofSeconds(60))
        .prefetchCount(prefetchCount)
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
