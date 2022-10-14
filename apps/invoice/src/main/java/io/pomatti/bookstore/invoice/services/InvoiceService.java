package io.pomatti.bookstore.invoice.services;

import java.time.LocalDateTime;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import io.pomatti.bookstore.invoice.integration.CreateInvoiceEvent;
import io.pomatti.bookstore.invoice.integration.InvoiceSender;

@Service
public class InvoiceService {

  @Autowired
  InvoiceRepository repository;

  @Autowired
  InvoiceSender sender;

  public void createInvoices(CreateInvoiceEvent event) {
    event.getItems().parallelStream().forEach(item -> {
      var invoice = new Invoice();
      populateInvoice(invoice);
      invoice.setOrderId(event.getOrderId());
      repository.save(invoice);
    });
    sender.sendProcessesInvoicesEvent(event.getItems());
  }

  // public void processInvoice(CreateInvoiceEvent event) {
  //   event.getItems().parallelStream().forEach(item -> {
  //     var invoice = new Invoice();
  //     populateInvoice(invoice);
  //     invoice.setOrderId(event.getOrderId());
  //     repository.save(invoice);
  //   });
  // }

  protected void populateInvoice(Invoice invoice) {
    invoice.setExtraString1("abcdefghijk");
    invoice.setExtraString2("abcdefghijk");
    invoice.setExtraString3("abcdefghijk");
    invoice.setExtraString4("abcdefghijk");
    invoice.setExtraString5("abcdefghijk");
    invoice.setExtraString6("abcdefghijk");
    invoice.setExtraString7("abcdefghijk");
    invoice.setExtraString8("abcdefghijk");
    invoice.setExtraString9("abcdefghijk");
    invoice.setExtraString10("abcdefghijk");

    invoice.setExtraLong1(1000L);
    invoice.setExtraLong2(1000L);
    invoice.setExtraLong3(1000L);
    invoice.setExtraLong4(1000L);
    invoice.setExtraLong5(1000L);
    invoice.setExtraLong6(1000L);

    invoice.setExtraDateTime1(LocalDateTime.now());
    invoice.setExtraDateTime2(LocalDateTime.now());
    invoice.setExtraDateTime3(LocalDateTime.now());
    invoice.setExtraDateTime4(LocalDateTime.now());
    invoice.setExtraDateTime5(LocalDateTime.now());
    invoice.setExtraDateTime6(LocalDateTime.now());
  }

}
