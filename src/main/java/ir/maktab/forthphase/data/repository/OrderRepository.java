package ir.maktab.forthphase.data.repository;

import ir.maktab.forthphase.data.model.Order;
import ir.maktab.forthphase.data.model.enums.OrderStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.Set;

public interface OrderRepository extends JpaRepository<Order, Long> {

    Set<Order> findBySubServiceName(String subServiceName);

    Optional<Order> findByOrderCode(String orderCode);

    Set<Order> findByOrderStatusAndSubServiceName(OrderStatus orderStatus, String subServiceName);

    List<Order> findOrdersByCustomerEmail(String customerEmail);

    List<Order> findOrdersByCustomerEmailAndOrderStatus(String customerEmail, OrderStatus orderStatus);

    List<Order> findOrdersByAcceptedExpertEmail(String expertEmail);
}
