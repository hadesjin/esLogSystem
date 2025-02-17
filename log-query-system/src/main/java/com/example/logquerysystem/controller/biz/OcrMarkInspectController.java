package com.example.logquerysystem.controller.biz;

import com.example.logquerysystem.entity.OcrMarkInspectDO;
import com.example.logquerysystem.service.orm.data.intf.OcrMarkInspectService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.util.List;
import java.util.logging.Logger;

@RestController
@RequestMapping("/api/logs")
public class OcrMarkInspectController {
    Logger logger = Logger.getLogger(OcrMarkInspectController.class.getName());
    @Autowired
    private OcrMarkInspectService ocrMarkInspectService;

    @GetMapping("/getMarkAll")
    public List<OcrMarkInspectDO> getMarAll() throws IOException {
        return ocrMarkInspectService.selectAll();
    }
}