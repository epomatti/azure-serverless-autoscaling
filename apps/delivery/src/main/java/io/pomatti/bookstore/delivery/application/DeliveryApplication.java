package io.pomatti.bookstore.delivery.application;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@ComponentScan({ "io.pomatti.bookstore.delivery" })
@EntityScan({ "io.pomatti.bookstore.delivery" })
@EnableJpaRepositories("io.pomatti.bookstore.delivery")
@SpringBootApplication
public class DeliveryApplication {

	public static void main(String[] args) {
		SpringApplication.run(DeliveryApplication.class, args);
	}

}
