package ir.maktab.forthphase.data.repository;

import ir.maktab.forthphase.data.dto.searchrequest.OrderSearchRequest;
import ir.maktab.forthphase.data.model.Customer;
import ir.maktab.forthphase.data.model.Order;
import ir.maktab.forthphase.data.model.enums.OrderStatus;
import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import jakarta.persistence.criteria.Subquery;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;

public interface OrderRepository extends JpaRepository<Order, Long> {

    Set<Order> findBySubServiceName(String subServiceName);

    Optional<Order> findByOrderCode(String orderCode);

    Set<Order> findByOrderStatusAndSubServiceName(OrderStatus orderStatus, String subServiceName);

    @Query(value = "select * from customer c inner join order_table o on c.id = o.customer_id\n" +
            "         where o.customer_id = ?1 limit 1", nativeQuery = true)
    Optional<Customer> findCustomerEmailByCustomerId(int customerId);

    static Specification<Order> searchFilter(OrderSearchRequest request) {
        return (root, query, builder) -> {

            List<Predicate> predicates = new ArrayList<>();

            if (request.getServiceName() != null) {
                String serviceName = "%" + request.getServiceName() + "%";
                predicates.add(builder.like(root.get("serviceName"), serviceName));
            }
            if (request.getStatus() != null) {
                String status = "%" + request.getStatus() + "%";
                predicates.add(builder.like(root.get("orderStatus"), status));
            }
            if (request.getSubServiceName() != null) {
                String subServiceName = "%" + request.getSubServiceName() + "%";
                predicates.add(builder.like(root.get("subServiceName"), subServiceName));
            }

            query.where(builder.or(predicates.toArray(new Predicate[0])));
            return builder.or(predicates.toArray(new Predicate[0]));
        };
    }

    List<Order> findAll(Specification<Order> specification);
}
