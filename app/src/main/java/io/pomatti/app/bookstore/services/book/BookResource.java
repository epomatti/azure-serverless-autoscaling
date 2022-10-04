package io.pomatti.app.bookstore.services.book;

import java.time.LocalDate;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import io.pomatti.app.bookstore.services.author.AuthorRepository;

@RestController
@RequestMapping(value = "/api/books")
public class BookResource {

  @Autowired
  BookRepository bookRepository;

  @Autowired
  AuthorRepository authorRepository;

  @PostMapping("/")
  public ResponseEntity<Book> post(@RequestBody BookRequest request) {
    var author = authorRepository.findById(request.getAuthorId()).get();

    var book = new Book();
    book.setAuthor(author);

    book.setName("Favorite Book");
    book.setPublishDate(LocalDate.now());
    book.setValue(50.0);

    book = bookRepository.save(book);

    return new ResponseEntity<Book>(book, HttpStatus.CREATED);
  }

  @GetMapping("/{id}")
  public ResponseEntity<Book> get(@PathVariable Long id) {
    var book = bookRepository.findById(id);
    return ResponseEntity.ok(book.get());
  }

}
