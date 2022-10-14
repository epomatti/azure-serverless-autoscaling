package io.pomatti.bookstore.invoice.services;

public class InvoiceNotFoundException extends RuntimeException {

  public InvoiceNotFoundException(Long id) {
    super(String.format("Invoice [%s] not found.", id));
  }

}
