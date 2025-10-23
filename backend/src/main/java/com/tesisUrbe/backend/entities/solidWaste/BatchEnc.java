package com.tesisUrbe.backend.entities.solidWaste;

import com.tesisUrbe.backend.entities.account.User;
import com.tesisUrbe.backend.solidWasteManagement.enums.BatchStatus;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "batch_enc")
public class BatchEnc {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "creation_date", nullable = false, updatable = false)
    private LocalDateTime creationDate;

    @Column(name = "total_weight", nullable = false)
    private BigDecimal totalWeight = BigDecimal.ZERO;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private BatchStatus status;

    @Column(length = 255)
    private String description;

    @ManyToOne
    @JoinColumn(name = "created_by_user_id", nullable = false)
    private User createdBy;

    @ManyToOne
    @JoinColumn(name = "processed_by_user_id")
    private User processedBy;

    @Column(name = "processed_at")
    private LocalDateTime processedAt;

    @OneToMany(mappedBy = "batchEnc", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<BatchReg> details = new ArrayList<>();

    @Column(nullable = false)
    private boolean deleted = false;
}
