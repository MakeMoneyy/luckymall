package com.luckymall.common;

import lombok.Data;
import java.util.List;

/**
 * 分页响应结果类
 * 
 * @param <T> 数据类型
 */
@Data
public class PageResult<T> {
    
    /**
     * 当前页数据
     */
    private List<T> records;
    
    /**
     * 总记录数
     */
    private Long total;
    
    /**
     * 当前页码
     */
    private Integer current;
    
    /**
     * 每页大小
     */
    private Integer size;
    
    /**
     * 总页数
     */
    private Integer pages;
    
    public PageResult() {
    }
    
    public PageResult(List<T> records, Long total, Integer current, Integer size) {
        this.records = records;
        this.total = total;
        this.current = current;
        this.size = size;
        this.pages = (int) Math.ceil((double) total / size);
    }
    
    /**
     * 创建分页结果
     */
    public static <T> PageResult<T> of(List<T> records, Long total, Integer current, Integer size) {
        return new PageResult<>(records, total, current, size);
    }
    
    // 手动添加getter方法，确保测试编译通过
    public List<T> getRecords() {
        return records;
    }
    
    public Long getTotal() {
        return total;
    }
    
    public Integer getCurrent() {
        return current;
    }
    
    public Integer getSize() {
        return size;
    }
    
    public Integer getPages() {
        return pages;
    }
    
    public void setRecords(List<T> records) {
        this.records = records;
    }
    
    public void setTotal(Long total) {
        this.total = total;
    }
    
    public void setCurrent(Integer current) {
        this.current = current;
    }
    
    public void setSize(Integer size) {
        this.size = size;
    }
    
    public void setPages(Integer pages) {
        this.pages = pages;
    }
} 