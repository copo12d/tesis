package com.tesisUrbe.backend.solidWasteManagement.services;

import com.tesisUrbe.backend.solidWasteManagement.repository.WasteRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class WasteService {
    private WasteRepository wasteRepository;
}
