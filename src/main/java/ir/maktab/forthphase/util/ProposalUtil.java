package ir.maktab.forthphase.util;

import ir.maktab.forthphase.data.model.Proposal;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.Set;

@RequiredArgsConstructor
@Component
public class ProposalUtil {

    public static boolean isOkPriceOfProposal(double expertPrice, double subServiceBaseCost) {
        return expertPrice >= subServiceBaseCost;
    }

    public static boolean isOkTimeOfProposal(Date requiredDate, Date submitDate) {
        return requiredDate.compareTo(submitDate) < 0;
    }

    public static Proposal findProposalByExpertEmail(Set<Proposal> proposalSet, String expertEmail) {
        for (Proposal proposal : proposalSet) {
            if (proposal.getExpertEmail().equals(expertEmail))
                return proposal;
        }
        return null;
    }
}
