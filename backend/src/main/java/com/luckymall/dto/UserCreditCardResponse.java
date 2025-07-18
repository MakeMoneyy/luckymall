package com.luckymall.dto;

import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDate;

@Data
public class UserCreditCardResponse {
    private String cardLevel;
    private Integer pointsBalance;
    private Integer pointsExpiring;
    private LocalDate expiringDate;
    private BigDecimal availableCredit;
    private Integer nextBillDate;
} 