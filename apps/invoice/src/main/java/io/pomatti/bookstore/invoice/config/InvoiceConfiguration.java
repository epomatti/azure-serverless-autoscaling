package io.pomatti.bookstore.invoice.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Scope;

import lombok.Data;

@Configuration
@Scope(value = "singleton")
@Data
public class InvoiceConfiguration {

  @Value("${azure.servicebus.connectionstring}")
  private String connectionString;

  @Value("${azure.servicebus.prefetchCount}")
  private Integer prefetchCount;

  @Value("${azure.servicebus.maxConcurrentCalls}")
  private Integer maxConcurrentCalls;

}
