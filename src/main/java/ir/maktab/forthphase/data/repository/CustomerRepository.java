package ir.maktab.forthphase.data.repository;

import ir.maktab.thirdphase.data.dto.searchrequest.UserSearchRequest;
import ir.maktab.thirdphase.data.model.Customer;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public interface CustomerRepository extends JpaRepository<Customer, Long>, JpaSpecificationExecutor<Customer> {

    Optional<Customer> findByEmail(String email);

    static Specification<Customer> searchFilter(UserSearchRequest request) {
        return (root, query, builder) -> {

            List<Predicate> predicates = new ArrayList<>();

            if (request.getFirstName() != null) {
                Predicate firstNamePredicate = builder
                        .like(root.get("firstName"), "%" + request.getFirstName() + "%");
                predicates.add(firstNamePredicate);
            }
            if (request.getLastName() != null) {
                Predicate lastNamePredicate = builder
                        .like(root.get("lastName"), "%" + request.getLastName() + "%");
                predicates.add(lastNamePredicate);
            }
            if (request.getEmail() != null) {
                Predicate emailNamePredicate = builder
                        .equal(root.get("email"), request.getEmail());
                predicates.add(emailNamePredicate);
            }

            query.where(builder.or(predicates.toArray(new Predicate[0])));

            return builder.or(predicates.toArray(new Predicate[0]));
        };
    }


    List<Customer> findAll(Specification<Customer> specification);
}
