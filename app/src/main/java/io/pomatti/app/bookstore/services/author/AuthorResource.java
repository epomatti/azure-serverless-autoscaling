package io.pomatti.app.bookstore.services.author;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(value = "/api/authors")
public class AuthorResource {

  @Autowired
  AuthorRepository authorRepository;

  @PostMapping("/")
  public ResponseEntity<Author> post() {
    Author author = new Author();
    author.setName("Sisyphus");
    authorRepository.save(author);
    return new ResponseEntity<Author>(author, HttpStatus.CREATED);
  }

  @GetMapping("/{id}")
  public ResponseEntity<Author> get(@PathVariable Long id) {
    var author = authorRepository.findById(id);
    return ResponseEntity.ok(author.get());
  }

}
