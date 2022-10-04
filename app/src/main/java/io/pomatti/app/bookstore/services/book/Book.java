package io.pomatti.app.bookstore.services.book;

import lombok.Data;

import java.time.LocalDate;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

import io.pomatti.app.bookstore.services.author.Author;

@Entity
@Data
public class Book {

  @Id
  @GeneratedValue(strategy = GenerationType.AUTO)
  private Long id;

  private String name;
  private String description;
  private LocalDate publishDate;
  private Double value;

  @ManyToOne
  @JoinColumn(nullable = false)
  private Author author;

}