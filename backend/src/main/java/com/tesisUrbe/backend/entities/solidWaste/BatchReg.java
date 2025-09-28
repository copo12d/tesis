package com.tesisUrbe.backend.entities.solidWaste;

import com.tesisUrbe.backend.entities.account.User;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@Entity
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "batch_reg")
public class BatchReg {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "collection_date", nullable = false)
    private LocalDateTime collectionDate;

    @Column(nullable = false)
    private BigDecimal weight;

    @ManyToOne
    @JoinColumn(name = "container_id", nullable = false)
    private Container container;

    @ManyToOne
    @JoinColumn(name = "batch_enc_id", nullable = false)
    private BatchEnc batchEnc;

    @ManyToOne
    @JoinColumn(name = "created_by_user_id", nullable = false)
    private User createdBy;

    @Column(nullable = false)
    private boolean deleted = false;
}
