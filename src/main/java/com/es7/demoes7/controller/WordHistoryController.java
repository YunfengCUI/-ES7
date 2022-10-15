package com.es7.demoes7.controller;

import com.es7.demoes7.entity.NewsAbnp;
import com.es7.demoes7.entity.WordHistory;
import com.es7.demoes7.service.WordHistoryService;
import io.swagger.annotations.Api;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.Date;

@RestController
@RequestMapping("/wordHostory")
@Api(tags = "搜索记录")
public class WordHistoryController {
    @Resource
    WordHistoryService wordHistoryService;
    @PostMapping("/add")
    String add(WordHistory wordHistory){
        wordHistory.setCreatedTime(new Date());
        return wordHistoryService.save(wordHistory)? "成功":"失败";
    }
}
