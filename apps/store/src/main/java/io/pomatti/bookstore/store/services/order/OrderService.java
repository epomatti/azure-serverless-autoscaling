package io.pomatti.bookstore.store.services.order;

import java.time.LocalDateTime;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class OrderService {

  @Autowired
  OrderRepository orderRepository;

  public Order createOrder() {
    var order = new Order();

    order.setExtraString1("abcdefghijk");
    order.setExtraString2("abcdefghijk");
    order.setExtraString3("abcdefghijk");
    order.setExtraString4("abcdefghijk");
    order.setExtraString5("abcdefghijk");
    order.setExtraString6("abcdefghijk");
    order.setExtraString7("abcdefghijk");
    order.setExtraString8("abcdefghijk");
    order.setExtraString9("abcdefghijk");
    order.setExtraString10("abcdefghijk");

    order.setExtraLong1(1000L);
    order.setExtraLong2(1000L);
    order.setExtraLong3(1000L);
    order.setExtraLong4(1000L);
    order.setExtraLong5(1000L);
    order.setExtraLong6(1000L);

    order.setExtraDateTime1(LocalDateTime.now());
    order.setExtraDateTime2(LocalDateTime.now());
    order.setExtraDateTime3(LocalDateTime.now());
    order.setExtraDateTime4(LocalDateTime.now());
    order.setExtraDateTime5(LocalDateTime.now());
    order.setExtraDateTime6(LocalDateTime.now());

    return orderRepository.save(order);
  }

  public void createDelivery(Order order) {
    
  }

}
