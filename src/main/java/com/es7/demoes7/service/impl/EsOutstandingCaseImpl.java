package com.es7.demoes7.service.impl;

import com.alibaba.fastjson.JSON;

import com.es7.demoes7.entity.EsOutstandingCase;
import com.es7.demoes7.service.EsEntityService;
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
import org.elasticsearch.index.query.MatchQueryBuilder;
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
@Service("EsOutstandingCaseImpl")
public class EsOutstandingCaseImpl implements EsEntityService {
    @Resource
    RestHighLevelClient restHighLevelClient;


    //    ????????????
    @Override
    @SneakyThrows({IOException.class})
    public boolean creatIndex(String indexName) {
        //1.??????????????????
        CreateIndexRequest my_index = new CreateIndexRequest(indexName);
        //2.?????????????????? indicesClient,?????????????????????
        CreateIndexResponse createIndexResponse = restHighLevelClient.indices().create(my_index, RequestOptions.DEFAULT);
        System.out.println(createIndexResponse.isAcknowledged());
        return createIndexResponse.isAcknowledged();
    }
//??????????????????
    @Override
    @SneakyThrows({IOException.class})
    public boolean getIndex(String indexName) {
//        ????????????????????????
        GetIndexRequest request = new GetIndexRequest(indexName);
        return restHighLevelClient.indices().exists(request, RequestOptions.DEFAULT);
    }
    /**
     * ????????????
     * @param indexName
     * @return
     */
    @Override
    @SneakyThrows({IOException.class})
    public String deleteIndex(String indexName) {
//        ????????? ?????? ????????? ???????????? ????????????
        if (!getIndex(indexName))
            return "???????????????";
        DeleteIndexRequest deleteRequest = new DeleteIndexRequest(indexName);
        AcknowledgedResponse delete = restHighLevelClient.indices().delete(deleteRequest, RequestOptions.DEFAULT);
        return delete.isAcknowledged()?"????????????":"????????????";
    }
    /**
     * ?????????????????? ?????????  ??????uuid??????
     * @param indexName
     * @param esOutstandingCase
     * @return
     */
    @Override
    @SneakyThrows({IOException.class})
    public String addData(String indexName, EsOutstandingCase esOutstandingCase) {
        //????????????
        //????????????
        IndexRequest request = new IndexRequest(indexName);
        //?????? put /jacob_index/_doc/1 ?????? id ?????? ????????????id
        request.id(esOutstandingCase.getEsAttachmentUuid());
        request.timeout(TimeValue.timeValueSeconds(1));
        //?????????????????????,???Json?????????
        IndexRequest source = request.source(JSON.toJSONString(esOutstandingCase), XContentType.JSON);
        //?????????????????????,??????????????????
        IndexResponse index = restHighLevelClient.index(request, RequestOptions.DEFAULT);
        System.out.println(index.toString());
        System.out.println(index.status());//???????????????????????????
        return index.status().toString();
    }
    /**
     * ??????????????????
     * @param indexName
     * @param id
     * @return
     */
    @Override
    @SneakyThrows({IOException.class})
    public Map<String, Object> getData(String indexName, String id) {
        GetRequest getRequest = new GetRequest(indexName, id);
        System.out.println(getRequest);
        GetResponse getResponse = restHighLevelClient.get(getRequest,RequestOptions.DEFAULT);
        System.out.println(getResponse);
        return getResponse.getSource();
    }
    /**
     * ?????????????????? ????????????
     * @param indexName
     * @param id
     * @return
     */
    @Override
    @SneakyThrows({IOException.class})
    public boolean existsData(String indexName, String id) {
        GetRequest getRequest = new GetRequest(indexName, id);
        System.out.println(getRequest);
        GetResponse getResponse = restHighLevelClient.get(getRequest,RequestOptions.DEFAULT);
        System.out.println(getResponse);
        return getResponse.isExists();
    }
    /**
     * ??????????????????
     * @param indexName
     * @param id
     * @return
     */
    @Override
    @SneakyThrows({IOException.class})
    public DocWriteResponse.Result deleteDocument(String indexName, String id) {
        DeleteRequest deleteRequest = new DeleteRequest(indexName, id);
        deleteRequest.timeout("1s");
        DeleteResponse delete = restHighLevelClient.delete(deleteRequest, RequestOptions.DEFAULT);
//        System.out.println(delete.status());
//        System.out.println(delete);
        return delete.getResult();
    }

