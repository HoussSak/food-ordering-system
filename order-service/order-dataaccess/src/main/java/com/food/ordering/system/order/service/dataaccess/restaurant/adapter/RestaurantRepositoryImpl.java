package com.food.ordering.system.order.service.dataaccess.restaurant.adapter;

import com.food.odrering.system.order.service.domain.entity.Restaurant;
import com.food.ordering.system.order.service.dataaccess.restaurant.mapper.RestaurantDataAccessMapper;
import com.food.ordering.system.order.service.dataaccess.restaurant.repository.RestaurantJpaRepository;
import com.food.ordering.system.order.service.domain.ports.output.repository.RestaurantRepository;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Component
public class RestaurantRepositoryImpl implements RestaurantRepository {

    private final RestaurantJpaRepository repository;
    private final RestaurantDataAccessMapper restaurantDataAccessMapper;

    public RestaurantRepositoryImpl(RestaurantJpaRepository repository, RestaurantDataAccessMapper restaurantDataAccessMapper) {
        this.repository = repository;
        this.restaurantDataAccessMapper = restaurantDataAccessMapper;
    }

    @Override
    public Optional<Restaurant> findRestaurantInformation(Restaurant restaurant) {
        List<UUID> restaurantProducts = restaurantDataAccessMapper.restaurantToRestaurantProducts(restaurant);

        return repository.findByRestaurantIdAndProductIdIn(restaurant.getId().getValue(), restaurantProducts)
                .map(restaurantDataAccessMapper::restaurantEntityToRestaurant);
    }
}
