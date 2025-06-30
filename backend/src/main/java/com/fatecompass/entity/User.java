package com.fatecompass.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 用戶實體類
 */
@Entity
@Table(name = "FC_USERS")
public class User {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "USER_ID")
    private Long userId;
    
    @NotBlank(message = "用戶名不能為空")
    @Size(min = 3, max = 50, message = "用戶名長度必須在3-50個字符之間")
    @Column(name = "USERNAME", unique = true, nullable = false, length = 50)
    private String username;
    
    @NotBlank(message = "密碼不能為空")
    @Size(min = 6, message = "密碼長度至少6個字符")
    @Column(name = "PASSWORD", nullable = false)
    private String password;
    
    @Email(message = "請輸入有效的電子郵件")
    @Column(name = "EMAIL", unique = true, length = 100)
    private String email;
    
    @Column(name = "REAL_NAME", length = 50)
    private String realName;
    
    @Column(name = "PHONE", length = 20)
    private String phone;
    
    @Enumerated(EnumType.STRING)
    @Column(name = "GENDER", length = 10)
    private Gender gender;
    
    @Column(name = "BIRTH_DATE")
    private LocalDateTime birthDate;
    
    @Column(name = "BIRTH_PLACE", length = 100)
    private String birthPlace;
    
    @Column(name = "IS_ACTIVE")
    private Boolean isActive = true;
    
    @CreationTimestamp
    @Column(name = "CREATED_AT", updatable = false)
    private LocalDateTime createdAt;
    
    @UpdateTimestamp
    @Column(name = "UPDATED_AT")
    private LocalDateTime updatedAt;
    
    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<FortuneHistory> fortuneHistories;
    
    // 枚舉：性別
    public enum Gender {
        MALE("男"),
        FEMALE("女");
        
        private final String description;
        
        Gender(String description) {
            this.description = description;
        }
        
        public String getDescription() {
            return description;
        }
    }
    
    // 構造函數
    public User() {}
    
    public User(String username, String password, String email) {
        this.username = username;
        this.password = password;
        this.email = email;
    }
    
    // Getter和Setter方法
    public Long getUserId() {
        return userId;
    }
    
    public void setUserId(Long userId) {
        this.userId = userId;
    }
    
    public String getUsername() {
        return username;
    }
    
    public void setUsername(String username) {
        this.username = username;
    }
    
    public String getPassword() {
        return password;
    }
    
    public void setPassword(String password) {
        this.password = password;
    }
    
    public String getEmail() {
        return email;
    }
    
    public void setEmail(String email) {
        this.email = email;
    }
    
    public String getRealName() {
        return realName;
    }
    
    public void setRealName(String realName) {
        this.realName = realName;
    }
    
    public String getPhone() {
        return phone;
    }
    
    public void setPhone(String phone) {
        this.phone = phone;
    }
    
    public Gender getGender() {
        return gender;
    }
    
    public void setGender(Gender gender) {
        this.gender = gender;
    }
    
    public LocalDateTime getBirthDate() {
        return birthDate;
    }
    
    public void setBirthDate(LocalDateTime birthDate) {
        this.birthDate = birthDate;
    }
    
    public String getBirthPlace() {
        return birthPlace;
    }
    
    public void setBirthPlace(String birthPlace) {
        this.birthPlace = birthPlace;
    }
    
    public Boolean getIsActive() {
        return isActive;
    }
    
    public void setIsActive(Boolean isActive) {
        this.isActive = isActive;
    }
    
    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
    
    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
    
    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }
    
    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }
    
    public List<FortuneHistory> getFortuneHistories() {
        return fortuneHistories;
    }
    
    public void setFortuneHistories(List<FortuneHistory> fortuneHistories) {
        this.fortuneHistories = fortuneHistories;
    }
} 