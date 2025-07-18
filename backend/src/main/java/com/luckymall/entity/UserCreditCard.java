package com.luckymall.entity;

import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
public class UserCreditCard {
    private Long id;
    private Long userId;
    private String cardLevel; // GOLD, PLATINUM, DIAMOND
    private Integer pointsBalance = 0;
    private Integer pointsExpiring = 0;
    private LocalDate expiringDate;
    private BigDecimal creditLimit;
    private Integer billDate;
    private Integer dueDate;
    private LocalDateTime createdTime;
    private LocalDateTime updatedTime;
} 