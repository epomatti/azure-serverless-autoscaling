package io.pomatti.app.books;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@SpringBootApplication
@RestController
@RequestMapping("/api/books")
public class BookApplication {

	public static void main(String[] args) {
		SpringApplication.run(BookApplication.class, args);
	}

	// @Autowired
	// BookRepository repository;

	// @PostMapping("/")
	// public Book post(@RequestBody Book order) {
	// var book = new Book();
	// book.setName("Eternal Sunshine");
	// book.setAuthor("Anonymous");
	// return repository.save(book);
	// }

	@GetMapping("/")
	public Book post(@RequestBody Book order) {
		var book = new Book();
		return book;
	}

}
