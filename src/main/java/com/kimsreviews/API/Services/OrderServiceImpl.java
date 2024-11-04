package com.kimsreviews.API.Services;

import com.kimsreviews.API.DTO.OrderDTO;
import com.kimsreviews.API.Exceptions.OrderNotFoundException;
import com.kimsreviews.API.Repository.OrderRepo;
import com.kimsreviews.API.models.Order;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class OrderServiceImpl implements OrderService {

    private final OrderRepo orderRepo;

    @Autowired
    public OrderServiceImpl(OrderRepo orderRepo) {
        this.orderRepo = orderRepo;
    }

    @Override
    public List<OrderDTO> getAllOrders() {
        // Convert List<Order> to List<OrderDTO>
        List<OrderDTO> orderList = orderRepo.findAll().stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());

        // Reverse the list
        Collections.reverse(orderList);
        return orderList;
    }

    @Override
    public OrderDTO getOrderById(Long id) {
        Order order = orderRepo.findById(id)
                .orElseThrow(() -> new OrderNotFoundException("Order not found with id: " + id));
        return convertToDTO(order);
    }

    // Convert Order entity to OrderDTO
    private OrderDTO convertToDTO(Order order) {
        OrderDTO orderDTO = new OrderDTO();
        orderDTO.setId(order.getId());
        orderDTO.setProductName(order.getProductName());
        orderDTO.setQuantity(order.getQuantity());
        orderDTO.setPrice(order.getPrice());
        return orderDTO;
    }
}
