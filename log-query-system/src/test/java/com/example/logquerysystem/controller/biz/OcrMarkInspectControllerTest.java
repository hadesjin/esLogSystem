package com.example.logquerysystem.controller.biz;

import static org.hamcrest.Matchers.hasSize;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.alibaba.fastjson.JSON;
import com.example.logquerysystem.LogQuerySystemApplication;
import com.example.logquerysystem.entity.OcrMarkInspectDO;
import com.example.logquerysystem.service.orm.data.intf.OcrMarkInspectService;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.Arrays;
import java.util.List;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(classes = LogQuerySystemApplication.class)
public class OcrMarkInspectControllerTest{

    @Autowired
    private OcrMarkInspectService ocrMarkInspectService;

    @Test
    public void testGetMarAll() throws Exception {
        List<OcrMarkInspectDO> ocrMarkInspectDOS = ocrMarkInspectService.selectAll();
        System.out.println(JSON.toJSONString(ocrMarkInspectDOS));
    }

}