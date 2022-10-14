package io.pomatti.bookstore.invoice.integration.bus;

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

import io.pomatti.bookstore.core.JsonUtils;
import io.pomatti.bookstore.invoice.config.InvoiceConfiguration;
import io.pomatti.bookstore.invoice.integration.events.CreateInvoiceEvent;
import io.pomatti.bookstore.invoice.services.InvoiceService;

@Service
@Scope("singleton")
public class AuthorizeInvoiceConsumer {

  Logger logger = LoggerFactory.getLogger(AuthorizeInvoiceConsumer.class);

  @Autowired
  ApplicationContext context;

  @Autowired
  InvoiceConfiguration config;

  @Autowired
  InvoiceService service;

  private static ServiceBusProcessorClient processorClient;

  public void start() {
    Consumer<ServiceBusReceivedMessageContext> processMessage = messageContext -> {
      try {
        var event = parseEvent(messageContext);
        service.authorizeInvoice(event);
        messageContext.complete();
      } catch (Exception ex) {
        logger.error("Error processing message", ex);
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
        .prefetchCount(config.getPrefetchCount())
        .queueName(EventQueues.AUTHORIZE_INVOICE_QUEUE)
        .processMessage(processMessage)
        .processError(processError)
        .disableAutoComplete()
        .buildProcessorClient();

    processorClient.start();
  }

  protected Long parseEvent(ServiceBusReceivedMessageContext messageContext) {
    var payload = messageContext.getMessage().getBody().toString();
    return Long.parseLong(payload);
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