    /**
     * ????????????|????????????
     * @param indexName
     * @param esAuditBasyArrayList
     * @return
     */
    @Override
    @SneakyThrows({IOException.class})
    public RestStatus bulkAdd_Update_EsOutstandingCase_Document(String indexName, ArrayList<EsOutstandingCase> esAuditBasyArrayList) {
        BulkRequest bulkRequest = new BulkRequest();
        bulkRequest.timeout("10s");
        //???????????????
        //?????????????????????????????????????????????
//        for (int i = 0; i < esAuditBasyArrayList.size(); i++) {
//            //??????id???????????????id
//            bulkRequest.add(
//                    new IndexRequest(indexName)
//                            .id(""+(i+1))
//                            .source(JSON.toJSONString(esAuditBasyArrayList.get(i)),XContentType.JSON));
//        }
        for (EsOutstandingCase Es:
             esAuditBasyArrayList) {
            bulkRequest.add(new IndexRequest(indexName)
                    .id(Es.getEsAttachmentUuid())
                    .source(JSON.toJSONString(Es),XContentType.JSON)
                    );

        }
        BulkResponse bulk = restHighLevelClient.bulk(bulkRequest, RequestOptions.DEFAULT);
        return bulk.status();
    }

    /**
     * ???????????????????????????
     * @param indexName
     * @param esOutstandingCase
     * @param formNo
     * @param formSize
     * @return
     */
    @Override
    @SneakyThrows({IOException.class})
    public Object search(String indexName, EsOutstandingCase esOutstandingCase, Integer formNo, Integer formSize) {
//????????????
        formNo= (formNo==null||formNo <=1)? 0:formNo;
        formSize= (formSize==null||formSize<=0)? 10:formSize;
        //??????????????????????????????
        SearchRequest searchRequest = new SearchRequest(indexName);
        //??????????????????
        BoolQueryBuilder boolQueryBuilder = new BoolQueryBuilder();
        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
//     ??????????????????
//        ???????????????
        boolQueryBuilder.must(QueryBuilders.matchQuery("esContent",esOutstandingCase.getEsContent()));
//        ??????????????????
        //        ????????????????????? ??????????????? ????????????    ????????????
        if (esOutstandingCase.getEsDictname()!=null) {
            String[] split = esOutstandingCase.getEsDictname().split("\\s+");
            ArrayList<String> sites = new ArrayList<String>();
            Collections.addAll(sites, split);
            boolQueryBuilder.filter(QueryBuilders.termsQuery("esDictname", sites));
        }
//        ??????????????????
        if (esOutstandingCase.getEsDataTitle()!=null && !esOutstandingCase.getEsDataTitle().equals(""))
            boolQueryBuilder.must(QueryBuilders.matchQuery("esDataTitle",esOutstandingCase.getEsDataTitle()));
        //        ??????
        searchSourceBuilder.size(formSize).from(formNo);
//        ??????
        HighlightBuilder highlightBuilder = new HighlightBuilder();
        highlightBuilder.field("esContent").preTags("<span style='color:red'>").postTags("</span>");
        searchSourceBuilder.highlighter(highlightBuilder);
        searchSourceBuilder.query(boolQueryBuilder);
//        ????????????
        searchRequest.source(searchSourceBuilder);
        System.out.println("?????????searchRequest:"+searchRequest.source());
//         ????????????
        SearchResponse searchResponse = restHighLevelClient.search(searchRequest,RequestOptions.DEFAULT);
        // ????????????
        SearchHits hits = searchResponse.getHits();
        List<Map<String, Object>> results = new ArrayList<>();
        for (SearchHit documentFields : hits.getHits()) {
            // ?????????????????????????????????????????????????????????
            Map<String, Object> sourceAsMap = documentFields.getSourceAsMap();
            // ????????????
            Map<String, HighlightField> highlightFields = documentFields.getHighlightFields();
            HighlightField name = highlightFields.get("esContent");
            // ??????
            if (name != null){
                Text[] fragments = name.fragments();
                StringBuilder new_name = new StringBuilder();
                for (Text text : fragments) {
                    new_name.append(text);
                }
                sourceAsMap.put("esContent",new_name.toString());
            }
            results.add(sourceAsMap);
        }
        return results;
    }


}
