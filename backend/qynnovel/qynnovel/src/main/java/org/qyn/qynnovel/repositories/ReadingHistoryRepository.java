package org.qyn.qynnovel.repositories;

import java.util.List;
import java.util.Optional;

import org.qyn.qynnovel.models.ReadingHistory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface ReadingHistoryRepository extends JpaRepository<ReadingHistory, Long> {

    List<ReadingHistory> findByUserIdOrderByReadAtDesc(Long userId);

    Optional<ReadingHistory> findTopByUserIdAndStoryIdOrderByReadAtDesc(Long userId, Long storyId);

    @Query("SELECT rh FROM ReadingHistory rh WHERE rh.userId = :userId ORDER BY rh.readAt DESC")
    Page<ReadingHistory> findRecentReadingHistory(@Param("userId") Long userId, Pageable pageable);
}