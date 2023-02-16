package ir.maktab.forthphase.data.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class CustomerLoginDto {

    String firstName;
    String lastName;
    String email;
    String password;
    String nationalCode;
    String address;
}
