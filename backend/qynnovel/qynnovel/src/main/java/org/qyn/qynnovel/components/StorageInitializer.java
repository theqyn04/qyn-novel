package org.qyn.qynnovel.components;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
public class StorageInitializer implements CommandLineRunner {
    @Value("${app.storage.path:./storage}")
    private String storagePath;

    @Override
    public void run(String... args) throws Exception {
        // Tạo thư mục storage nếu chưa tồn tại
        Path storageDir = Paths.get(storagePath);
        if (!Files.exists(storageDir)) {
            Files.createDirectories(storageDir);
            System.out.println("Đã tạo thư mục storage: " + storageDir.toAbsolutePath());
        }

        // Tạo thư mục con cho stories
        Path storiesDir = storageDir.resolve("stories");
        if (!Files.exists(storiesDir)) {
            Files.createDirectories(storiesDir);
            System.out.println("Đã tạo thư mục stories: " + storiesDir.toAbsolutePath());
        }
    }
}
