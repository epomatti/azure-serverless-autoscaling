package io.pomatti.bookstore.core;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

import com.azure.core.util.BinaryData;
import com.azure.messaging.servicebus.ServiceBusClientBuilder;
import com.azure.messaging.servicebus.ServiceBusMessage;
import com.azure.messaging.servicebus.ServiceBusSenderClient;

public class ServiceBusSender {

  private final Map<String, ServiceBusSenderClient> senders = new HashMap<>();
  private final ServiceBusClientBuilder clientBuilder;

  public ServiceBusSender(String connectionString) {
    this.clientBuilder = new ServiceBusClientBuilder()
        .connectionString(connectionString);
  }

  public void addAndInitSender(String queueName) {
    var sender = this.clientBuilder.sender()
        .queueName(queueName)
        .buildClient();
    senders.put(queueName, sender);
  }

  public void addAndInitSenderAsync(String queueName) {
    CompletableFuture.runAsync(() -> this.addAndInitSender(queueName));
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
