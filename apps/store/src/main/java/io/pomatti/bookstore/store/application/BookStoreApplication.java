package io.pomatti.bookstore.store.application;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.event.EventListener;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

import com.azure.messaging.servicebus.ServiceBusMessage;

import io.pomatti.bookstore.store.shared.ServiceBusConfiguration;

@ComponentScan({ "io.pomatti.bookstore.store" })
@EntityScan({ "io.pomatti.bookstore.store" })
@EnableJpaRepositories("io.pomatti.bookstore.store")
@SpringBootApplication
public class BookStoreApplication {

	Logger logger = LoggerFactory.getLogger(BookStoreApplication.class);

	@Autowired
	ServiceBusConfiguration config;

	public static void main(String[] args) {
		SpringApplication.run(BookStoreApplication.class, args);
	}

	@EventListener(ApplicationReadyEvent.class)
	public void runAfterStartup() {
		logger.info("Starting Service Bus");

		var sender = config.getClientBuilder().sender()
				.queueName("healthcheck")
				.buildClient();
		try {
			sender.sendMessage(new ServiceBusMessage("health check"));
			logger.info("Service Bus started");
		} finally {
			sender.close();
		}
	}

}
