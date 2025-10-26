package com.tesisUrbe.backend.settingsManagement.services;

import com.tesisUrbe.backend.settingsManagement.config.StorageProperties;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

@Service
public class FileStorageService {

    private final Path rootLocation;

    public FileStorageService(StorageProperties properties) {
        this.rootLocation = Paths.get(properties.getPath());
    }

    public void save(String filename, byte[] content) throws IOException {
        Files.createDirectories(rootLocation);
        Path destination = rootLocation.resolve(filename);
        Files.write(destination, content);
    }

    public Resource load(String filename) throws MalformedURLException {
        Path file = rootLocation.resolve(filename);
        if (!Files.exists(file)) {
            throw new MalformedURLException("Archivo no encontrado: " + file.toAbsolutePath());
        }
        return new UrlResource(file.toUri());
    }
}

