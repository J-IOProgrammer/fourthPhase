package ir.maktab.forthphase.data.model;

import jakarta.persistence.*;
import lombok.*;

import java.util.HashSet;
import java.util.Set;

@Entity
@AllArgsConstructor
@Getter
@Setter
@NoArgsConstructor
@Builder
public class SubServices extends BaseEntity {

    private String name;

    private double basePrice;

    private String description;

    @ManyToOne(fetch = FetchType.LAZY)
    private Services services;

    @ManyToMany(mappedBy = "subServices", cascade = CascadeType.ALL)
    private Set<Expert> expertList = new HashSet<>();

    @Override
    public String toString() {
        return "SubServices{" +
                "name='" + name + '\'' +
                ", basePrice=" + basePrice + '\'' +
                ", description='" + description + '\'' +
                '}' + "\n";
    }
}
