package com.tesisUrbe.backend.settingsManagement.controllers;

import com.tesisUrbe.backend.common.exception.ApiResponse;
import com.tesisUrbe.backend.entities.setting.ReportSetting;
import com.tesisUrbe.backend.entities.setting.UbicationSetting;
import com.tesisUrbe.backend.entities.setting.UniversitySetting;
import com.tesisUrbe.backend.settingsManagement.services.SettingsService;
import lombok.AllArgsConstructor;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@AllArgsConstructor
@RestController
@RequestMapping("${api.base-path}/settings/admin")
@PreAuthorize("hasAnyRole('ROLE_ADMIN','ROLE_SUPERUSER')")
public class SettingsController {

    private final SettingsService settingsService;

    @GetMapping("/report")
    public ResponseEntity<ApiResponse<ReportSetting>> getReportSetting() {
        ApiResponse<ReportSetting> response = settingsService.getReportSetting();
        return ResponseEntity.status(HttpStatus.valueOf(response.meta().status())).body(response);
    }

    @PutMapping("/report")
    public ResponseEntity<ApiResponse<Void>> updateReportSetting(@RequestBody ReportSetting report) {
        ApiResponse<Void> response = settingsService.updateReportSetting(report);
        return ResponseEntity.status(HttpStatus.valueOf(response.meta().status())).body(response);
    }

    @GetMapping("/university")
    public ResponseEntity<ApiResponse<UniversitySetting>> getUniversitySetting() {
        ApiResponse<UniversitySetting> response = settingsService.getUniversitySetting();
        return ResponseEntity.status(HttpStatus.valueOf(response.meta().status())).body(response);
    }

    @PutMapping("/university")
    public ResponseEntity<ApiResponse<Void>> updateUniversitySetting(@RequestBody UniversitySetting university) {
        ApiResponse<Void> response = settingsService.updateUniversitySetting(university);
        return ResponseEntity.status(HttpStatus.valueOf(response.meta().status())).body(response);
    }

    @PostMapping(value = "/university/logo", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ApiResponse<Void>> replaceLogo(@RequestPart("file") MultipartFile file) {
        ApiResponse<Void> response = settingsService.replaceLogo(file);
        return ResponseEntity.status(HttpStatus.valueOf(response.meta().status())).body(response);
    }

    @GetMapping(value = "/university/logo", produces = MediaType.IMAGE_PNG_VALUE)
    public ResponseEntity<Resource> getLogoImage() {
        try {
            Resource image = settingsService.getLogoImage();
            return ResponseEntity.ok(image);
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }

    @GetMapping("/ubication")
    public ResponseEntity<ApiResponse<UbicationSetting>> getUbicationSetting() {
        ApiResponse<UbicationSetting> response = settingsService.getUbicationSetting();
        return ResponseEntity.status(HttpStatus.valueOf(response.meta().status())).body(response);
    }

    @PutMapping("/ubication")
    public ResponseEntity<ApiResponse<Void>> updateUbicationSetting(@RequestBody UbicationSetting ubication) {
        ApiResponse<Void> response = settingsService.updateUbicationSetting(ubication);
        return ResponseEntity.status(HttpStatus.valueOf(response.meta().status())).body(response);
    }

}
