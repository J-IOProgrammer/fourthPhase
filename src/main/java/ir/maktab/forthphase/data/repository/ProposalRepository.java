package ir.maktab.forthphase.data.repository;

import ir.maktab.forthphase.data.model.Proposal;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.Set;

public interface ProposalRepository extends JpaRepository<Proposal, Long> {

    Set<Proposal> findProposalsByOrderCode(String orderCode);

    Optional<Proposal> findProposalByOrderCodeAndExpertEmail(String orderCode, String expertEmail);

    Set<Proposal> findProposalsByOrderCodeOrderByCost(String orderCode);

    Set<Proposal> findProposalsByOrderCodeOrderByExpertRate(String orderCode);
}
