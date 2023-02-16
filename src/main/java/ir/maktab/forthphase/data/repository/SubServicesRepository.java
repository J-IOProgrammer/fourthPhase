package ir.maktab.forthphase.data.repository;

import ir.maktab.forthphase.data.model.SubServices;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface SubServicesRepository extends JpaRepository<SubServices, Long> {
    Optional<SubServices> findByName(String subServiceName);

    @Query(value = "select id from sub_services where name = ?1", nativeQuery = true)
    Long getSubServiceIdByName(String subServiceName);
}
