package com.tesisUrbeTemp2.backend.solidWasteManagement.services;

import com.tesisUrbeTemp2.backend.solidWasteManagement.repository.BatchRepository;
import org.springframework.stereotype.Service;

@Service
public class BatchService {
    private BatchRepository batchRepository;

    public BatchService(BatchRepository batchRepository) {
        this.batchRepository = batchRepository;
    }

}
