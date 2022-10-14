package com.es7.demoes7.controller;
import com.es7.demoes7.entity.EsNews;
import com.es7.demoes7.service.EsNewsService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import javax.annotation.Resource;
@Api(tags = "新闻控制层")
@RequestMapping("/esNews")
@RestController
@Slf4j
public class EsNewsController {
    private final String IndexName ="news_abnp";
    @Resource
    @Qualifier("EsNewsImpl")
    EsNewsService esNewsService;

    @PostMapping("/search")
    @ApiOperation(value = "查询数据", notes = "返回数据体")
    public Object search(@RequestBody EsNews esNews ){
        System.out.println(esNews);
        return esNewsService.search(IndexName,esNews,esNews.getFormNo(), esNews.getFormSize());
    }


}
