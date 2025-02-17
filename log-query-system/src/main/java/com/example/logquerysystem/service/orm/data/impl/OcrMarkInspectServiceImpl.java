package com.example.logquerysystem.service.orm.data.impl;

import com.example.logquerysystem.entity.OcrMarkInspectDO;
import com.example.logquerysystem.service.orm.base.AbstractBaseService;
import com.example.logquerysystem.service.orm.data.intf.OcrMarkInspectService;
import org.springframework.stereotype.Service;

/**
 * OCR标记检查服务实现类
 * 继承AbstractBaseService以实现通用的CRUD操作
 * 实现OcrMarkInspectService接口定义的业务功能
 */
@Service
public class OcrMarkInspectServiceImpl extends AbstractBaseService<OcrMarkInspectDO> implements OcrMarkInspectService {
}
