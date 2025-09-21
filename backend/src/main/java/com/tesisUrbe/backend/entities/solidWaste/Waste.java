package com.tesisUrbe.backend.entities.solidWaste;

import com.tesisUrbe.backend.entities.account.User;
import jakarta.persistence.*;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Digits;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Table(name = "waste")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Waste {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @DecimalMin(value = "0.01", inclusive = true)
    @Digits(integer = 10, fraction = 2)
    @Column(nullable = false)
    private BigDecimal weight;

    @Column(name = "collection_date", nullable = false, updatable = false)
    private LocalDate collectionDate;

    @ManyToOne(fetch = FetchType.EAGER, optional = false)
    @JoinColumn(name = "container_id", nullable = false)
    private Container container;

    @ManyToOne(fetch = FetchType.EAGER, optional = false)
    @JoinColumn(name = "batch_id", nullable = false)
    private BatchEnc batch;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "created_by_user_id", nullable = false, updatable = false)
    private User createdBy;

    @Column(nullable = false)
    private boolean deleted = false;

    @Column(name = "updated_at")
    private LocalDate updatedAt;
}
