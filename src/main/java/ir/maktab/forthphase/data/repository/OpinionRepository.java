package ir.maktab.forthphase.data.repository;

import ir.maktab.forthphase.data.model.Opinion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface OpinionRepository extends JpaRepository<Opinion, Long> {

    @Query("select avg(opi.rate) from Opinion opi")
    double calcRateOfExpert(String expertEmail);

    List<Opinion> findOpinionsByExpert_Email(String expertEmail);
}
