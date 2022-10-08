package io.pomatti.bookstore.store.services.book;

import java.time.LocalDate;
import java.time.LocalDateTime;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import io.pomatti.bookstore.core.ResourceCore;
import io.pomatti.bookstore.store.services.author.AuthorRepository;


@RestController
@RequestMapping(value = "/api/books")
public class BookResource extends ResourceCore {

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

    book.setExtraString1("abcdefghijk");
    book.setExtraString2("abcdefghijk");
    book.setExtraString3("abcdefghijk");
    book.setExtraString4("abcdefghijk");
    book.setExtraString5("abcdefghijk");
    book.setExtraString6("abcdefghijk");
    book.setExtraString7("abcdefghijk");
    book.setExtraString8("abcdefghijk");
    book.setExtraString9("abcdefghijk");
    book.setExtraString10("abcdefghijk");

    book.setExtraLong1(1000L);
    book.setExtraLong2(1000L);
    book.setExtraLong3(1000L);
    book.setExtraLong4(1000L);
    book.setExtraLong5(1000L);
    book.setExtraLong6(1000L);

    book.setExtraDateTime1(LocalDateTime.now());
    book.setExtraDateTime2(LocalDateTime.now());
    book.setExtraDateTime3(LocalDateTime.now());
    book.setExtraDateTime4(LocalDateTime.now());
    book.setExtraDateTime5(LocalDateTime.now());
    book.setExtraDateTime6(LocalDateTime.now());

    bookRepository.save(book);

    return created(book);
  }

  @GetMapping("/{id}")
  public ResponseEntity<Book> get(@PathVariable Long id) {
    var book = bookRepository.findById(id);
    return optionalGet(book);
  }

}
