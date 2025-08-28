package org.qyn.qynnovel.repositories;

import java.sql.Date;
import java.util.List;
import java.util.Optional;

import org.qyn.qynnovel.models.Chapter;
import org.qyn.qynnovel.models.Story;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import jakarta.transaction.Transactional;

@Repository
public interface StoryRepository extends JpaRepository<Story, Long> {

        // Tìm truyện theo slug
        Optional<Story> findBySlug(String slug);

        // Tìm truyện theo trạng thái
        List<Story> findByStatus(String status);

        // Tìm truyện theo trạng thái với phân trang
        Page<Story> findByStatus(String status, Pageable pageable);

        // Tìm truyện theo tác giả
        List<Story> findByAuthor(String author);

        // Tìm truyện theo tác giả với phân trang
        Page<Story> findByAuthor(String author, Pageable pageable);

        // Tìm truyện theo user (người đăng)
        List<Story> findByUserId(Long userId);

        // Tìm truyện theo user với phân trang
        Page<Story> findByUserId(Long userId, Pageable pageable);

        // Tìm truyện theo tiêu đề (tìm kiếm gần đúng)
        List<Story> findByTitleContainingIgnoreCase(String title);

        // Tìm truyện theo tiêu đề với phân trang
        Page<Story> findByTitleContainingIgnoreCase(String title, Pageable pageable);

        // Tìm truyện có nhiều view nhất
        Page<Story> findByOrderByTotalViewsDesc(Pageable pageable);

        // Tìm truyện mới nhất
        Page<Story> findByOrderByCreatedAtDesc(Pageable pageable);

        // Tìm truyện được thích nhiều nhất
        Page<Story> findByOrderByTotalLikesDesc(Pageable pageable);

        // Tìm truyện theo thể loại (sử dụng JOIN)
        @Query("SELECT s FROM Story s JOIN s.categories c WHERE c.id = :categoryId")
        List<Story> findByCategoryId(@Param("categoryId") Long categoryId);

        // Tìm truyện theo thể loại với phân trang
        @Query("SELECT s FROM Story s JOIN s.categories c WHERE c.id = :categoryId")
        Page<Story> findByCategoryId(@Param("categoryId") Long categoryId, Pageable pageable);

        // Tìm truyện theo thể loại slug
        @Query("SELECT s FROM Story s JOIN s.categories c WHERE c.slug = :categorySlug")
        List<Story> findByCategorySlug(@Param("categorySlug") String categorySlug);

        // Tìm truyện theo thể loại slug với phân trang
        @Query("SELECT s FROM Story s JOIN s.categories c WHERE c.slug = :categorySlug")
        Page<Story> findByCategorySlug(@Param("categorySlug") String categorySlug, Pageable pageable);

        // Tìm truyện theo nhiều thể loại
        @Query("SELECT s FROM Story s JOIN s.categories c WHERE c.id IN :categoryIds")
        List<Story> findByCategoryIds(@Param("categoryIds") List<Long> categoryIds);

        // Tìm truyện theo nhiều thể loại với phân trang
        @Query("SELECT s FROM Story s JOIN s.categories c WHERE c.id IN :categoryIds")
        Page<Story> findByCategoryIds(@Param("categoryIds") List<Long> categoryIds, Pageable pageable);

        // Tìm kiếm nâng cao: theo tiêu đề, tác giả, mô tả
        @Query("SELECT s FROM Story s WHERE " +
                        "LOWER(s.title) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
                        "LOWER(s.author) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
                        "LOWER(s.description) LIKE LOWER(CONCAT('%', :keyword, '%'))")
        List<Story> advancedSearch(@Param("keyword") String keyword);

        // Tìm kiếm nâng cao với phân trang
        @Query("SELECT s FROM Story s WHERE " +
                        "LOWER(s.title) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
                        "LOWER(s.author) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
                        "LOWER(s.description) LIKE LOWER(CONCAT('%', :keyword, '%'))")
        Page<Story> advancedSearch(@Param("keyword") String keyword, Pageable pageable);

        // Đếm số truyện theo trạng thái
        @Query("SELECT COUNT(s) FROM Story s WHERE s.status = :status")
        Long countByStatus(@Param("status") String status);

