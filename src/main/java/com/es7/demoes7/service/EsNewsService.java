package com.es7.demoes7.service;


import com.es7.demoes7.entity.EsNews;
import com.es7.demoes7.entity.EsOutstandingCase;
import org.elasticsearch.action.DocWriteResponse;
import org.elasticsearch.rest.RestStatus;

import java.util.ArrayList;
import java.util.Map;

public interface EsNewsService {
    //    查询数据 优秀案例
    Object search(String indexName, EsNews esNews, Integer formNo, Integer formSize);

}