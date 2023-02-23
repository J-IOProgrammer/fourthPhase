package ir.maktab.forthphase.service;

import ir.maktab.forthphase.data.dto.searchrequest.OrderSearchRequest;
import ir.maktab.forthphase.data.model.Order;
import ir.maktab.forthphase.data.model.enums.OrderStatus;
import ir.maktab.forthphase.data.repository.OrderRepository;
import ir.maktab.forthphase.exceptions.OrderCodeNotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
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

    public List<Order> applyFilter(OrderSearchRequest request) {
        List<Order> all = orderRepository.findAll();
        if (checkRequestFields(request))
            return all;

        List<Order> applyOrderStatus = applyOrderStatus(request.getStatus(),
                orderRepository.findAll());
        List<Order> applySubServiceName = applySubServiceName(request.getSubServiceName(),
                orderRepository.findAll());
        List<Order> applyServiceName = applyServiceName(request.getServiceName(),
                orderRepository.findAll());
        List<Order> applyCustomerEmail = applyCustomerEmail(request.getCustomerEmail(),
                orderRepository.findAll());

        applyServiceName.addAll(applySubServiceName);
        applyOrderStatus.addAll(applyServiceName);
        applyCustomerEmail.addAll(applyOrderStatus);
        return applyCustomerEmail;
    }

    public int applyCountDoneStatusOrders(String expertEmail) {
        return orderRepository.findCountDoneStatusOrdersByExpertEmail(expertEmail);
    }

    private boolean checkRequestFields(OrderSearchRequest request) {
        return request.getServiceName() == null && request.getSubServiceName() == null
                && request.getStatus() == null && request.getEndDate() == null
                && request.getCustomerEmail() == null && request.getStartDate() == null;
    }

    private List<Order> applyServiceName(String serviceName, List<Order> orders) {
        List<Order> orderList = new ArrayList<>();
        for (Order order : orders)
            if (order.getServiceName().equals(serviceName))
                orderList.add(order);
        return orderList;
    }

    private List<Order> applySubServiceName(String subServiceName, List<Order> orders) {
        List<Order> orderList = new ArrayList<>();
        for (Order order : orders)
            if (order.getSubServiceName().equals(subServiceName))
                orderList.add(order);
        return orderList;
    }

    private List<Order> applyOrderStatus(OrderStatus status, List<Order> orders) {
        List<Order> orderList = new ArrayList<>();
        for (Order order : orders)
            if (order.getOrderStatus().equals(status))
                orderList.add(order);
        return orderList;
    }

    private List<Order> applyCustomerEmail(String customerEmail, List<Order> orders) {
        List<Order> orderList = new ArrayList<>();
        for (Order order : orders)
            if (order.getCustomer().getEmail().equals(customerEmail))
                orderList.add(order);
        return orderList;
    }
}
