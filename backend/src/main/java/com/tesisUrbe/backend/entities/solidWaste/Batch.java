package com.tesisUrbe.backend.entities.solidWaste;

import com.tesisUrbe.backend.solidWasteManagement.enums.BatchStatus;
import jakarta.persistence.*;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.PastOrPresent;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "batch")
public class Batch {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @PastOrPresent
    @Column(name = "creation_date", nullable = false)
    private LocalDate creationDate;

    @Column(name = "description", columnDefinition = "TEXT")
    private String description;

    @DecimalMin(value = "0.00", inclusive = true)
    @Digits(integer = 10, fraction = 2)
    @Column(name = "total_weight", nullable = false)
    private BigDecimal totalWeight;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private BatchStatus status;

    @Column(name = "shipping_date")
    private LocalDate shippingDate;
}
