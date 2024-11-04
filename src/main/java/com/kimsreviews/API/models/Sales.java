package com.kimsreviews.API.models;


import jakarta.persistence.*;
import lombok.Data;

import java.util.Date;

@Entity
@Data
@Table(name = "sales")
public class Sales {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Date date;

    @Column(name = "item_sold")
    private String birdType;

    @Column(name = "sales_amount")
    private Double sales;

    private int eggProduction;
    private int feedConsumption;
    private int expenses;
    private String hatchDate;
    private int totalBirds;
    private int newFlock;
    private int mortalities;
    private String hatchData;
    private int birdCount;
    private boolean sold;

}
