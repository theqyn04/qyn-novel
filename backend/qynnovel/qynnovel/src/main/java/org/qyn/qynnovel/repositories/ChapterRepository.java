package org.qyn.qynnovel.repositories;

import java.util.List;
import java.util.Optional;

import org.qyn.qynnovel.models.Chapter;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface ChapterRepository extends JpaRepository<Chapter, Long> {

    // ... các phương thức hiện có

    @Query("SELECT c FROM Chapter c WHERE c.story.id = :storyId AND c.chapterNumber > :currentChapterNumber ORDER BY c.chapterNumber ASC")
    Optional<Chapter> findNextChapter(@Param("storyId") Long storyId,
            @Param("currentChapterNumber") Integer currentChapterNumber);

    @Query("SELECT c FROM Chapter c WHERE c.story.id = :storyId AND c.chapterNumber < :currentChapterNumber ORDER BY c.chapterNumber DESC")
    Optional<Chapter> findPreviousChapter(@Param("storyId") Long storyId,
            @Param("currentChapterNumber") Integer currentChapterNumber);

    Optional<Chapter> findFirstByStoryIdOrderByChapterNumberAsc(Long storyId);

    List<Chapter> findByStoryIdOrderByChapterNumberAsc(Long storyId);

    @Query("SELECT c FROM Chapter c WHERE c.story.id = :storyId ORDER BY c.chapterNumber ASC")
    Page<Chapter> findByStoryIdOrderByChapterNumberAsc(@Param("storyId") Long storyId, Pageable pageable);
}
