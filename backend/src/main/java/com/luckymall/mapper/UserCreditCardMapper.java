package com.luckymall.mapper;

import com.luckymall.entity.UserCreditCard;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface UserCreditCardMapper {
    
    UserCreditCard findByUserId(Long userId);
    
    void insert(UserCreditCard userCreditCard);
    
    void update(UserCreditCard userCreditCard);
} 