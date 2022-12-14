package io.pomatti.bookstore.delivery.services;

import java.time.LocalDateTime;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class DeliveryService {

  @Autowired
  DeliveryRepository repository;

  public void createDelivery(Long orderId) {
    var delivery = new Delivery();

    delivery.setOrderId(orderId);

    delivery.setExtraString1("abcdefghijk");
    delivery.setExtraString2("abcdefghijk");
    delivery.setExtraString3("abcdefghijk");
    delivery.setExtraString4("abcdefghijk");
    delivery.setExtraString5("abcdefghijk");
    delivery.setExtraString6("abcdefghijk");
    delivery.setExtraString7("abcdefghijk");
    delivery.setExtraString8("abcdefghijk");
    delivery.setExtraString9("abcdefghijk");
    delivery.setExtraString10("abcdefghijk");

    delivery.setExtraLong1(1000L);
    delivery.setExtraLong2(1000L);
    delivery.setExtraLong3(1000L);
    delivery.setExtraLong4(1000L);
    delivery.setExtraLong5(1000L);
    delivery.setExtraLong6(1000L);

    delivery.setExtraDateTime1(LocalDateTime.now());
    delivery.setExtraDateTime2(LocalDateTime.now());
    delivery.setExtraDateTime3(LocalDateTime.now());
    delivery.setExtraDateTime4(LocalDateTime.now());
    delivery.setExtraDateTime5(LocalDateTime.now());
    delivery.setExtraDateTime6(LocalDateTime.now());

    repository.save(delivery);
  }

}
