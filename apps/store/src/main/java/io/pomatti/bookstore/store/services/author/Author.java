package io.pomatti.bookstore.store.services.author;

import java.util.Set;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;

import io.pomatti.bookstore.store.services.book.Book;
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
