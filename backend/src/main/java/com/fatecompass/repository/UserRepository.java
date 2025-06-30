package com.fatecompass.repository;

import com.fatecompass.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * 用戶資料存取層
 */
@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    
    /**
     * 根據用戶名查找用戶
     */
    Optional<User> findByUsername(String username);
    
    /**
     * 根據電子郵件查找用戶
     */
    Optional<User> findByEmail(String email);
    
    /**
     * 檢查用戶名是否存在
     */
    boolean existsByUsername(String username);
    
    /**
     * 檢查電子郵件是否存在
     */
    boolean existsByEmail(String email);
    
    /**
     * 根據用戶名或電子郵件查找用戶
     */
    @Query("SELECT u FROM User u WHERE u.username = :loginId OR u.email = :loginId")
    Optional<User> findByUsernameOrEmail(@Param("loginId") String loginId);
    
    /**
     * 查找啟用的用戶
     */
    @Query("SELECT u FROM User u WHERE u.isActive = true AND (u.username = :loginId OR u.email = :loginId)")
    Optional<User> findActiveUserByUsernameOrEmail(@Param("loginId") String loginId);
} 