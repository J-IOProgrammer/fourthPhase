package ir.maktab.forthphase.data.dto.searchrequest;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ExpertSearchRequest {

    private String firstName;
    private String lastName;
    private String email;
    private String status;
    private String subServiceName;
    private boolean isActive;
    private String nationalCode;
}
