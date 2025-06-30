package com.fatecompass.config;

import com.fatecompass.entity.User;
import com.fatecompass.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * 資料初始化類 - 僅在demo模式下運行
 */
@Component
@Profile("demo")
public class DataInitializer implements CommandLineRunner {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) throws Exception {
        // 檢查是否已經有用戶資料
        if (userRepository.count() == 0) {
            createTestUsers();
            System.out.println("✓ 測試用戶資料已創建");
        } else {
            System.out.println("✓ 資料庫中已存在用戶資料");
        }
    }

    private void createTestUsers() {
        // 創建測試用戶1
        User user1 = new User();
        user1.setUsername("test");
        user1.setPassword(passwordEncoder.encode("123456"));
        user1.setEmail("test@example.com");
        user1.setRealName("測試用戶");
        user1.setGender(User.Gender.MALE);
        user1.setBirthDate(LocalDate.of(1990, 8, 15).atStartOfDay());
        user1.setBirthPlace("台北市");
        user1.setPhone("0912345678");
        user1.setIsActive(true);
        user1.setCreatedAt(LocalDateTime.now());
        user1.setUpdatedAt(LocalDateTime.now());
        userRepository.save(user1);

        // 創建測試用戶2
        User user2 = new User();
        user2.setUsername("demo");
        user2.setPassword(passwordEncoder.encode("demo123"));
        user2.setEmail("demo@example.com");
        user2.setRealName("示範用戶");
        user2.setGender(User.Gender.FEMALE);
        user2.setBirthDate(LocalDate.of(1985, 12, 3).atStartOfDay());
        user2.setBirthPlace("高雄市");
        user2.setPhone("0987654321");
        user2.setIsActive(true);
        user2.setCreatedAt(LocalDateTime.now());
        user2.setUpdatedAt(LocalDateTime.now());
        userRepository.save(user2);

        // 創建測試用戶3
        User user3 = new User();
        user3.setUsername("admin");
        user3.setPassword(passwordEncoder.encode("admin123"));
        user3.setEmail("admin@fatecompass.com");
        user3.setRealName("管理員");
        user3.setGender(User.Gender.MALE);
        user3.setBirthDate(LocalDate.of(1988, 5, 20).atStartOfDay());
        user3.setBirthPlace("台中市");
        user3.setPhone("0911222333");
        user3.setIsActive(true);
        user3.setCreatedAt(LocalDateTime.now());
        user3.setUpdatedAt(LocalDateTime.now());
        userRepository.save(user3);

        System.out.println("測試用戶已創建：");
        System.out.println("1. 用戶名: test, 密碼: 123456");
        System.out.println("2. 用戶名: demo, 密碼: demo123");
        System.out.println("3. 用戶名: admin, 密碼: admin123");
    }
} 