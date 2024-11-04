package com.kimsreviews.API.Repository;

import com.kimsreviews.API.models.Order;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OrderRepo extends JpaRepository<Order, Long> {
}
