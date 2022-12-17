package com.food.ordering.system.order.service.dataaccess.order.mapper;

import com.food.odrering.system.order.service.domain.entity.Order;
import com.food.odrering.system.order.service.domain.entity.OrderItem;
import com.food.odrering.system.order.service.domain.entity.Product;
import com.food.odrering.system.order.service.domain.valueobject.OrderItemId;
import com.food.odrering.system.order.service.domain.valueobject.StreetAddress;
import com.food.odrering.system.order.service.domain.valueobject.TrackingId;
import com.food.ordering.system.domain.valueobject.*;
import com.food.ordering.system.order.service.dataaccess.order.entity.OrderAddressEntity;
import com.food.ordering.system.order.service.dataaccess.order.entity.OrderEntity;
import com.food.ordering.system.order.service.dataaccess.order.entity.OrderItemEntity;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import static com.food.odrering.system.order.service.domain.entity.Order.FAILURE_MESSAGE_DELIMITER;

@Component
public class OrderDataAccessMapper {
    public OrderEntity orderToOrderEntity(Order order) {
        OrderEntity orderEntity = OrderEntity.builder()
                .id(order.getId().getValue())
                .customerId(order.getCustomerId().getValue())
                .restaurantId(order.getRestaurantId().getValue())
                .trackingId(order.getTrackingiD().getValue())
                .address(deliveryAddressToAdressEntity(order.getStreetAddress()))
                .price(order.getMoney().getAmount())
                .orderItems(orderItemtoOrderItemEntities(order.getItems()))
                .orderStatus(order.getOrderStatus())
                .failureMessages(order.getFailureMessages() != null ?
                        String.join(FAILURE_MESSAGE_DELIMITER,order.getFailureMessages()):"")
                .build();

        orderEntity.getAddress().setOrder(orderEntity);
        orderEntity.getOrderItems().forEach(orderItemEntity -> orderItemEntity.setOrder(orderEntity));

        return orderEntity;
    }

    public Order orderEntityToOrder(OrderEntity orderEntity) {

        return Order.builder()
                .id(new OrderId(orderEntity.getId()))
                .customerId(new CustomerId(orderEntity.getCustomerId()))
                .restaurantId(new RestaurantId(orderEntity.getRestaurantId()))
                .streetAddress(addressEntityToDeliveryAddress(orderEntity.getAddress()))
                .money(new Money(orderEntity.getPrice()))
                .items(orderItrmEntityToOrderItem(orderEntity.getOrderItems()))
                .trackingiD(new TrackingId(orderEntity.getTrackingId()))
                .orderStatus(orderEntity.getOrderStatus())
                .failureMessages(orderEntity.getFailureMessages().isEmpty() ? new ArrayList<>() :

                        new ArrayList<>(Arrays.asList(orderEntity.getFailureMessages()
                                .split(FAILURE_MESSAGE_DELIMITER))))
                .build();
    }

    private List<OrderItem> orderItrmEntityToOrderItem(List<OrderItemEntity> orderItems) {

        return orderItems.stream()
                .map(orderItemEntity -> OrderItem.builder()
                        .orderItemId(new OrderItemId(orderItemEntity.getId()))
                        .product(new Product(new ProductId(orderItemEntity.getProductId())))
                        .price(new Money(orderItemEntity.getPrice()))
                        .quantity(orderItemEntity.getQuantity())
                        .subTotal(new Money(orderItemEntity.getSubTotal()))
                        .build())
                .collect(Collectors.toList());

    }

    private StreetAddress addressEntityToDeliveryAddress(OrderAddressEntity address) {
        return  new StreetAddress(address.getId(),
                address.getStreet(),
                address.getPostalCode(),
                address.getCity());
    }

    private List<OrderItemEntity> orderItemtoOrderItemEntities(List<OrderItem> items) {
        return items.stream()
                .map(orderItem -> OrderItemEntity.builder()
                        .id(orderItem.getId().getValue())
                        .productId(orderItem.getProduct().getId().getValue())
                        .price(orderItem.getPrice().getAmount())
                        .quantity(orderItem.getQuantity())
                        .subTotal(orderItem.getSubTotal().getAmount())
                        .build())
                .collect(Collectors.toList());
    }

    private OrderAddressEntity deliveryAddressToAdressEntity(StreetAddress streetAddress) {

        return  OrderAddressEntity.builder()
                .id(streetAddress.getId())
                .city(streetAddress.getCity())
                .street(streetAddress.getStreet())
                .postalCode(streetAddress.getPostalCode())
                .build();

    }
}