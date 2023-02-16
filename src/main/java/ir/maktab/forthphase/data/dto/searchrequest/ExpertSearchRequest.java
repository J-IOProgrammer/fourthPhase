package ir.maktab.forthphase.data.dto.searchrequest;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ExpertSearchRequest extends UserSearchRequest {

    private String status;
    private String subServiceName;

    public ExpertSearchRequest(String firstName, String lastName, String email, String status, String subServiceName) {
        super(firstName, lastName, email);
        this.status = status;
        this.subServiceName = subServiceName;
    }
}
