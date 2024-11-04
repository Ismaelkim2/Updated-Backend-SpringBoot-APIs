package com.kimsreviews.API.Services;

import com.kimsreviews.API.DTO.OrderDTO;

import java.util.List;

public interface OrderService {
    List<OrderDTO> getAllOrders();
    OrderDTO getOrderById(Long id);
}
