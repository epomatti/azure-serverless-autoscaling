package io.pomatti.bookstore.delivery.services;

import java.time.LocalDateTime;
import java.util.function.Consumer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Service;

import com.azure.messaging.servicebus.ServiceBusClientBuilder;
import com.azure.messaging.servicebus.ServiceBusErrorContext;
import com.azure.messaging.servicebus.ServiceBusProcessorClient;
import com.azure.messaging.servicebus.ServiceBusReceivedMessageContext;

@Service
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

  public void start() {
    Consumer<ServiceBusReceivedMessageContext> processMessage = messageContext -> {
      try {
        var payload = messageContext.getMessage().getBody().toString();
        var orderId = Long.parseLong(payload);
        var repository = context.getBean(DeliveryRepository.class);
        var delivery = buildDelivery(orderId);
        repository.save(delivery);
        messageContext.complete();
      } catch (Exception ex) {
        messageContext.abandon();
      }
    };

    Consumer<ServiceBusErrorContext> processError = errorContext -> {
      System.err.println("Error occurred while receiving message: " + errorContext.getException());
    };

    ServiceBusProcessorClient processorClient = new ServiceBusClientBuilder()
        .connectionString(connectionString)
        .processor()
        .maxConcurrentCalls(maxConcurrentCalls)
        .prefetchCount(prefetchCount)
        .queueName(ORDERS_QUEUE)
        .processMessage(processMessage)
        .processError(processError)
        .disableAutoComplete()
        .buildProcessorClient();

    processorClient.start();
  }

  protected Delivery buildDelivery(Long orderId) {
    var delivery = new Delivery();

    delivery.setOrderId(orderId);

    delivery.setExtraString1("abcdefghijk");
    delivery.setExtraString2("abcdefghijk");
    delivery.setExtraString3("abcdefghijk");
    delivery.setExtraString4("abcdefghijk");
    delivery.setExtraString5("abcdefghijk");
    delivery.setExtraString6("abcdefghijk");
    delivery.setExtraString7("abcdefghijk");
    delivery.setExtraString8("abcdefghijk");
    delivery.setExtraString9("abcdefghijk");
    delivery.setExtraString10("abcdefghijk");

    delivery.setExtraLong1(1000L);
    delivery.setExtraLong2(1000L);
    delivery.setExtraLong3(1000L);
    delivery.setExtraLong4(1000L);
    delivery.setExtraLong5(1000L);
    delivery.setExtraLong6(1000L);

    delivery.setExtraDateTime1(LocalDateTime.now());
    delivery.setExtraDateTime2(LocalDateTime.now());
    delivery.setExtraDateTime3(LocalDateTime.now());
    delivery.setExtraDateTime4(LocalDateTime.now());
    delivery.setExtraDateTime5(LocalDateTime.now());
    delivery.setExtraDateTime6(LocalDateTime.now());

    return delivery;
  }

}
