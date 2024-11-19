package com.kimsreviews.API.models;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Entity
@Data
//@Data
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "eggs_record")
public class EggsRecord {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "date", nullable = false)
    private LocalDate date;

    @Column(name = "eggs_count", nullable = false)
    private Integer eggsCount;
}

