package com.kimsreviews.API.models;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.Date;

@Entity
@Table(name = "bird_records")
@Data
@NoArgsConstructor
public class BirdRecord {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String birdType;
    private Integer count;
    private LocalDate hatchDate;
    private Date date;

    private boolean sold;

    @Column(name = "next_vaccine")
    private String nextVaccine;

    @Column(name = "next_vaccine_date")
    private LocalDate nextVaccineDate;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)

    public int calculateAgeInDays() {
        return (int) (LocalDate.now().toEpochDay() - hatchDate.toEpochDay());
    }

    public void sell() {
        this.sold = true;
    }

    public void unsell() {
        this.sold = false;
    }
}

