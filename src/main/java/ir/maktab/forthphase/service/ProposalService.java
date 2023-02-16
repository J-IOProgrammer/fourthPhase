package ir.maktab.forthphase.service;

import ir.maktab.forthphase.data.model.Proposal;
import ir.maktab.forthphase.data.repository.ProposalRepository;
import ir.maktab.forthphase.exceptions.NoSuchProposalFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Set;

@Service
@RequiredArgsConstructor
@Transactional
public class ProposalService {

    private final ProposalRepository proposalRepository;

    public Set<Proposal> getProposalsByOrderCode(String orderCode) {
        return proposalRepository.findProposalsByOrderCode(orderCode);
    }

    public Proposal findProposalByOrderCodeAndExpertEmail(String orderCode, String expertEmail) {
        return proposalRepository
                .findProposalByOrderCodeAndExpertEmail(orderCode, expertEmail)
                .orElseThrow(NoSuchProposalFoundException::new);
    }

    public Set<Proposal> showProposalsOrderedByCost(String orderCode) {
        return proposalRepository.findProposalsByOrderCodeOrderByCost(orderCode);
    }

    public Set<Proposal> showProposalsOrderedByExpertRate(String orderCode) {
        return proposalRepository.findProposalsByOrderCodeOrderByExpertRate(orderCode);
    }

    public void saveEditedProposal(Proposal proposal) {
        proposalRepository.save(proposal);
    }
}
