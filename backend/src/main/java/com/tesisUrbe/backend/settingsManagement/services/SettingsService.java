package com.tesisUrbe.backend.settingsManagement.services;

import com.tesisUrbe.backend.common.exception.ApiError;
import com.tesisUrbe.backend.common.exception.ApiErrorFactory;
import com.tesisUrbe.backend.common.exception.ApiResponse;
import com.tesisUrbe.backend.entities.setting.ReportSetting;
import com.tesisUrbe.backend.entities.setting.UbicationSetting;
import com.tesisUrbe.backend.entities.setting.UniversitySetting;
import com.tesisUrbe.backend.settingsManagement.repository.ReportSettingRepository;
import com.tesisUrbe.backend.settingsManagement.repository.UbicationSettingRepository;
import com.tesisUrbe.backend.settingsManagement.repository.UniversitySettingRepository;
import io.micrometer.common.util.StringUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

@Slf4j
@Service
@RequiredArgsConstructor
public class SettingsService {

    @Value("${storage.path}")
    private String storagePath;

    private final FileStorageService fileStorageService;
    private final ReportSettingRepository reportRepo;
    private final UniversitySettingRepository universityRepo;
    private final UbicationSettingRepository ubicationRepo;
    private final ApiErrorFactory errorFactory;

    public ApiResponse<ReportSetting> getReportSetting() {
        ReportSetting report = reportRepo.findById(1L).orElseGet(() -> {
            ReportSetting r = new ReportSetting();
            r.setId(1L);
            r.setTableHeaderColor("#0000FF");
            r.setHeaderTextColor("#FFFFFF");
            r.setRecordColor("#000000");
            return reportRepo.save(r);
        });
        return new ApiResponse<>(errorFactory.buildMeta(HttpStatus.OK, "Estilos obtenidos correctamente"), report, null);
    }

    public ApiResponse<Void> updateReportSetting(ReportSetting report) {
        if (!isHex(report.getTableHeaderColor()) || !isHex(report.getHeaderTextColor()) || !isHex(report.getRecordColor())) {
            return errorFactory.build(HttpStatus.BAD_REQUEST, List.of(
                    new ApiError("INVALID_COLOR", "tableHeaderColor", "Formato de color inválido"),
                    new ApiError("INVALID_COLOR", "headerTextColor", "Formato de color inválido"),
                    new ApiError("INVALID_COLOR", "recordColor", "Formato de color inválido")
            ));
        }
        report.setId(1L);
        reportRepo.save(report);
        return errorFactory.buildSuccess(HttpStatus.OK, "Estilos actualizados correctamente");
    }

    private boolean isHex(String color) {
        return Pattern.matches("^#([A-Fa-f0-9]{6})$", color);
    }

    public ApiResponse<UniversitySetting> getUniversitySetting() {
        UniversitySetting university = universityRepo.findById(1L).orElseThrow(() ->
                new IllegalStateException("Configuración institucional no encontrada")
        );
        return new ApiResponse<>(errorFactory.buildMeta(HttpStatus.OK, "Datos institucionales obtenidos correctamente"), university, null);
    }

    public ApiResponse<Void> updateUniversitySetting(UniversitySetting university) {
        List<ApiError> errors = new ArrayList<>();

        if (StringUtils.isBlank(university.getLegalName())) {
            errors.add(new ApiError("REQUIRED", "legal_name", "El nombre legal es obligatorio"));
        }

        if (StringUtils.isBlank(university.getEmail()) || !university.getEmail().contains("@")) {
            errors.add(new ApiError("INVALID_EMAIL", "email", "Correo electrónico inválido"));
        }

        if (university.getTaxId() == null ||
                StringUtils.isBlank(university.getTaxId().getType()) ||
                StringUtils.isBlank(university.getTaxId().getNumber())) {
            errors.add(new ApiError("INVALID_TAX_ID", "tax_id", "RIF inválido"));
        }

        if (!errors.isEmpty()) {
            return errorFactory.build(HttpStatus.BAD_REQUEST, errors);
        }

        university.setId(1L);
        universityRepo.save(university);
        return errorFactory.buildSuccess(HttpStatus.OK, "Datos institucionales actualizados correctamente");
    }

    public ApiResponse<Void> replaceLogo(MultipartFile file) {
        if (file.isEmpty()) {
            return errorFactory.build(HttpStatus.BAD_REQUEST, List.of(
                    new ApiError("EMPTY_FILE", "logo", "El archivo está vacío")
            ));
        }
        if (!file.getContentType().startsWith("image/")) {
            return errorFactory.build(HttpStatus.BAD_REQUEST, List.of(
                    new ApiError("INVALID_TYPE", "logo", "El archivo debe ser una imagen")
            ));
        }

        try {
            Path path = Path.of(storagePath, "logo.png");
            Files.createDirectories(path.getParent());
            Files.write(path, file.getBytes(), StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING);
        } catch (IOException e) {
            return errorFactory.build(HttpStatus.INTERNAL_SERVER_ERROR, List.of(
                    new ApiError("LOGO_SAVE_ERROR", null, "No se pudo guardar el logo")
            ));
        }
        return errorFactory.buildSuccess(HttpStatus.OK, "Logo actualizado correctamente");
    }

    public Resource getLogoImage() throws IOException {
        return fileStorageService.load("logo.png");
    }

    public ApiResponse<UbicationSetting> getUbicationSetting() {
        UbicationSetting ubication = ubicationRepo.findById(1L).orElseThrow(() ->
                new IllegalStateException("Ubicación institucional no encontrada")
        );
        return new ApiResponse<>(errorFactory.buildMeta(HttpStatus.OK, "Ubicación obtenida correctamente"), ubication, null);
    }

    public ApiResponse<Void> updateUbicationSetting(UbicationSetting ubication) {
        List<ApiError> errors = new ArrayList<>();

        if (!isValidCoordinate(ubication.getLatitude())) {
            errors.add(new ApiError("INVALID_COORDINATE", "latitude", "Latitud inválida"));
        }
        if (!isValidCoordinate(ubication.getLongitude())) {
            errors.add(new ApiError("INVALID_COORDINATE", "longitude", "Longitud inválida"));
        }
        if (ubication.getMapZoom() < 1 || ubication.getMapZoom() > 22) {
            errors.add(new ApiError("INVALID_ZOOM", "mapZoom", "Zoom debe estar entre 1 y 22"));
        }

        if (!errors.isEmpty()) {
            return errorFactory.build(HttpStatus.BAD_REQUEST, errors);
        }

        ubication.setId(1L);
        ubicationRepo.save(ubication);
        return errorFactory.buildSuccess(HttpStatus.OK, "Ubicación actualizada correctamente");
    }

    private boolean isValidCoordinate(double value) {
        return value >= -180 && value <= 180;
    }


}
