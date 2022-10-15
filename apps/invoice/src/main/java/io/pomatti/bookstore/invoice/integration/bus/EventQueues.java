package io.pomatti.bookstore.invoice.integration.bus;

public class EventQueues {

  public final static String CREATE_INVOICES_QUEUE = "invoice-create";
  public final static String AUTHORIZE_INVOICE_QUEUE = "invoice-authorize";
  public final static String INVOICE_READY_QUEUE = "invoice-authorized";

}
