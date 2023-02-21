package ir.maktab.forthphase.data.repository;

import ir.maktab.forthphase.data.dto.searchrequest.CustomerSearchRequest;
import ir.maktab.forthphase.data.model.Customer;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public interface CustomerRepository extends JpaRepository<Customer, Long>, JpaSpecificationExecutor<Customer> {

    Optional<Customer> findByEmail(String email);

    static Specification<Customer> searchFilter(CustomerSearchRequest request) {
        return (root, query, builder) -> {

            List<Predicate> predicates = new ArrayList<>();

            if (request.getFirstName() != null) {
                String firstName = "%" + request.getFirstName() + "%";
                predicates.add(builder.like(root.get("firstName"), firstName));
            }
            if (request.getLastName() != null) {
                String lastName = "%" + request.getLastName() + "%";
                predicates.add(builder.like(root.get("lastName"), lastName));
            }
            if (request.getEmail() != null) {
                String email = "%" + request.getEmail() + "%";
                predicates.add(builder.like(root.get("email"), email));
            }
            if (request.getNationalCode() != null) {
                String nationalCode = "%" + request.getNationalCode() + "%";
                predicates.add(builder.like(root.get("nationalCode"), nationalCode));
            }
            query.where(builder.or(predicates.toArray(new Predicate[0])));

            return builder.or(predicates.toArray(new Predicate[0]));
        };
    }

    List<Customer> findAll(Specification<Customer> specification);
}
