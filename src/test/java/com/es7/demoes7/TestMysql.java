package com.es7.demoes7;

import com.es7.demoes7.entity.NewsAbnp;
import com.es7.demoes7.service.MSGService;
import com.es7.demoes7.service.NewsAbnpService;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@SpringBootTest
public class TestMysql {
    @Resource
    NewsAbnpService service;
    @Test
    void testselectone(){
//        System.out.println(service.getById(1));
//        System.out.println(service.getById(1));
        List<NewsAbnp> list = new ArrayList<>();
        list = service.list();
        System.out.println(list);
    }
    @Resource
    MSGService msgService;
    @Test
    void test(){
        Map map = new HashMap();
        map.put("code",5418);
        msgService.send(map,"17742162921");
    }
}
