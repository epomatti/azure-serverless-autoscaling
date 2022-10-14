package io.pomatti.bookstore.invoice.services;

import java.time.LocalDateTime;

import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

import lombok.Data;

@Entity
@Data
public class Invoice {

  @Id
  @GeneratedValue(strategy = GenerationType.AUTO)
  private Long id;

  private Long orderId;
  private Long registryNumber;

  @Enumerated(EnumType.ORDINAL)
  private Status status = Status.PENDING;

  // Filler fields
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
