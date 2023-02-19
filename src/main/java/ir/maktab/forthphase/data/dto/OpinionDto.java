package ir.maktab.forthphase.data.dto;

import jakarta.validation.constraints.Pattern;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class OpinionDto {

    @Pattern(regexp = "^[0-4].[0-9]|^[0-5]$")
    double rate;
    String opinionText;

}
