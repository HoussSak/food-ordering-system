package com.food.odrering.system.order.service.domain.entity;

import com.food.odrering.system.order.service.domain.exception.OrderDomainException;
import com.food.odrering.system.order.service.domain.valueobject.OrderItemId;
import com.food.odrering.system.order.service.domain.valueobject.StreetAddress;
import com.food.odrering.system.order.service.domain.valueobject.TrackingId;
import com.food.ordering.system.domain.entity.AggregateRoot;
import com.food.ordering.system.domain.valueobject.*;

import java.util.List;
import java.util.UUID;

public class Order extends AggregateRoot<OrderId> {

    private final CustomerId customerId;
    private final RestaurantId restaurantId;
    private final StreetAddress streetAddress;
    private final Money price;
    private final List<OrderItem> items;



    private TrackingId trackingiD;
    private OrderStatus orderStatus;
    private List<String> failureMessages;


    public void initializeOrder() {
        setId(new OrderId(UUID.randomUUID()));
        trackingiD = new TrackingId(UUID.randomUUID());
        orderStatus =OrderStatus.PENDING;
        initializeOrderItems();
    }

    public void validateOrder(){
        validateInitializeOrder();
        validateTotalPrice();
        validateItemsPrice();

    }

    public void pay(){
        if(orderStatus != OrderStatus.PENDING) {
            throw new OrderDomainException("Order state is not in the correct state for pay operation ! ");
        }
        orderStatus = OrderStatus.PAID;
    }

    public void approve(){
        if(orderStatus != OrderStatus.PAID) {
            throw new OrderDomainException("Order state is not in the correct state for approve operation ! ");
        }
        orderStatus = OrderStatus.APPROVED;
    }

    public void initCancel(List<String> failureMessages){
        if(orderStatus != OrderStatus.PAID) {
            throw new OrderDomainException("Order state is not in the correct state for initCancel operation ! ");
        }
        orderStatus = OrderStatus.CANCELLING;
        updateFailureMessages(failureMessages);
    }


    public void cancel(List<String> failureMessages){
        if(!(orderStatus == OrderStatus.PAID || orderStatus == OrderStatus.CANCELLING)) {
            throw new OrderDomainException("Order state is not in the correct state for cancel operation ! ");
        }
        orderStatus = OrderStatus.CANCELLED;
        updateFailureMessages(failureMessages);
    }

    private void updateFailureMessages(List<String> failureMessages) {
        if (this.failureMessages != null && failureMessages != null) {
            this.failureMessages.addAll(failureMessages.stream()
                    .filter(message -> !message.isEmpty()).toList());
        }
        if(this.failureMessages == null) {
            this.failureMessages = failureMessages;
        }
    }




    private void validateItemsPrice() {
       Money orderItemsTotal=items.stream().map(orderItem -> {
            validateItemPrice(orderItem);
            return orderItem.getSubTotal();

        }).reduce(Money.ZERO,Money::add);

       if (!price.equals(orderItemsTotal)) {
           throw new OrderDomainException("Total price: "+price.getAmount()+
                   " is not equal to Order items total: "+orderItemsTotal.getAmount()+ "!");
       }
    }

    private void validateItemPrice(OrderItem orderItem) {

        if (!orderItem.isPriceValid()) {
            throw new OrderDomainException("order item price: "+orderItem.getPrice().getAmount()+
                    " is not valid for product "+orderItem.getProduct().getId().getValue());
        }
    }


    private void validateInitializeOrder() {
        if(orderStatus != null || getId()!= null) {
            throw new OrderDomainException("Order is not in correct state for initialization ");

        }
    }

    private void validateTotalPrice() {
        if (price == null || !price.isGreaterThanZero() ) {
            throw new OrderDomainException("Total price must be greater than zero!");
        }
    }


    private void initializeOrderItems() {
        long itemId = 1;
        for (OrderItem orderItem : items) {
            orderItem.initializeOrderItem(this.getId(),new OrderItemId(itemId++));

        }
    }

    private Order(Builder builder) {
        super.setId(builder.orderId);
        customerId = builder.customerId;
        restaurantId = builder.restaurantId;
        streetAddress = builder.streetAddress;
        price = builder.price;
        items = builder.items;
        trackingiD = builder.trackingiD;
        orderStatus = builder.orderStatus;
        failureMessages = builder.failureMessages;
    }


    public CustomerId getCustomerId() {
        return customerId;
    }

    public RestaurantId getRestaurantId() {
        return restaurantId;
    }

    public StreetAddress getStreetAddress() {
        return streetAddress;
    }

    public Money getMoney() {
        return price;
    }

    public List<OrderItem> getItems() {
        return items;
    }

    public TrackingId getTrackingiD() {
        return trackingiD;
    }

    public OrderStatus getOrderStatus() {
        return orderStatus;
    }

    public List<String> getFailureMessages() {
        return failureMessages;
    }

    public static Builder builder() {
        return new Builder();
    }


    public static final class Builder {
        private OrderId orderId;
        private CustomerId customerId;
        private RestaurantId restaurantId;
        private StreetAddress streetAddress;
        private Money price;
        private List<OrderItem> items;
        private TrackingId trackingiD;
        private OrderStatus orderStatus;
        private List<String> failureMessages;


        private Builder() {
        }

        public static Builder builder() {
            return new Builder();
        }

        public Builder id(OrderId val) {
            orderId = val;
            return this;
        }

        public Builder customerId(CustomerId val) {
            customerId = val;
            return this;
        }

        public Builder restaurantId(RestaurantId val) {
            restaurantId = val;
            return this;
        }

        public Builder streetAddress(StreetAddress val) {
            streetAddress = val;
            return this;
        }

        public Builder money(Money val) {
            price = val;
            return this;
        }

        public Builder items(List<OrderItem> val) {
            items = val;
            return this;
        }

        public Builder trackingiD(TrackingId val) {
            trackingiD = val;
            return this;
        }

        public Builder orderStatus(OrderStatus val) {
            orderStatus = val;
            return this;
        }

        public Builder failureMessages(List<String> val) {
            failureMessages = val;
            return this;
        }

        public Order build() {
            return new Order(this);
        }
    }
}
