package ir.maktab.forthphase.data.model;

import ir.maktab.forthphase.data.model.enums.Role;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.hibernate.annotations.CreationTimestamp;

import java.util.Date;

@Getter
@Setter
@ToString
@NoArgsConstructor
@MappedSuperclass
public class Person extends BaseEntity {

    private String firstName;

    private String lastName;

    @Column(unique = true, nullable = false)
    private String email;

    private String password;

    @CreationTimestamp
    @Temporal(TemporalType.DATE)
    private Date registerDate;

    private double credit = 9999999;

    @Column(length = 10, unique = true)
    private String nationalCode;

    @Enumerated(EnumType.STRING)
    private Role role;
}
