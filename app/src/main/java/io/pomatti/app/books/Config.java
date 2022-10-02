package io.pomatti.app.books;

// import org.springframework.beans.factory.annotation.Value;
// import org.springframework.context.annotation.Configuration;

import lombok.Data;

// @Configuration
@Data
public class Config {

  // @Value("${azure.eventhub.connectionString}")
  private String connectionString;

  // @Value("${azure.eventhub.name}")
  private String eventHubName;

}
