package ir.maktab.forthphase.data.model;

import jakarta.persistence.Entity;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
@NoArgsConstructor
@Entity
public class Admin extends BaseEntity {

    private final String username = "root";

    private final String password = "1234";
}
