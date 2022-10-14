package com.es7.demoes7.service.impl;

import com.alibaba.fastjson.JSON;
import com.es7.demoes7.entity.EsNews;

import com.es7.demoes7.service.EsEntityService;
import com.es7.demoes7.service.EsNewsService;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.elasticsearch.action.DocWriteResponse;
import org.elasticsearch.action.admin.indices.delete.DeleteIndexRequest;
import org.elasticsearch.action.bulk.BulkRequest;
import org.elasticsearch.action.bulk.BulkResponse;
import org.elasticsearch.action.delete.DeleteRequest;
import org.elasticsearch.action.delete.DeleteResponse;
import org.elasticsearch.action.get.GetRequest;
import org.elasticsearch.action.get.GetResponse;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.action.support.master.AcknowledgedResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.client.indices.CreateIndexRequest;
import org.elasticsearch.client.indices.CreateIndexResponse;
import org.elasticsearch.client.indices.GetIndexRequest;
import org.elasticsearch.common.text.Text;
import org.elasticsearch.common.unit.TimeValue;
import org.elasticsearch.common.xcontent.XContentType;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.rest.RestStatus;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.SearchHits;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightBuilder;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightField;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

@Slf4j
@Service("EsNewsImpl")
public class EsNewsImpl implements EsNewsService {
    @Resource
    RestHighLevelClient restHighLevelClient;
    /**
     * 优秀案例的查询方法
     * @param indexName
     * @param esNews
     * @param formNo
     * @param formSize
     * @return
     */
    @Override
    @SneakyThrows({IOException.class})
    public Object search(String indexName, EsNews esNews, Integer formNo, Integer formSize) {
//分页判断
        formNo= (formNo==null||formNo <=1)? 0:formNo;
        formSize= (formSize==null||formSize<=0)? 10:formSize;
        //根据索引创建查询请求
        SearchRequest searchRequest = new SearchRequest(indexName);
        //构建搜索条件
        BoolQueryBuilder boolQueryBuilder = new BoolQueryBuilder();
        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
//     构建查询语句
//        添加关键字
        boolQueryBuilder.must(QueryBuilders.matchQuery("Content",esNews.getContent()));
//        添加查询字典
        //        前端传个字符串 用空格隔开 下面解析    字典名称
        if (esNews.getType()!=null) {
            String[] split = esNews.getType().split("\\s+");
            ArrayList<String> sites = new ArrayList<String>();
            Collections.addAll(sites, split);
            boolQueryBuilder.filter(QueryBuilders.termsQuery("Type", sites));
        }
        //        分页
        searchSourceBuilder.size(formSize).from(formNo);
//        高亮
        HighlightBuilder highlightBuilder = new HighlightBuilder();
        highlightBuilder.field("Content").preTags("<span style='color:red'>").postTags("</span>");
        searchSourceBuilder.highlighter(highlightBuilder);
        searchSourceBuilder.query(boolQueryBuilder);
//        整理请求
        searchRequest.source(searchSourceBuilder);
        System.out.println("请求体searchRequest:"+searchRequest.source());
//         发送请求
        SearchResponse searchResponse = restHighLevelClient.search(searchRequest,RequestOptions.DEFAULT);
        // 解析结果
        SearchHits hits = searchResponse.getHits();
        List<Map<String, Object>> results = new ArrayList<>();
        for (SearchHit documentFields : hits.getHits()) {
            // 使用新的字段值（高亮），覆盖旧的字段值
            Map<String, Object> sourceAsMap = documentFields.getSourceAsMap();
            // 高亮字段
            Map<String, HighlightField> highlightFields = documentFields.getHighlightFields();
            HighlightField name = highlightFields.get("Content");
            // 替换
            if (name != null){
                Text[] fragments = name.fragments();
                StringBuilder new_name = new StringBuilder();
                for (Text text : fragments) {
                    new_name.append(text);
                }
                sourceAsMap.put("Content",new_name.toString());
            }
            results.add(sourceAsMap);
        }
        return results;
    }


}
