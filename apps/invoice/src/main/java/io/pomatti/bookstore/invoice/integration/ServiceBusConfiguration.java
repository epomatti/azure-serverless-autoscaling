package io.pomatti.bookstore.invoice.integration;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Scope;

import lombok.Data;

@Configuration
@Scope(value = "singleton")
@Data
public class ServiceBusConfiguration {

  @Value("${azure.servicebus.connectionstring}")
  private String connectionString;

}
