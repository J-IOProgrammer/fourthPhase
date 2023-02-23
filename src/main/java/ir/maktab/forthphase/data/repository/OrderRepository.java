package ir.maktab.forthphase.data.repository;

import ir.maktab.forthphase.data.model.Order;
import ir.maktab.forthphase.data.model.enums.OrderStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;
import java.util.Set;

public interface OrderRepository extends JpaRepository<Order, Long> {

    Set<Order> findBySubServiceName(String subServiceName);

    Optional<Order> findByOrderCode(String orderCode);

    Set<Order> findByOrderStatusAndSubServiceName(OrderStatus orderStatus, String subServiceName);

    @Query(value = "select count(id) from order_table where order_status='DONE' and accepted_expert_email= ?1",
            nativeQuery = true)
    int findCountDoneStatusOrdersByExpertEmail(String expertEmail);
}
