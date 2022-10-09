package io.pomatti.bookstore.store.shared;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Scope;

import com.azure.messaging.servicebus.ServiceBusClientBuilder;

@Configuration
public class ServiceBusConfiguration {

  @Value("${azure.servicebus.connectionstring}")
  private String connectionString;

  @Bean
  @Scope(value = "singleton")
  public ServiceBusClientBuilder getClientBuilder() {
    return new ServiceBusClientBuilder()
        .connectionString(connectionString);
  }

}
