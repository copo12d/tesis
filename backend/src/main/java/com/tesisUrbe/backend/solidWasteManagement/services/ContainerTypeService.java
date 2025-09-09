package com.tesisUrbe.backend.solidWasteManagement.services;

import com.tesisUrbe.backend.solidWasteManagement.dto.NewContainerTypeDto;
import com.tesisUrbe.backend.entities.solidWaste.ContainerType;
import com.tesisUrbe.backend.solidWasteManagement.repository.ContainerTypeRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ContainerTypeService {
    private ContainerTypeRepository containerTypeRepository;

    public ContainerTypeService(ContainerTypeRepository containerTypeRepository) {
        this.containerTypeRepository = containerTypeRepository;
    }

    public void registerContainerType(NewContainerTypeDto newContainerTypeDto) {
        containerTypeRepository.save(new ContainerType(newContainerTypeDto.getName(), newContainerTypeDto.getDescription()));
        System.out.println("New container type registered: " + newContainerTypeDto.getName());
    }

    public List<ContainerType> getAllContainerTypes() {
        return containerTypeRepository.findAll();
    }

    public ContainerType getContainerTypeById(Long id) {
        return containerTypeRepository.findById(id).get();
    }

    public ContainerType updateContainerType(Long id, NewContainerTypeDto newContainerTypeDto) {
        ContainerType containerType = containerTypeRepository.findById(id).get();
        containerType.setName(newContainerTypeDto.getName());
        containerType.setDescription(newContainerTypeDto.getDescription());
        return containerTypeRepository.save(containerType);
    }

    //manejar soft delete
    public void deleteContainerType(Long id) {
        containerTypeRepository.deleteById(id);
    }
}
