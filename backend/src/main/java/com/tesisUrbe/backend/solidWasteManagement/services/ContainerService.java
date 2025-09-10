package com.tesisUrbe.backend.solidWasteManagement.services;

import com.tesisUrbe.backend.solidWasteManagement.repository.ContainerRepository;
import org.springframework.stereotype.Service;

@Service
public class ContainerService {
    private ContainerRepository containerRepository;

    public ContainerService(ContainerRepository containerRepository) {
        this.containerRepository = containerRepository;
    }
}
