package com.food.ordering.system.order.service.dataaccess.restaurant.entity;

import lombok.*;

import javax.persistence.*;
import java.math.BigDecimal;
import java.util.Objects;
import java.util.UUID;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "order_restaurant_m_view",schema = "restaurants")
@IdClass(RestaurantEntityId.class)
@Entity
public class RestaurantEntity {
    @Id
    private UUID restaurantId;
    @Id
    private  UUID productId;

    private String restaurantName;

    private Boolean restaurantActive;
    private String productName;
    private BigDecimal productPrice;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        RestaurantEntity that = (RestaurantEntity) o;
        return restaurantId.equals(that.restaurantId) && productId.equals(that.productId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(restaurantId, productId);
    }
}
