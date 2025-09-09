package com.tesisUrbeTemp2.backend.solidWasteManagement.services;

import com.tesisUrbeTemp2.backend.solidWasteManagement.repository.ContainerRepository;
import org.springframework.stereotype.Service;

@Service
public class ContainerService {
    private ContainerRepository containerRepository;

    public ContainerService(ContainerRepository containerRepository) {
        this.containerRepository = containerRepository;
    }
}
