package com.fatecompass.repository;

import com.fatecompass.entity.FortuneHistory;
import com.fatecompass.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 算命歷史資料存取層
 */
@Repository
public interface FortuneHistoryRepository extends JpaRepository<FortuneHistory, Long> {
    
    /**
     * 根據用戶查找算命歷史
     */
    List<FortuneHistory> findByUserOrderByCreatedAtDesc(User user);
    
    /**
     * 根據用戶ID查找算命歷史
     */
    @Query("SELECT fh FROM FortuneHistory fh WHERE fh.user.userId = :userId ORDER BY fh.createdAt DESC")
    List<FortuneHistory> findByUserIdOrderByCreatedAtDesc(@Param("userId") Long userId);
    
    /**
     * 根據算命類型查找歷史記錄
     */
    List<FortuneHistory> findByFortuneTypeOrderByCreatedAtDesc(FortuneHistory.FortuneType fortuneType);
    
    /**
     * 根據用戶和算命類型查找歷史記錄
     */
    List<FortuneHistory> findByUserAndFortuneTypeOrderByCreatedAtDesc(User user, FortuneHistory.FortuneType fortuneType);
    
    /**
     * 查找指定日期範圍內的算命記錄
     */
    @Query("SELECT fh FROM FortuneHistory fh WHERE fh.user.userId = :userId AND fh.createdAt BETWEEN :startDate AND :endDate ORDER BY fh.createdAt DESC")
    List<FortuneHistory> findByUserIdAndDateRange(@Param("userId") Long userId, 
                                                  @Param("startDate") LocalDateTime startDate, 
                                                  @Param("endDate") LocalDateTime endDate);
    
    /**
     * 統計用戶的算命次數
     */
    @Query("SELECT COUNT(fh) FROM FortuneHistory fh WHERE fh.user.userId = :userId")
    Long countByUserId(@Param("userId") Long userId);
    
    /**
     * 統計各類型算命次數
     */
    @Query("SELECT fh.fortuneType, COUNT(fh) FROM FortuneHistory fh WHERE fh.user.userId = :userId GROUP BY fh.fortuneType")
    List<Object[]> countByUserIdGroupByFortuneType(@Param("userId") Long userId);
} 