package ir.maktab.forthphase.data.dto.searchrequest;

import ir.maktab.forthphase.data.model.enums.OrderStatus;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class OrderSearchRequest {

    private String customerEmail;
    private String startDate;
    private String endDate;
    private OrderStatus status;
    private String serviceName;
    private String subServiceName;

}
