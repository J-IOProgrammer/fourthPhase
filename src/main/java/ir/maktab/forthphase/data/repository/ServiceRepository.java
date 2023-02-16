package ir.maktab.forthphase.data.repository;

import ir.maktab.forthphase.data.model.Services;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ServiceRepository extends JpaRepository<Services, Long> {
    Optional<Services> findByName(String serviceName);
}
