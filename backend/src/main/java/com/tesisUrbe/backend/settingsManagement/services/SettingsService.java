package com.tesisUrbe.backend.settingsManagement.services;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.tesisUrbe.backend.common.exception.ApiError;
import com.tesisUrbe.backend.common.exception.ApiErrorFactory;
import com.tesisUrbe.backend.common.exception.ApiResponse;
import com.tesisUrbe.backend.entities.setting.ReportSetting;
import com.tesisUrbe.backend.entities.setting.UbicationSetting;
import com.tesisUrbe.backend.entities.setting.UniversitySetting;
import io.micrometer.common.util.StringUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.yaml.snakeyaml.DumperOptions;
import org.yaml.snakeyaml.Yaml;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

@Slf4j
@Service
@RequiredArgsConstructor
public class SettingsService {

    private final ApiErrorFactory errorFactory;
    private final ObjectMapper objectMapper;

    private final String configPath = "settings.yml";
    private final String logoPath = "src/main/resources/static/images/logo.png";

    private Map<String, Object> loadYaml() {
        try (InputStream input = getClass().getClassLoader().getResourceAsStream(configPath)) {
            if (input == null) {
                throw new FileNotFoundException("Archivo settings.yml no encontrado en el classpath");
            }
            return new Yaml().load(input);
        } catch (Exception e) {
            log.error("Error al cargar YAML", e);
            throw new RuntimeException("Error al cargar configuración");
        }
    }

    private void saveYaml(Map<String, Object> data) {
        try (FileWriter writer = new FileWriter(configPath)) {
            DumperOptions options = new DumperOptions();
            options.setPrettyFlow(true);
            options.setDefaultFlowStyle(DumperOptions.FlowStyle.BLOCK);
            new Yaml(options).dump(data, writer);
        } catch (Exception e) {
            log.error("Error al guardar YAML", e);
            throw new RuntimeException("Error al guardar configuración");
        }
    }

    public ApiResponse<ReportSetting> getReportSetting() {
        Map<String, Object> yaml = loadYaml();
        ReportSetting report = objectMapper.convertValue(yaml.get("report"), ReportSetting.class);
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

        Map<String, Object> yaml = loadYaml();
        yaml.put("report", report);
        saveYaml(yaml);
        return errorFactory.buildSuccess(HttpStatus.OK, "Estilos actualizados correctamente");
    }

    private boolean isHex(String color) {
        return Pattern.matches("^#([A-Fa-f0-9]{6})$", color);
    }

    public ApiResponse<UniversitySetting> getUniversitySetting() {
        Map<String, Object> yaml = loadYaml();
        UniversitySetting university = objectMapper.convertValue(yaml.get("university"), UniversitySetting.class);
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

        if (university.getTaxId() == null) {
            errors.add(new ApiError("INVALID_TAX_ID", "tax_id", "RIF no puede estar vacío"));
        } else {
            if (StringUtils.isBlank(university.getTaxId().getType())) {
                errors.add(new ApiError("INVALID_TAX_ID", "tax_id.type", "Tipo de RIF inválido"));
            }
            if (StringUtils.isBlank(university.getTaxId().getNumber())) {
                errors.add(new ApiError("INVALID_TAX_ID", "tax_id.number", "Número de RIF inválido"));
            }
        }

        if (!errors.isEmpty()) {
            return errorFactory.build(HttpStatus.BAD_REQUEST, errors);
        }

        Map<String, Object> yaml = loadYaml();

        Map<String, Object> universityMap = objectMapper.convertValue(
                university,
                new TypeReference<Map<String, Object>>() {}
        );

        yaml.put("university", universityMap);
        saveYaml(yaml);

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
            Path path = Path.of(logoPath);
            Files.createDirectories(path.getParent());
            Files.write(path, file.getBytes());
        } catch (IOException e) {
            log.error("Error al guardar logo", e);
            return errorFactory.build(HttpStatus.INTERNAL_SERVER_ERROR, List.of(
                    new ApiError("LOGO_SAVE_ERROR", null, "No se pudo guardar el logo")
            ));
        }

        return errorFactory.buildSuccess(HttpStatus.OK, "Logo actualizado correctamente");
    }

    public ApiResponse<UbicationSetting> getUbicationSetting() {
        Map<String, Object> yaml = loadYaml();
        UbicationSetting ubication = objectMapper.convertValue(yaml.get("ubication"), UbicationSetting.class);
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

        Map<String, Object> yaml = loadYaml();
        yaml.put("ubication", ubication);
        saveYaml(yaml);
        return errorFactory.buildSuccess(HttpStatus.OK, "Ubicación actualizada correctamente");
    }

    private boolean isValidCoordinate(double value) {
        return value >= -180 && value <= 180;
    }
}
