package ir.maktab.forthphase.data.dto;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class ExpertSaveRequestDto {

    String firstName;
    String lastName;
    String email;
    String password;
    String aboutMe;
    String nationalCode;
    String image;
}
