package ir.maktab.forthphase.data.dto.searchrequest;

import lombok.*;

import java.util.Date;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class CustomerSearchRequest {

    private String firstName;
    private String lastName;
    private String email;
    private String nationalCode;
    private Date registerDate;
    private int sendOrdersCount;
}
