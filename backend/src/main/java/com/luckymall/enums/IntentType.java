package com.luckymall.enums;

/**
 * 用户意图类型枚举
 */
public enum IntentType {
    /**
     * 订单查询
     */
    ORDER_QUERY("order_query", "订单查询"),
    
    /**
     * 商品查询
     */
    PRODUCT_QUERY("product_query", "商品查询"),
    
    /**
     * 支付查询
     */
    PAYMENT_QUERY("payment_query", "支付查询"),
    
    /**
     * 物流查询
     */
    LOGISTICS_QUERY("logistics_query", "物流查询"),
    
    /**
     * 退换货
     */
    RETURN_REFUND("return_refund", "退换货"),
    
    /**
     * 信用卡咨询
     */
    CREDIT_CARD("credit_card", "信用卡咨询"),
    
    /**
     * 积分查询
     */
    POINTS_QUERY("points_query", "积分查询"),
    
    /**
     * 一般问题
     */
    GENERAL_QUESTION("general_question", "一般问题"),
    
    /**
     * 投诉
     */
    COMPLAINT("complaint", "投诉"),
    
    /**
     * 表扬
     */
    PRAISE("praise", "表扬"),
    
    /**
     * 未知意图
     */
    UNKNOWN("unknown", "未知意图");
    
    private final String code;
    private final String description;
    
    IntentType(String code, String description) {
        this.code = code;
        this.description = description;
    }
    
    public String getCode() {
        return code;
    }
    
    public String getDescription() {
        return description;
    }
    
    /**
     * 根据code获取枚举
     * @param code 编码
     * @return 枚举值
     */
    public static IntentType fromCode(String code) {
        for (IntentType type : IntentType.values()) {
            if (type.getCode().equals(code)) {
                return type;
            }
        }
        return UNKNOWN;
    }
} 