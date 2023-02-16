package ir.maktab.forthphase.util;

import ir.maktab.thirdphase.data.model.Opinion;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@RequiredArgsConstructor
@Component
public class OpinionUtil {

    public static List<String> convertOpinionToExpertOpinionDto(List<Opinion> opinions) {
        List<String> opinionList = new ArrayList<>();
        for (Opinion opinion : opinions) {
            opinionList.add(opinion.getRate() + " in : " + opinion.getRecordDate());
        }
        return opinionList;
    }

}
