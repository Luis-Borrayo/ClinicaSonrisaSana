package com.luisborrayo.clinicasonrisasana.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;

@Entity
@Table(name = "facturas")
public class Facturas {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;



}
