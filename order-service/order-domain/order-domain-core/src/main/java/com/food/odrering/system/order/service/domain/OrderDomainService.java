package com.food.odrering.system.order.service.domain;

import com.food.odrering.system.order.service.domain.entity.Order;
import com.food.odrering.system.order.service.domain.entity.Restaurant;
import com.food.odrering.system.order.service.domain.event.OrderCancelledEvent;
import com.food.odrering.system.order.service.domain.event.OrderCreatedEvent;
import com.food.odrering.system.order.service.domain.event.OrderPaidEvent;

import java.util.List;

public interface OrderDomainService {

    OrderCreatedEvent validateAndInitiateEvent(Order order, Restaurant restaurant);

    OrderPaidEvent payOrder(Order order);

    void approveOrder(Order order);

    OrderCancelledEvent cancelOrderPayment(Order order, List<String> failureMessages);

    void cancelOrder(Order order,List<String> failureMessages);
}
