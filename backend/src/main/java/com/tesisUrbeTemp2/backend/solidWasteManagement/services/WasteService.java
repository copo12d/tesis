package com.tesisUrbeTemp2.backend.solidWasteManagement.services;

import com.tesisUrbeTemp2.backend.solidWasteManagement.repository.WasteRepository;
import org.springframework.stereotype.Service;

@Service
public class WasteService {
    private WasteRepository wasteRepository;

    public WasteService(WasteRepository wasteRepository) {
        this.wasteRepository = wasteRepository;
    }
}
