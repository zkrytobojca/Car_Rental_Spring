package com.carrental.models;

import com.carrental.models.enums.RentState;
import lombok.*;
import org.apache.commons.lang3.time.DateUtils;

import javax.persistence.*;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.Date;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "CR_RENT")
public class Rent extends AuditModel {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long id;

    @OneToOne(cascade = CascadeType.DETACH)
    private Car car;

    @ManyToOne(cascade = CascadeType.DETACH)
    private User user;

    @Column(name = "rent_date", nullable = false)
    private Date rentDate;

    @Column(name = "rent_length", nullable = false)
    private int rentLength;

    @Column(nullable = false)
    private BigDecimal payment;

    @Column(name = "state")
    @Enumerated(EnumType.STRING)
    private RentState state;

    public String getEndRentDateString() {
        long endTime = rentDate.getTime() + rentLength * DateUtils.MILLIS_PER_DAY;
        return new SimpleDateFormat("yyyy-MM-dd HH:MM:ss").format(new Date(endTime));
    }

    @PreRemove
    public void removeRent() {
        car.setAvailable(!car.isAvailable());
    }

    @Override
    public String toString() {
        return "Rent{" +
                "id=" + id +
                ", car=" + car +
                ", user=" + user +
                ", rentDate=" + rentDate +
                ", rentLength=" + rentLength +
                ", payment=" + payment +
                ", state=" + state +
                '}';
    }
}
