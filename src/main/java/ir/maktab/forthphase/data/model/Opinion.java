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
public class Opinion extends BaseEntity {

    @Column(columnDefinition = "double precision default 0")
    private double rate;

    private String opinionText;

    @ManyToOne(fetch = FetchType.LAZY)
    private Expert expert;

    @CreationTimestamp
    @Temporal(TemporalType.DATE)
    private Date recordDate;

    public Opinion(double rate, String opinionText) {
        this.rate = rate;
        this.opinionText = opinionText;
    }

    @Override
    public String toString() {
        return "Opinion{" +
                "rate=" + rate +
                ", opinionText='" + opinionText + '\'' +
                ", recordDate=" + recordDate +
                '}' + "\n";
    }
}
