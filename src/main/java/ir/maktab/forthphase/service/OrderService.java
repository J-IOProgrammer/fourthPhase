package ir.maktab.forthphase.service;

import ir.maktab.forthphase.data.dto.searchrequest.OrderSearchRequest;
import ir.maktab.forthphase.data.model.Order;
import ir.maktab.forthphase.data.model.enums.OrderStatus;
import ir.maktab.forthphase.data.repository.OrderRepository;
import ir.maktab.forthphase.exceptions.InvalidTimeException;
import ir.maktab.forthphase.exceptions.OrderCodeNotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
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

    public List<Order> showCustomerOrdersByOrderStatus(String customerEmail, OrderStatus status) {
        return orderRepository.findOrdersByCustomerEmailAndOrderStatus(customerEmail, status);
    }

    public Set<Order> findOrderByStatusAndSubServiceName(OrderStatus orderStatus, String subServiceName) {
        return orderRepository.findByOrderStatusAndSubServiceName(orderStatus, subServiceName);
    }

    public void saveOrder(Order order) {
        orderRepository.save(order);
    }

    public List<Order> applyFilter(OrderSearchRequest request) {
        List<Order> all = orderRepository.findOrdersByCustomerEmail(request.getCustomerEmail());
        List<Order> applyOrderStatus = new ArrayList<>();
        List<Order> applySubServiceName = new ArrayList<>();
        List<Order> applyServiceName = new ArrayList<>();
        List<Order> applyTimeRange = new ArrayList<>();
        if (checkRequestFields(request))
            return all;

        if (request.getStatus() != null)
            applyOrderStatus = applyOrderStatus(request.getStatus(),
                    orderRepository.findOrdersByCustomerEmail(request.getCustomerEmail()));
        if (request.getSubServiceName() != null)
            applySubServiceName = applySubServiceName(request.getSubServiceName(),
                    orderRepository.findOrdersByCustomerEmail(request.getCustomerEmail()));
        if (request.getServiceName() != null)
            applyServiceName = applyServiceName(request.getServiceName(),
                    orderRepository.findOrdersByCustomerEmail(request.getCustomerEmail()));
        if (request.getEndDate() != null && request.getStartDate() != null)
            applyTimeRange = applyTimeRange(request.getStartDate(), request.getEndDate(),
                    orderRepository.findOrdersByCustomerEmail(request.getCustomerEmail()));

        applyServiceName.addAll(applySubServiceName);
        applyOrderStatus.addAll(applyServiceName);
        applyTimeRange.addAll(applyOrderStatus);

        return applyTimeRange;
    }

    public List<Order> showCustomerOrders(String customerEmail) {
        return orderRepository.findOrdersByCustomerEmail(customerEmail);
    }

    public List<Order> showDoneOrdersByExpert(String expertEmail) {
        return orderRepository.findOrdersByAcceptedExpertEmail(expertEmail);
    }

    private boolean checkRequestFields(OrderSearchRequest request) {
        return request.getServiceName() == null && request.getSubServiceName() == null
                && request.getStatus() == null && request.getEndDate() == null
                && request.getStartDate() == null;
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

    private List<Order> applyTimeRange(String startTime, String endTime, List<Order> orders) {
        List<Order> orderList = new ArrayList<>();
        Date sTime;
        Date eTime;
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        try {
            sTime = format.parse(startTime);
            eTime = format.parse(endTime);
        } catch (ParseException e) {
            throw new InvalidTimeException();
        }
        for (Order order : orders)
            if (order.getWriteInDate().compareTo(sTime) > 0 || order.getWriteInDate().compareTo(sTime) == 0
                    && order.getWriteInDate().compareTo(eTime) < 0 || order.getWriteInDate().compareTo(eTime) == 0)
                orderList.add(order);
        return orderList;
    }
}
