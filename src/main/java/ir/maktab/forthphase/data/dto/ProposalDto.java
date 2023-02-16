package ir.maktab.forthphase.data.dto;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@NoArgsConstructor
public class ProposalDto {
    double cost;
    String neededTime;
    String orderCode;
    String customerEmail;
    Date timeOfDoing;
}
