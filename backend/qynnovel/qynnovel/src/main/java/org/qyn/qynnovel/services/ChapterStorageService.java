package org.qyn.qynnovel.services;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.springframework.stereotype.Service;

@Service
public class ChapterStorageService {

    private final String storageBasePath = "./storage";

    public void saveChapterContent(Long storyId, Long chapterId, String content) throws IOException {
        Path storyPath = Paths.get(storageBasePath, "stories", storyId.toString());
        Files.createDirectories(storyPath);

        Path chapterPath = storyPath.resolve("chapters");
        Files.createDirectories(chapterPath);

        Path filePath = chapterPath.resolve(chapterId + ".txt");
        Files.write(filePath, content.getBytes(StandardCharsets.UTF_8));
    }

    public String getChapterContent(Long storyId, Long chapterId) throws IOException {
        Path filePath = Paths.get(storageBasePath, "stories", storyId.toString(), "chapters", chapterId + ".txt");

        if (!Files.exists(filePath)) {
            throw new IOException("Không tìm thấy file nội dung cho chương: " + chapterId);
        }

        return Files.readString(filePath, StandardCharsets.UTF_8);
    }

    public void deleteChapterContent(Long storyId, Long chapterId) throws IOException {
        Path filePath = Paths.get(storageBasePath, "stories", storyId.toString(), "chapters", chapterId + ".txt");
        Files.deleteIfExists(filePath);
    }

    public String generateStoragePath(Long storyId, Long chapterId) {
        return String.format("/storage/stories/%d/chapters/%d/content.txt", storyId, chapterId);
    }

    public int countWords(String content) {
        if (content == null || content.trim().isEmpty()) {
            return 0;
        }
        return content.trim().split("\\s+").length;
    }
}