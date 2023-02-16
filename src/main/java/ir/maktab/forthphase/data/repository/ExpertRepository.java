package ir.maktab.forthphase.data.repository;

import ir.maktab.thirdphase.data.dto.searchrequest.ExpertSearchRequest;
import ir.maktab.thirdphase.data.model.Expert;
import ir.maktab.thirdphase.data.model.enums.ExpertStatus;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public interface ExpertRepository extends JpaRepository<Expert, Long>, JpaSpecificationExecutor<Expert> {
    Optional<Expert> findByEmail(String email);

    @Query(value = "SELECT * FROM expert WHERE rating = (SELECT MAX(rating) FROM expert)", nativeQuery = true)
    Optional<Expert> findMaxRating();

    @Query(value = "SELECT * FROM expert WHERE rating = (SELECT MIN(rating) FROM expert)", nativeQuery = true)
    Optional<Expert> findMinRating();

    @Query("from Expert where expertStatus= ?1")
    List<Expert> findExpertsByExpertStatus(ExpertStatus expertStatus);

    @Query(value = "select * from expert left join expert_sub_services ess on expert.id = ess.expert_id where sub_service_id = ?1", nativeQuery = true)
    List<Expert> findExpertsBySubServicesName(Long subServiceId);

    static Specification<Expert> searchFilter(ExpertSearchRequest request) {
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

    List<Expert> findAll(Specification<Expert> specification);
}
