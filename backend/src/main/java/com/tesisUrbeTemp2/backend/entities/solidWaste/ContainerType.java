package com.tesisUrbeTemp2.backend.entities.solidWaste;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity(name = "containerType")
public class ContainerType {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank
    @Column(nullable = false)
    private String name;

    @NotBlank
    @Column(nullable = true)
    private String description;

    public ContainerType(String name, String description) {
        this.name = name;
        this.description = description;
    }
}
