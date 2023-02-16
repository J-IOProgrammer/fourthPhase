package ir.maktab.forthphase.data.repository;

import ir.maktab.forthphase.data.model.Customer;
import ir.maktab.forthphase.data.model.Order;
import ir.maktab.forthphase.data.model.enums.OrderStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;
import java.util.Set;

public interface OrderRepository extends JpaRepository<Order, Long> {

    Set<Order> findBySubServiceName(String subServiceName);

    Optional<Order> findByOrderCode(String orderCode);

    Set<Order> findByOrderStatusAndSubServiceName(OrderStatus orderStatus, String subServiceName);

    @Query(value = "select * from customer c inner join order_table o on c.id = o.customer_id\n" +
            "         where order_code= ?1", nativeQuery = true)
    Optional<Customer> findCustomerEmailByOrderCode(String orderCode);

}
