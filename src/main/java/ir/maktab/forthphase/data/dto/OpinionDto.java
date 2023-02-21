package ir.maktab.forthphase.data.dto;

import jakarta.validation.constraints.Pattern;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@Getter
@Setter
public class OpinionDto {

    @Pattern(regexp = "\\^[0-4].[0-9]|^[0-5]$")
    String rate;
    String opinionText;

}
