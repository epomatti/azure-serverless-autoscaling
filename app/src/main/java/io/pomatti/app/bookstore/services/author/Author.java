package io.pomatti.app.bookstore.services.author;

import java.util.Set;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;

import io.pomatti.app.bookstore.services.book.Book;
import lombok.Data;

@Entity
@Data
public class Author {

  @Id
  @GeneratedValue(strategy = GenerationType.AUTO)
  private Long id;

  private String name;

  @OneToMany
  private Set<Book> books;

}
