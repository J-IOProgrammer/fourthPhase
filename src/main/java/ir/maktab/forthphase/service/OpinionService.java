package ir.maktab.forthphase.service;

import ir.maktab.thirdphase.data.repository.OpinionRepository;
import ir.maktab.thirdphase.util.OpinionUtil;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class OpinionService {

    private final OpinionRepository opinionRepository;

    public double calcExpertRate(String expertEmail) {
        return opinionRepository.calcRateOfExpert(expertEmail);
    }

    public List<String> getExpertOpinions(String expertEmail) {
        return OpinionUtil.convertOpinionToExpertOpinionDto(
                opinionRepository
                        .findOpinionsByExpert_Email(expertEmail));
    }
}
