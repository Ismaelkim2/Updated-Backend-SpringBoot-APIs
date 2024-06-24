package com.kimsreviews.API.Services;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
public class FileStorageService {

    @Value("${file.upload-dir}")
    private String uploadDir;

    public List<String> storeMultipleFiles(MultipartFile[] files) {
        List<String> fileNames = new ArrayList<>();
        for (MultipartFile file : files) {
            try {
                // Generate a unique file name
                String fileName = StringUtils.cleanPath(UUID.randomUUID().toString() + "_" + file.getOriginalFilename());
                // Resolve the file storage directory
                Path uploadPath = Paths.get(uploadDir).toAbsolutePath().normalize();
                // Create the directory if it doesn't exist
                Files.createDirectories(uploadPath);
                // Copy the file to the target location
                Path targetLocation = uploadPath.resolve(fileName);
                Files.copy(file.getInputStream(), targetLocation, StandardCopyOption.REPLACE_EXISTING);
                fileNames.add(fileName);
            } catch (IOException ex) {
                throw new RuntimeException("Failed to store file " + file.getOriginalFilename(), ex);
            }
        }
        return fileNames;
    }

    public List<String> getUploadedDocuments() {
        List<String> documents = new ArrayList<>();
        File[] files = new File(uploadDir).listFiles();
        if (files != null) {
            for (File file : files) {
                if (file.isFile()) {
                    documents.add(file.getName());
                }
            }
        }
        return documents;
    }

    public String storeFile(MultipartFile file) {
        try {
            // Generate a unique file name
            String fileName = StringUtils.cleanPath(UUID.randomUUID().toString() + "_" + file.getOriginalFilename());
            // Resolve the file storage directory
            Path uploadPath = Paths.get(uploadDir).toAbsolutePath().normalize();
            // Create the directory if it doesn't exist
            Files.createDirectories(uploadPath);
            // Copy the file to the target location
            Path targetLocation = uploadPath.resolve(fileName);
            Files.copy(file.getInputStream(), targetLocation, StandardCopyOption.REPLACE_EXISTING);
            return fileName;
        } catch (IOException ex) {
            throw new RuntimeException("Failed to store file " + file.getOriginalFilename(), ex);
        }
    }

    public List<String> storeFiles(List<MultipartFile> files) {
        // Implement as needed
        // For example, you might iterate over the list of files and call storeFile for each file
        List<String> storedFileNames = new ArrayList<>();
        for (MultipartFile file : files) {
            String storedFileName = storeFile(file);
            storedFileNames.add(storedFileName);
        }
        return storedFileNames;
    }
}
