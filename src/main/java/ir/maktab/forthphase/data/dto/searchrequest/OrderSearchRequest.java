package ir.maktab.forthphase.data.dto.searchrequest;

import ir.maktab.forthphase.data.model.enums.OrderStatus;
import lombok.*;

import java.util.Date;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class OrderSearchRequest {

    private String customerEmail;
    private Date startDate;
    private Date endDate;
    private OrderStatus status;
    private String serviceName;
    private String subServiceName;

}
