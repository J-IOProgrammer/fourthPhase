package ir.maktab.forthphase.data.dto;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@NoArgsConstructor
public class CustomerOrderDto {

    String description;
    double cost;
    String address;
    String subServiceName;
    String serviceName;
    Date requiredDate;
}
