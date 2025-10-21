package com.tesisUrbe.backend.prediction.model;

import java.time.LocalDateTime;

import com.tesisUrbe.backend.entities.account.User;
import com.tesisUrbe.backend.entities.solidWaste.Container;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "collection_canceleds")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CollectionCanceled {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "container_fill_cycle_id", nullable = false)
    private ContainerFillCycle containerFillCycle;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Builder.Default
    @Column(name = "cancel_time", nullable = false)
    private LocalDateTime cancelTime = LocalDateTime.now();

    @Column(name = "reason", nullable = false)
    private String reason;

}
