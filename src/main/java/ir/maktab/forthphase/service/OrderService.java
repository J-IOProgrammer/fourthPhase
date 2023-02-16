package ir.maktab.forthphase.service;

import ir.maktab.forthphase.data.model.Order;
import ir.maktab.forthphase.data.model.enums.OrderStatus;
import ir.maktab.forthphase.data.repository.OrderRepository;
import ir.maktab.forthphase.exceptions.OrderCodeNotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Set;

@Service
@RequiredArgsConstructor
@Transactional
public class OrderService {

    private final OrderRepository orderRepository;

    public Set<Order> findOrdersBySubServiceName(String subServiceName) {
        return orderRepository.findBySubServiceName(subServiceName);
    }

    public Order findOrderByCode(String code) {
        return orderRepository.findByOrderCode(code).orElseThrow(
                OrderCodeNotFoundException::new);
    }

    public Set<Order> findOrderByStatusAndSubServiceName(OrderStatus orderStatus, String subServiceName) {
        return orderRepository.findByOrderStatusAndSubServiceName(orderStatus, subServiceName);
    }

    public void saveOrder(Order order) {
        orderRepository.save(order);
    }
}
