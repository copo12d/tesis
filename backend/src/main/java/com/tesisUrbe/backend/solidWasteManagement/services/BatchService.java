package com.tesisUrbe.backend.solidWasteManagement.services;

import com.tesisUrbe.backend.solidWasteManagement.repository.BatchRepository;
import org.springframework.stereotype.Service;

@Service
public class BatchService {
    private BatchRepository batchRepository;

    public BatchService(BatchRepository batchRepository) {
        this.batchRepository = batchRepository;
    }

}
