package org.qyn.qynnovel.services;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;

import org.qyn.qynnovel.models.Chapter;
import org.qyn.qynnovel.models.ReadingHistory;
import org.qyn.qynnovel.models.Story;
import org.qyn.qynnovel.repositories.ChapterRepository;
import org.qyn.qynnovel.repositories.ReadingHistoryRepository;
import org.qyn.qynnovel.repositories.StoryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import jakarta.persistence.EntityNotFoundException;

@Service
@Transactional
public class ChapterService {

    @Autowired
    private ChapterRepository chapterRepository;

    @Autowired
    private StoryRepository storyRepository;

    @Autowired
    private ReadingHistoryRepository readingHistoryRepository;

    @Autowired
    private ChapterStorageService storageService;

    public Chapter findById(Long id) {
        return chapterRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Chapter not found with id: " + id));
    }

    public Chapter saveChapter(Chapter chapter) {
        // Lưu metadata trước để có ID
        Chapter savedChapter = chapterRepository.save(chapter);

        // Nếu có nội dung, lưu vào file
        if (chapter.getContent() != null && !chapter.getContent().trim().isEmpty()) {
            try {
                // Lưu nội dung vào file
                storageService.saveChapterContent(
                        savedChapter.getStoryId(),
                        savedChapter.getId(),
                        chapter.getContent());

                // Cập nhật word count và storage path
                savedChapter.setWordCount(storageService.countWords(chapter.getContent()));
                savedChapter.setStoragePath(
                        storageService.generateStoragePath(
                                savedChapter.getStoryId(),
                                savedChapter.getId()));

                // Lưu lại metadata với storage path
                savedChapter = chapterRepository.save(savedChapter);

            } catch (IOException e) {
                throw new RuntimeException("Lỗi khi lưu nội dung chương", e);
            }
        }

        return savedChapter;
    }

    public Chapter getChapterById(Long id) {
        return chapterRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy chương: " + id));
    }

    public Chapter getChapterWithContent(Long id) {
        Chapter chapter = getChapterById(id);

        // Nếu có storage path, đọc nội dung từ file
        if (chapter.getStoragePath() != null) {
            try {
                String content = storageService.getChapterContent(
                        chapter.getStoryId(),
                        chapter.getId());
                chapter.setContent(content);
            } catch (IOException e) {
                throw new RuntimeException("Lỗi khi đọc nội dung chương", e);
            }
        }

        // Tăng lượt xem
        chapter.setViews(chapter.getViews() + 1);
        chapterRepository.save(chapter);

        // Cập nhật tổng lượt xem của truyện
        Story story = storyRepository.findById(chapter.getStoryId())
                .orElseThrow(() -> new RuntimeException("Không tìm thấy truyện: " + chapter.getStoryId()));
        story.setTotalViews(story.getTotalViews() + 1);
        storyRepository.save(story);

        return chapter;
    }

    public void deleteChapter(Long id) {
        Chapter chapter = getChapterById(id);

        // Xóa file nội dung nếu có
        if (chapter.getStoragePath() != null) {
            try {
                storageService.deleteChapterContent(
                        chapter.getStoryId(),
                        chapter.getId());
            } catch (IOException e) {
                // Log warning nhưng vẫn tiếp tục xóa metadata
                System.out.println("Không thể xóa file nội dung chương: " + id);
            }
        }

        // Xóa metadata
        chapterRepository.deleteById(id);
    }

    public Chapter updateChapterContent(Long id, String newContent) {
        Chapter chapter = getChapterById(id);

        try {
            // Lưu nội dung mới vào file
            storageService.saveChapterContent(
                    chapter.getStoryId(),
                    chapter.getId(),
                    newContent);

            // Cập nhật word count
            chapter.setWordCount(storageService.countWords(newContent));
            chapter.setStoragePath(
                    storageService.generateStoragePath(
                            chapter.getStoryId(),
                            chapter.getId()));

            return chapterRepository.save(chapter);

        } catch (IOException e) {
            throw new RuntimeException("Lỗi khi cập nhật nội dung chương", e);
        }
    }

    public List<Chapter> getChaptersByStoryId(Long storyId) {
        return chapterRepository.findByStoryIdOrderByChapterNumberAsc(storyId);
    }

    public Page<Chapter> getChaptersByStoryId(Long storyId, Pageable pageable) {
        return chapterRepository.findByStoryIdOrderByChapterNumberAsc(storyId, pageable);
    }

    public Chapter getNextChapter(Long currentChapterId) {
        Chapter currentChapter = getChapterById(currentChapterId);

        return chapterRepository.findNextChapter(
                currentChapter.getStoryId(),
                currentChapter.getChapterNumber()).orElse(null);
    }

    public Chapter getPreviousChapter(Long currentChapterId) {
        Chapter currentChapter = getChapterById(currentChapterId);

        return chapterRepository.findPreviousChapter(
                currentChapter.getStoryId(),
                currentChapter.getChapterNumber()).orElse(null);
    }

    public Chapter getFirstChapter(Long storyId) {
        return chapterRepository.findFirstByStoryIdOrderByChapterNumberAsc(storyId)
                .orElseThrow(() -> new RuntimeException("Truyện không có chương nào"));
    }

    public void saveReadingHistory(Long userId, Long chapterId) {
        Chapter chapter = getChapterById(chapterId);

        ReadingHistory history = new ReadingHistory();
        history.setUserId(userId);
        history.setChapterId(chapterId);
        history.setStoryId(chapter.getStoryId());
        history.setReadAt(LocalDateTime.now());

        readingHistoryRepository.save(history);
    }
}