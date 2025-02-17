package com.example.logquerysystem.entity;

import lombok.Data;
import javax.persistence.Table;

/**
 * OCR标记检查数据对象
 * 用于存储和管理OCR标记检查相关的信息
 */
@Data
@Table(name = "ocr_mark_inspect")
public class OcrMarkInspectDO {
    /**
     * 主键ID
     */
    private Integer id;
    
    /**
     * 批次ID，用于标识一组相关的OCR标记检查记录
     */
    private Integer batchId;
    
    /**
     * 版本号，用于记录数据的版本信息
     */
    private Integer version;
    
    /**
     * TFS文件路径，存储相关文件的位置信息
     */
    private String tfs;
    
    /**
     * 标记内容
     */
    private String mark;
    
    /**
     * 删除标志，0表示未删除，1表示已删除
     */
    private Integer isdelete;
    
    /**
     * 检查结果
     */
    private String result;
    
    /**
     * 标记名称
     */
    private String markName;
}