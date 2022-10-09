package io.pomatti.bookstore.store.services.order;

import java.time.LocalDateTime;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import lombok.Data;

@Entity
@Table(name = "\"order\"")
@Data
public class Order {

  @Id
  @GeneratedValue(strategy = GenerationType.AUTO)
  private Long id;

  private String extraString1;
  private String extraString2;
  private String extraString3;
  private String extraString4;
  private String extraString5;
  private String extraString6;
  private String extraString7;
  private String extraString8;
  private String extraString9;
  private String extraString10;

  private Long extraLong1;
  private Long extraLong2;
  private Long extraLong3;
  private Long extraLong4;
  private Long extraLong5;
  private Long extraLong6;

  private LocalDateTime extraDateTime1;
  private LocalDateTime extraDateTime2;
  private LocalDateTime extraDateTime3;
  private LocalDateTime extraDateTime4;
  private LocalDateTime extraDateTime5;
  private LocalDateTime extraDateTime6;

}
