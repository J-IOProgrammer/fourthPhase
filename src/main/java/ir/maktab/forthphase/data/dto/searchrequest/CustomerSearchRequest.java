package ir.maktab.forthphase.data.dto.searchrequest;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CustomerSearchRequest {

    private String firstName;
    private String lastName;
    private String email;
    private String nationalCode;
}
