package ir.maktab.forthphase.data.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import ir.maktab.thirdphase.data.model.enums.ExpertStatus;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ExpertLoginDto {
    String firstName;
    String lastName;
    String email;
    String password;
    ExpertStatus expertStatus;
    Date registerDate;
    double credit;
    String aboutMe;
    double rating;
    boolean isAccepted;
    String nationalCode;
}
