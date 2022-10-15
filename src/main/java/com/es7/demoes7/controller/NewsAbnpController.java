package com.es7.demoes7.controller;

import com.es7.demoes7.entity.NewsAbnp;
import com.es7.demoes7.service.NewsAbnpService;
import io.swagger.annotations.Api;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

@RestController
@RequestMapping("/newsAbnp")
@Api(tags = "后台处理新闻")
public class NewsAbnpController {
    @Resource
    NewsAbnpService newsAbnpService;
    @PostMapping("/getById")
    NewsAbnp getById(String uuid){
        return newsAbnpService.getById(uuid);
    }
}
