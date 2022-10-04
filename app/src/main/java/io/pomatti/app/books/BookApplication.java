package io.pomatti.app.books;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@SpringBootApplication
@RestController
@RequestMapping(value = "/api/books")
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

	@PostMapping("/")
	public Book post() {
		var book = new Book();
		return book;
	}

	@GetMapping("/")
	public ResponseEntity<Book> get() {
		var book = new Book();
		return ResponseEntity.ok(book);
		// return new ResponseEntity<>(book, HttpStatus.OK);
	}

}
