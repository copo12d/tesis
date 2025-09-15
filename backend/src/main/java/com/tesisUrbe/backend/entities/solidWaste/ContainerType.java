package com.tesisUrbe.backend.entities.solidWaste;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.*;

@Entity
@Table(name = "containerType")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ContainerType {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank
    @Column(nullable = false)
    private String name;

    @Column(nullable = true)
    private String description;

    @Column(name = "deleted", nullable = false)
    private boolean deleted = false;

}
