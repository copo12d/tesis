package com.tesisUrbe.backend.entities.solidWaste;

import com.tesisUrbe.backend.entities.account.User;
import com.tesisUrbe.backend.solidWasteManagement.enums.ContainerStatus;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "container")
public class Container {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull
    @Column(nullable = false)
    private BigDecimal latitude;

    @NotNull
    @Column(nullable = false)
    private BigDecimal longitude;

    @NotNull
    @Column(nullable = false)
    private BigDecimal capacity;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ContainerStatus status;

    @ManyToOne(fetch = FetchType.EAGER, optional = false)
    @JoinColumn(name = "container_type_id", nullable = false)
    private ContainerType containerType;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "created_by_user_id", nullable = false, updatable = false)
    private User createdBy;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "deleted", nullable = false)
    private boolean deleted = false;

}
