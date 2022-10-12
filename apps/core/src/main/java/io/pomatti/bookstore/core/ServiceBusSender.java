package io.pomatti.bookstore.core;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.azure.core.util.BinaryData;
import com.azure.messaging.servicebus.ServiceBusClientBuilder;
import com.azure.messaging.servicebus.ServiceBusMessage;
import com.azure.messaging.servicebus.ServiceBusSenderClient;

public class ServiceBusSender {

  Logger logger = LoggerFactory.getLogger(ServiceBusSender.class);

  private final Map<String, ServiceBusSenderClient> senders = new HashMap<>();
  private final ServiceBusClientBuilder clientBuilder;

  public ServiceBusSender(String connectionString) {
    logger.info("Service Bus Sender ::: Initializing");
    this.clientBuilder = new ServiceBusClientBuilder()
        .connectionString(connectionString);
    logger.info("Service Bus Sender ::: Instantiated connection");
  }

  public void addAndInitSender(String queueName) {
    logger.info(String.format("Service Bus Sender ::: Initiating [%s] queue sender.", queueName));
    var sender = this.clientBuilder.sender()
        .queueName(queueName)
        .buildClient();
    senders.put(queueName, sender);
    logger.info(String.format("Service Bus Sender ::: Queue sender started for queue [%s].", queueName));
  }

  public void addAndInitSenderAsync(String queueName) {
    this.addAndInitSender(queueName);
    // CompletableFuture.runAsync(() -> this.addAndInitSender(queueName));
  }

  public void send(String queueName, String body) {
    senders.get(queueName).sendMessage(new ServiceBusMessage(body));
  }

  public void sendBatch(String queueName, List<String> body) {
    var sender = senders.get(queueName);
    var batch = sender.createMessageBatch();
    body.forEach(payload -> {
      var message = new ServiceBusMessage(BinaryData.fromString(payload));
      batch.tryAddMessage(message);
    });
    sender.sendMessages(batch);
  }

  public void close() {
    this.senders.values().parallelStream().forEach(sender -> sender.close());
  }

}
