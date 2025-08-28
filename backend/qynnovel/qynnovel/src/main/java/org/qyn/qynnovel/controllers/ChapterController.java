package org.qyn.qynnovel.controllers;

import java.util.List;
import java.util.Map;

import org.qyn.qynnovel.models.Chapter;
import org.qyn.qynnovel.services.ChapterService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/chapters")
@CrossOrigin(origins = "http://localhost:3000")
public class ChapterController {
    @Autowired
    private ChapterService chapterService;
    private static final org.slf4j.Logger log = org.slf4j.LoggerFactory.getLogger(ChapterController.class);

    public ChapterController(ChapterService chapterService) {
        this.chapterService = chapterService;
    }

    /**
     * Lấy thông tin chương (không bao gồm nội dung)
     */
    @GetMapping("/{id}")
    public ResponseEntity<Chapter> getChapterById(@PathVariable Long id) {
        try {
            Chapter chapter = chapterService.findById(id);
            if (chapter == null) {
                log.warn("Chapter with id {} not found", id);
                return ResponseEntity.notFound().build();
            }
            return ResponseEntity.ok(chapter);
        } catch (Exception e) {
            log.error("Error fetching chapter with id: {}", id, e);
            return ResponseEntity.status(500).body(null);
        }
    }

    /**
     * Lấy chương với nội dung đầy đủ
     */
    @GetMapping("/{id}/content")
    public ResponseEntity<Chapter> getChapterWithContent(@PathVariable Long id) {
        Chapter chapter = chapterService.getChapterWithContent(id);
        return ResponseEntity.ok(chapter);
    }

    /**
     * Tạo chương mới
     */
    @PostMapping
    public ResponseEntity<Chapter> createChapter(@RequestBody Chapter chapter) {
        Chapter savedChapter = chapterService.saveChapter(chapter);
        return ResponseEntity.status(HttpStatus.CREATED).body(savedChapter);
    }

    /**
     * Cập nhật nội dung chương
     */
    @PutMapping("/{id}/content")
    public ResponseEntity<Chapter> updateChapterContent(
            @PathVariable Long id,
            @RequestBody Map<String, String> request) {

        String content = request.get("content");
        Chapter updatedChapter = chapterService.updateChapterContent(id, content);
        return ResponseEntity.ok(updatedChapter);
    }

    /**
     * Xóa chương
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteChapter(@PathVariable Long id) {
        chapterService.deleteChapter(id);
        return ResponseEntity.noContent().build();
    }

    /**
     * Lấy danh sách chương của truyện
     */
    @GetMapping("/story/{storyId}")
    public ResponseEntity<List<Chapter>> getChaptersByStory(@PathVariable Long storyId) {
        List<Chapter> chapters = chapterService.getChaptersByStoryId(storyId);
        return ResponseEntity.ok(chapters);
    }

    /**
     * Lấy chương tiếp theo
     */
    @GetMapping("/{id}/next")
    public ResponseEntity<Chapter> getNextChapter(@PathVariable Long id) {
        Chapter nextChapter = chapterService.getNextChapter(id);
        if (nextChapter == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(nextChapter);
    }

    /**
     * Lấy chương trước đó
     */
    @GetMapping("/{id}/previous")
    public ResponseEntity<Chapter> getPreviousChapter(@PathVariable Long id) {
        Chapter previousChapter = chapterService.getPreviousChapter(id);
        if (previousChapter == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(previousChapter);
    }

    /**
     * Lưu lịch sử đọc
     */
    @PostMapping("/{id}/read")
    public ResponseEntity<Void> markAsRead(
            @PathVariable Long id,
            @RequestHeader("X-User-ID") Long userId) {

        chapterService.saveReadingHistory(userId, id);
        return ResponseEntity.ok().build();
    }

    /**
     * Lấy danh sách chương của truyện (phân trang)
     */
    @GetMapping("/story/{storyId}/list")
    public ResponseEntity<Page<Chapter>> getChaptersByStory(
            @PathVariable Long storyId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "50") int size) {

        Page<Chapter> chapters = (Page<Chapter>) chapterService.getChaptersByStoryId(storyId);
        return ResponseEntity.ok(chapters);
    }
}
