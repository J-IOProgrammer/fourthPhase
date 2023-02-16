package ir.maktab.forthphase.data.model;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.OneToMany;
import lombok.*;

import java.util.HashSet;
import java.util.Set;

@Entity
@AllArgsConstructor
@Getter
@Setter
@NoArgsConstructor
@Builder
public class Services extends BaseEntity {

    private String name;

    @OneToMany(mappedBy = "services", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<SubServices> subServices = new HashSet<>();

    @Override
    public String toString() {
        return "Services{" +
                "name='" + name + '\'' +
                "subServices=" + this.getSubServices() +
                '}' + "\n";
    }
}
