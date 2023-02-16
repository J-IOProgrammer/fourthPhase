package ir.maktab.forthphase.data.model;

import ir.maktab.forthphase.data.model.enums.OrderStatus;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.util.Date;
import java.util.HashSet;
import java.util.Set;

@Entity
@AllArgsConstructor
@Getter
@Setter
@NoArgsConstructor
@Builder
@Table(name = "order_table")
public class Order extends BaseEntity {

    private String description;

    private double cost;

    @CreationTimestamp
    @Temporal(TemporalType.DATE)
    private Date writeInDate;

    private String address;

    @ManyToOne(fetch = FetchType.LAZY)
    private Customer customer;

    @Enumerated(EnumType.STRING)
    private OrderStatus orderStatus;

    private String subServiceName;

    private String acceptedExpertEmail;

    private String serviceName;

    @Column(unique = true)
    private String orderCode;

    @Temporal(TemporalType.DATE)
    private Date requiredDate;

    @OneToOne(cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    @JoinColumn(name = "opinion_id")
    Opinion opinion = new Opinion();

    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<Proposal> proposals = new HashSet<>();

    @Override
    public String toString() {
        return "Order{" +
                "description='" + description + '\'' +
                ", cost=" + cost +
                ", writeInDate=" + writeInDate +
                ", address='" + address + '\'' +
                ", orderStatus=" + orderStatus +
                ", subServiceName='" + subServiceName + '\'' +
                ", acceptedExpertEmail='" + acceptedExpertEmail + '\'' +
                ", serviceName='" + serviceName + '\'' +
                ", orderCode='" + orderCode + '\'' +
                ", requiredDate=" + requiredDate +
                '}' + "\n";
    }
}
