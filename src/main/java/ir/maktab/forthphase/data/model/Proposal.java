package ir.maktab.forthphase.data.model;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.util.Date;

@Entity
@AllArgsConstructor
@Getter
@Setter
@NoArgsConstructor
@Builder
public class Proposal extends BaseEntity {

    private double cost;

    String orderCode;

    String customerEmail;

    String expertEmail;

    @ManyToOne(fetch = FetchType.LAZY)
    private Order order;

    @Column(columnDefinition = "double precision default 0")
    double expertRate;

    private String neededTime;

    @CreationTimestamp
    @Temporal(TemporalType.DATE)
    private Date writeInDate;

    @Temporal(TemporalType.TIME)
    private Date timeOfStart;

    @Temporal(TemporalType.DATE)
    private Date timeOfDoing;

    @Override
    public String toString() {
        return "Proposal{" +
                "cost=" + cost +
                ", orderCode='" + orderCode + '\'' +
                ", customerEmail='" + customerEmail + '\'' +
                ", expertEmail='" + expertEmail + '\'' +
                ", expertRate=" + expertRate +
                ", neededTime='" + neededTime + '\'' +
                ", writeInDate=" + writeInDate +
                ", timeOfDoing=" + timeOfDoing +
                ", timeOfStart=" + timeOfStart +
                '}' + "\n";
    }
}
