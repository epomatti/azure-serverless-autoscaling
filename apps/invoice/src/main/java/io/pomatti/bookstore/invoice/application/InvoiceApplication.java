package io.pomatti.bookstore.invoice.application;

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

import io.pomatti.bookstore.invoice.integration.bus.AuthorizeInvoiceConsumer;
import io.pomatti.bookstore.invoice.integration.bus.CreateInvoicesConsumer;
import io.pomatti.bookstore.invoice.integration.bus.InvoiceSender;

@ComponentScan({ "io.pomatti.bookstore.invoice" })
@EntityScan({ "io.pomatti.bookstore.invoice" })
@EnableJpaRepositories("io.pomatti.bookstore.invoice")
@SpringBootApplication
public class InvoiceApplication {

	Logger logger = LoggerFactory.getLogger(InvoiceApplication.class);

	@Autowired
	CreateInvoicesConsumer createInvoicesConsumer;

	@Autowired
	AuthorizeInvoiceConsumer authorizeInvoiceConsumer;

	@Autowired
	InvoiceSender sender;

	public static void main(String[] args) {
		SpringApplication.run(InvoiceApplication.class, args);
	}

	@EventListener(ApplicationReadyEvent.class)
	public void startCreateInvoicesConsumer() {
		createInvoicesConsumer.start();
	}

	@EventListener(ApplicationReadyEvent.class)
	public void startAuthorizeInvoiceConsumer() {
		authorizeInvoiceConsumer.start();
	}

	@EventListener(ApplicationReadyEvent.class)
	public void startSender() {
		sender.start();
	}

}
