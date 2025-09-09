package com.tesisUrbeTemp2.backend.solidWasteManagement.controllers;

import com.tesisUrbeTemp2.backend.solidWasteManagement.services.WasteService;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("waste")
public class WasteController {

    private final WasteService wasteService;

    public WasteController(WasteService wasteService) {
        this.wasteService = wasteService;
    }


}
