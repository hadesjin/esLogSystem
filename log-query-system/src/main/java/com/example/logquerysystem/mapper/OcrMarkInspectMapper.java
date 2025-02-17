package com.example.logquerysystem.mapper;

import com.example.logquerysystem.entity.OcrMarkInspectDO;
import tk.mybatis.mapper.common.Mapper;

/**
 * OCR标记检查数据访问层接口
 * 继承通用Mapper接口以获取基础的CRUD操作能力
 * 用于处理OCR标记检查相关的数据库操作
 */
public interface OcrMarkInspectMapper extends Mapper<OcrMarkInspectDO> {
}