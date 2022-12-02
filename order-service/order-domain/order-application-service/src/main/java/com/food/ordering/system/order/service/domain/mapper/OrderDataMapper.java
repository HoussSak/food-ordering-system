package com.food.ordering.system.order.service.domain.mapper;

import com.food.odrering.system.order.service.domain.entity.Order;
import com.food.odrering.system.order.service.domain.entity.OrderItem;
import com.food.odrering.system.order.service.domain.entity.Product;
import com.food.odrering.system.order.service.domain.entity.Restaurant;
import com.food.odrering.system.order.service.domain.valueobject.StreetAddress;
import com.food.ordering.system.domain.valueobject.*;
import com.food.ordering.system.order.service.domain.dto.create.CreateOrderCommand;
import com.food.ordering.system.order.service.domain.dto.create.CreateOrderResponse;
import com.food.ordering.system.order.service.domain.dto.create.OrderAdress;
import com.food.ordering.system.order.service.domain.dto.track.TrackOrderRespone;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Component
public class OrderDataMapper {

    public Restaurant createOrderCommandToRestaurant(CreateOrderCommand createOrderCommand) {
        return Restaurant.builder()
                .restaurantId(new RestaurantId(createOrderCommand.getRestaurantId()))
                .products(createOrderCommand.getItems().stream().map(orderItem ->
                        new Product(new ProductId(orderItem.getProductId())))
                        .collect(Collectors.toList())
                )
        .build();
    }

    public Order createOrderCommandToOrder (CreateOrderCommand createOrderCommand) {

        return Order.builder()
                .customerId(new CustomerId(createOrderCommand.getCustomerId()))
                .restaurantId(new RestaurantId(createOrderCommand.getRestaurantId()))
                .streetAddress(orderAddressToStreetAdress(createOrderCommand.getAdress()))
                .money(new Money(createOrderCommand.getPrice()))
                .items(orderItemsToOrderItemEntities(createOrderCommand.getItems()))
                .build();
    }

    public CreateOrderResponse orderToCreateOrderResponse(Order order,String message) {

        return CreateOrderResponse.builder()
                .orderTrackingId(order.getTrackingiD().getValue())
                .orderStatus(order.getOrderStatus())
                .message(message)
                .build();

    }

    private List<OrderItem> orderItemsToOrderItemEntities(
            List<com.food.ordering.system.order.service.domain.dto.create.OrderItem> items) {

        return items.stream()
                .map(orderItem ->
                        OrderItem.builder()
                                .product(new Product(new ProductId(orderItem.getProductId())))
                                .price(new Money(orderItem.getPrice()))
                                .quantity(orderItem.getQuantity())
                                .subTotal(new Money(orderItem.getSubTotal()))
                                .build()).collect(Collectors.toList());
    }

    private StreetAddress orderAddressToStreetAdress(OrderAdress adress) {
        return new StreetAddress(
                UUID.randomUUID(),
                adress.getStreet(),
                adress.getPostalCode(),
                adress.getCity()
                );
    }

    public TrackOrderRespone orderToTrackOrderResponse(Order order) {
        return TrackOrderRespone.builder()
                .orderTrackingId(order.getTrackingiD().getValue())
                .orderStatus(order.getOrderStatus())
                .failureMessages(order.getFailureMessages())
                .build();
    }
}