        // Đếm số truyện theo thể loại
        @Query("SELECT COUNT(s) FROM Story s JOIN s.categories c WHERE c.id = :categoryId")
        Long countByCategoryId(@Param("categoryId") Long categoryId);

        // Đếm số truyện theo user
        @Query("SELECT COUNT(s) FROM Story s WHERE s.userId = :userId")
        Long countByUserId(@Param("userId") Long userId);

        // Tìm truyện có chapter mới nhất (cập nhật gần đây)
        @Query("SELECT s FROM Story s ORDER BY s.updatedAt DESC")
        Page<Story> findRecentlyUpdated(Pageable pageable);

        // Tìm truyện đã hoàn thành
        @Query("SELECT s FROM Story s WHERE s.status = 'COMPLETED' ORDER BY s.updatedAt DESC")
        Page<Story> findCompletedStories(Pageable pageable);

        // Tìm truyện đang tiến hành
        @Query("SELECT s FROM Story s WHERE s.status = 'ONGOING' ORDER BY s.updatedAt DESC")
        Page<Story> findOngoingStories(Pageable pageable);

        // Tìm truyện đề xuất (nhiều view, like, mới cập nhật)
        @Query("SELECT s FROM Story s ORDER BY s.totalViews DESC, s.totalLikes DESC, s.updatedAt DESC")
        Page<Story> findRecommendedStories(Pageable pageable);

        // Tìm truyện cùng tác giả
        @Query("SELECT s FROM Story s WHERE s.author = :author AND s.id != :excludeId")
        List<Story> findOtherStoriesByAuthor(@Param("author") String author, @Param("excludeId") Long excludeId);

        // Tìm truyện cùng thể loại
        @Query("SELECT s FROM Story s JOIN s.categories c WHERE c.id IN :categoryIds AND s.id != :excludeId")
        List<Story> findSimilarStories(@Param("categoryIds") List<Long> categoryIds,
                        @Param("excludeId") Long excludeId);

        // Tìm truyện theo khoảng thời gian
        @Query("SELECT s FROM Story s WHERE s.createdAt BETWEEN :startDate AND :endDate")
        List<Story> findByCreatedAtBetween(@Param("startDate") Date startDate, @Param("endDate") Date endDate);

        // Tìm truyện có từ số chapter trở lên
        @Query("SELECT s FROM Story s WHERE SIZE(s.chapters) >= :minChapters")
        List<Story> findByMinChapters(@Param("minChapters") int minChapters);

        // Tìm truyện có rating trung bình trên mức nào đó
        // @Query("SELECT s FROM Story s WHERE (SELECT COALESCE(AVG(r.rating), 0) FROM
        // Rating r WHERE r.story = s) >= :minRating")
        // List<Story> findByMinRating(@Param("minRating") double minRating);

        // Cập nhật lượt xem
        @Query("UPDATE Story s SET s.totalViews = s.totalViews + 1 WHERE s.id = :storyId")
        @Modifying
        @Transactional
        void incrementViews(@Param("storyId") Long storyId);

        // Cập nhật lượt thích
        @Query("UPDATE Story s SET s.totalLikes = s.totalLikes + 1 WHERE s.id = :storyId")
        @Modifying
        @Transactional
        void incrementLikes(@Param("storyId") Long storyId);

        // Giảm lượt thích
        @Query("UPDATE Story s SET s.totalLikes = s.totalLikes - 1 WHERE s.id = :storyId")
        @Modifying
        @Transactional
        void decrementLikes(@Param("storyId") Long storyId);

        // Đếm tổng số từ của tất cả chapter trong truyện
        @Query("SELECT SUM(c.wordCount) FROM Chapter c WHERE c.story.id = :storyId")
        Long getTotalWordCount(@Param("storyId") Long storyId);

        // Lấy số chapter của truyện
        @Query("SELECT COUNT(c) FROM Chapter c WHERE c.story.id = :storyId")
        Long getChapterCount(@Param("storyId") Long storyId);

        // Lấy chapter mới nhất của truyện
        @Query("SELECT c FROM Chapter c WHERE c.story.id = :storyId ORDER BY c.chapterNumber DESC")
        Page<Chapter> getLatestChapters(@Param("storyId") Long storyId, Pageable pageable);
}
