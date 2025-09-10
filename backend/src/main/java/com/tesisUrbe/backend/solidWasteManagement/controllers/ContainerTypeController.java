package com.tesisUrbe.backend.solidWasteManagement.controllers;

import com.tesisUrbe.backend.solidWasteManagement.dto.NewContainerTypeDto;
import com.tesisUrbe.backend.solidWasteManagement.services.ContainerTypeService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.client.HttpClientErrorException;

import java.util.Map;

@Controller
@RequestMapping("/container-type")
public class ContainerTypeController {

    private ContainerTypeService containerTypeService;

    public ContainerTypeController(ContainerTypeService containerTypeService) {
        this.containerTypeService = containerTypeService;
    }

   /* @PreAuthorize("hasRole('ADMIN') or hasRole('SUPERUSER')")
    @PostMapping("/register")
    public ResponseEntity<Map<String, String>> registerContainerType(@Valid @RequestBody NewContainerTypeDto newContainerTypeDto, BindingResult bindingResult, Authentication authentication){
        System.out.println("nad");
        if (bindingResult.hasErrors()) {
            return ResponseEntity
                    .badRequest()
                    .body(ErrorUtils.errorMap(bindingResult));
        }
        try{
            containerTypeService.registerContainerType(newContainerTypeDto);
            System.out.println("intentando registrar el container type");
            return ResponseEntity
                    .status(HttpStatus.CREATED)
                    .body(Map.of("message", "Tipo de contenedor registrado exitosamente"));
        } catch (HttpClientErrorException.Unauthorized e){
            return ResponseEntity
                    .status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("Acceso denegado: ", e.getMessage()));
        }
        catch (Exception e) {
            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("Error", "Error interno: "+e.getMessage()));
        }
    }
*/

}
