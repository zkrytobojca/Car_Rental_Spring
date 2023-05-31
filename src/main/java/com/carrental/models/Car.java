package com.carrental.models;

import lombok.*;

import javax.persistence.*;
import java.math.BigDecimal;
import java.util.Base64;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "CR_CAR")
public class Car extends AuditModel {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    @Column(nullable = false)
    private String model;
    @Column(nullable = false)
    private String brand;
    @Column(nullable = false)
    private int mileage;
    @Column(name = "production_year", nullable = false)
    private int productionYear;
    @Column(nullable = false)
    private boolean available;
    @Column(nullable = false)
    private BigDecimal rentPrice;
    @Lob
    private byte[] image;
    @OneToOne(mappedBy = "car", cascade = CascadeType.REMOVE)
    private Rent rent;

    public String getImageEncoded() {
        byte[] encode = image == null ? new byte[0] : Base64.getEncoder().encode(image);
        return new String(encode);
    }

    @Override
    public String toString() {
        return "Car{" +
                "id=" + id +
                ", model='" + model + '\'' +
                ", brand='" + brand + '\'' +
                ", mileage=" + mileage +
                ", productionYear=" + productionYear +
                ", available=" + available +
                ", rentPrice=" + rentPrice +
                '}';
    }
}


