package com.es7.demoes7;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.es7.demoes7.entity.User;
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
import org.elasticsearch.action.update.UpdateRequest;
import org.elasticsearch.action.update.UpdateResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.client.indices.CreateIndexRequest;
import org.elasticsearch.client.indices.CreateIndexResponse;
import org.elasticsearch.client.indices.GetIndexRequest;
import org.elasticsearch.common.text.Text;
import org.elasticsearch.common.unit.TimeValue;
import org.elasticsearch.common.xcontent.XContentType;
import org.elasticsearch.index.query.*;
import org.elasticsearch.rest.RestStatus;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.aggregations.AggregationBuilder;
import org.elasticsearch.search.aggregations.AggregationBuilders;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.elasticsearch.search.fetch.subphase.FetchSourceContext;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightBuilder;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightField;
import org.elasticsearch.search.sort.FieldSortBuilder;
import org.elasticsearch.search.sort.SortBuilders;
import org.elasticsearch.search.sort.SortOrder;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

@SpringBootTest
class EsApiApplicationTests {

    @Autowired
    private RestHighLevelClient restHighLevelClient;
    //高级客户端。索引api内容
    //测试索引的创建
    @Test
    void creatIndex() throws IOException {
        //1.创建索引请求
        CreateIndexRequest jacob_index = new CreateIndexRequest("jacob_index");
        //2.执行创建请求 indicesClient,请求后获得相应
        CreateIndexResponse createIndexResponse = restHighLevelClient.indices().create(jacob_index, RequestOptions.DEFAULT);
        System.out.println(createIndexResponse);
    }


    @Test
        //获取索引
    void getIndex() throws IOException {
        GetIndexRequest request = new GetIndexRequest("jacob_index");
        boolean exists = restHighLevelClient.indices().exists(request, RequestOptions.DEFAULT);
        System.out.println(exists);
    }

    @Test
        //删除索引
    void deleteIndex() throws IOException {
        DeleteIndexRequest deleteRequest = new DeleteIndexRequest("ttese");
        AcknowledgedResponse delete = restHighLevelClient.indices().delete(deleteRequest, RequestOptions.DEFAULT);
        System.out.println(delete.isAcknowledged());
    }


    @Test
        //添加文档测试
    void testAddText() throws IOException {

        //创建对象
        User user = new User("张三", 2);
        //创建请求
        IndexRequest request = new IndexRequest("jacob_index");
        //规则 put /jacob_index/_doc/1
        request.id("1");
        request.timeout(TimeValue.timeValueSeconds(1));

        //将数据放入请求,是Json格式的
        IndexRequest source = request.source(JSON.toJSONString(user), XContentType.JSON);

        //客户端发送请求,获取相应结果
        IndexResponse index = restHighLevelClient.index(request, RequestOptions.DEFAULT);
        System.out.println(index.toString());
        System.out.println(index.status());//对应命令返回的状态
    }

    //获取文档，判断文档是否存在
    //规则 get /index/1
    @Test
    void getText() throws IOException {

        GetRequest getRequest = new GetRequest("jacob_index", "1");
        System.out.println(getRequest);
        GetResponse getResponse = restHighLevelClient.get(getRequest,RequestOptions.DEFAULT);
        System.out.println(getResponse);
        //不获取
        getRequest.fetchSourceContext(new FetchSourceContext(false));
        getRequest.storedFields("_none_");
        boolean exists = restHighLevelClient.exists(getRequest, RequestOptions.DEFAULT);
        System.out.println(exists);
    }

    //修改文档信息
    @Test
    void updateDocument() throws IOException {
        UpdateRequest request = new UpdateRequest("jacob_index", "1");
        request.timeout("1s");
        User user = new User("李四", 2);
        UpdateRequest doc = request.doc(JSON.toJSONString(user),XContentType.JSON);
        UpdateResponse update = restHighLevelClient.update(request, RequestOptions.DEFAULT);
        System.out.println(update);
    }


    //删除文档信息
    @Test
    void DeleteDocument() throws IOException {

        DeleteRequest deleteRequest = new DeleteRequest("jacob_index", "2");
        deleteRequest.timeout("1s");
        DeleteResponse delete = restHighLevelClient.delete(deleteRequest, RequestOptions.DEFAULT);
        System.out.println(delete.status());
        System.out.println(delete);
    }

    //批量导入数据
    @Test
    void BulkAddDocument() throws IOException {

        BulkRequest bulkRequest = new BulkRequest();
        bulkRequest.timeout("10s");
        ArrayList<User> userArrayList = new ArrayList<>();
        userArrayList.add(new User("张三33",21112));
        userArrayList.add(new User("张三33",21112));
        userArrayList.add(new User("张三56",2234));
        userArrayList.add(new User("张三896",45));
        userArrayList.add(new User("张三567567",45));
        userArrayList.add(new User("张三vxc",22));
        userArrayList.add(new User("张三前往瑞芳但是v",545));
        userArrayList.add(new User("张三仔细擦",45));


        //批处理请求
        //批量删除和批量更新都在这里操作
        for (int i = 0; i < userArrayList.size(); i++) {
            //不用id会随机生成id
            bulkRequest.add(
                    new IndexRequest("jacob_index")
                            .id(""+(i+20))
                            .source(JSON.toJSONString(userArrayList.get(i)),XContentType.JSON));

        }

        BulkResponse bulk = restHighLevelClient.bulk(bulkRequest, RequestOptions.DEFAULT);
        System.out.println(bulk.toString());
    }



    //查询
    //步骤
    //1.searchRequest 搜索请求
    //2.SearchSourceBuilder 搜索的条件构造
    //XXXX Builder对应实现不同的功能，SearchSourceBuilder源码里都有

    @Test
    void Search() throws IOException {
        SearchRequest searchRequest = new SearchRequest("jacob_index");
        //构建搜索条件
        SearchSourceBuilder builder = new SearchSourceBuilder();
        //查询条件，使用QueryBuilders快速匹配
        //termQuery精确
//        QueryBuilders.matchAllQuery(); //匹配所有
        TermQueryBuilder termQuery = QueryBuilders.termQuery("name", "张");
        builder.query(termQuery);
        //分页from、size
//        builder.from(0);
//        builder.size(2);
        builder.timeout(new TimeValue(60, TimeUnit.SECONDS));
        searchRequest.source(builder);
        SearchResponse searchResponse = restHighLevelClient.search(searchRequest, RequestOptions.DEFAULT);
        //循环遍历查询结果 两层hit才能到数据体
        System.out.println(JSON.toJSONString(searchResponse.getHits().getHits()));
        System.out.println("+++++++++++++++++++++++++++++++++");
        for (SearchHit documentFields : searchResponse.getHits()) {
            System.out.println(documentFields.getSourceAsMap());
        }
    }


//    单词查询 严格查询
    @Test
 void queryTerm() throws IOException {
//        根据索引创建查询请求
     SearchRequest searchRequest = new SearchRequest("jacob_index");
     //构建搜索条件
     SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
//     构建查询语句
     searchSourceBuilder.query(QueryBuilders.termQuery("name.keyword","张三33"));
     System.out.println("searchSourceBuilder=============="+ searchSourceBuilder);
     searchRequest.source(searchSourceBuilder);
     SearchResponse response = restHighLevelClient.search(searchRequest,RequestOptions.DEFAULT);
     System.out.println(JSONObject.toJSON(response));
 }
    //    单词查询 严格查询2
    @Test
    void queryTerm2() throws IOException {
//        根据索引创建查询请求
        SearchRequest searchRequest = new SearchRequest("jacob_index");
        //构建搜索条件
        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
//     构建查询语句 将不进行score计算，从而提高查询效率
        searchSourceBuilder.query(QueryBuilders.constantScoreQuery(QueryBuilders.termQuery("name.keyword","张三33")));
        System.out.println("searchSourceBuilder=============="+ searchSourceBuilder);
        searchRequest.source(searchSourceBuilder);
        SearchResponse response = restHighLevelClient.search(searchRequest,RequestOptions.DEFAULT);
        System.out.println(JSONObject.toJSON(response));
    }
    //    多词查询 严格查询1
    @Test
    void queryTerms() throws IOException {

        String[] split = "张三33  张三56".split("\\s+ ");
        ArrayList<String> sites = new ArrayList<String>(Arrays.asList(split));
//        根据索引创建查询请求
        SearchRequest searchRequest = new SearchRequest("jacob_index");
        //构建搜索条件
        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
//     构建查询语句
        searchSourceBuilder.query(QueryBuilders.termsQuery("name.keyword", sites));
        System.out.println("searchSourceBuilder=============="+ searchSourceBuilder);
        searchRequest.source(searchSourceBuilder);
        SearchResponse response = restHighLevelClient.search(searchRequest,RequestOptions.DEFAULT);
        System.out.println(JSONObject.toJSON(response));
    }
    //    范围查询 严格查询1
    @Test
    void rangeQuery() throws IOException {
//        根据索引创建查询请求
        SearchRequest searchRequest = new SearchRequest("jacob_index");
        //构建搜索条件
        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
//     构建查询语句
        searchSourceBuilder.query(QueryBuilders.rangeQuery("age").gte(10).lte(45));
        System.out.println("searchSourceBuilder=============="+ searchSourceBuilder);
        searchRequest.source(searchSourceBuilder);
        SearchResponse response = restHighLevelClient.search(searchRequest,RequestOptions.DEFAULT);
        System.out.println(JSONObject.toJSON(response));
    }
    //    前缀查询
    @Test
    void prefixQuery() throws IOException {
//        根据索引创建查询请求
        SearchRequest searchRequest = new SearchRequest("jacob_index");
        //构建搜索条件
        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
//     构建查询语句
        searchSourceBuilder.query(QueryBuilders.prefixQuery("name.keyword","张三5"));
        System.out.println("searchSourceBuilder=============="+ searchSourceBuilder);
        searchRequest.source(searchSourceBuilder);
        SearchResponse response = restHighLevelClient.search(searchRequest,RequestOptions.DEFAULT);
        System.out.println(JSONObject.toJSON(response));
    }
    //    通配符查询
    @Test
    void wildcardQuery() throws IOException {
//        根据索引创建查询请求
        SearchRequest searchRequest = new SearchRequest("jacob_index");
        //构建搜索条件
        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
//     构建查询语句
        searchSourceBuilder.query(QueryBuilders.wildcardQuery("name.keyword","张*6*"));
        System.out.println("searchSourceBuilder=============="+ searchSourceBuilder);
        searchRequest.source(searchSourceBuilder);
        SearchResponse response = restHighLevelClient.search(searchRequest,RequestOptions.DEFAULT);
        System.out.println(JSONObject.toJSON(response));
    }
    //    多字段查询
    @Test
    void multiFieldQuery() throws IOException {
//        根据索引创建查询请求
        SearchRequest searchRequest = new SearchRequest("jacob_index");
        //构建搜索条件
        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
//     构建查询语句
        searchSourceBuilder.query(QueryBuilders.boolQuery()
                .must(QueryBuilders.termQuery("age", "45"))
                .must(QueryBuilders.termQuery("name.keyword", "张三896"))
        );
        System.out.println("searchSourceBuilder=============="+ searchSourceBuilder);
        searchRequest.source(searchSourceBuilder);
        SearchResponse response = restHighLevelClient.search(searchRequest,RequestOptions.DEFAULT);
        System.out.println(JSONObject.toJSON(response));
    }

    //    复杂条件查询 es现在数据不够多 还没测试
    @Test
    void boolQuery() throws IOException {
//        根据索引创建查询请求
        SearchRequest searchRequest = new SearchRequest("jacob_index");
        //构建搜索条件
        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
// 构建查询语句
        BoolQueryBuilder boolQueryBuilder = QueryBuilders.boolQuery()
                .must(QueryBuilders.termQuery("sex", "女"))
                .must(QueryBuilders.rangeQuery("age").gte(30).lte(40))
                .mustNot(QueryBuilders.termQuery("sect.keyword", "明教"))
                .should(QueryBuilders.termQuery("address.keyword", "峨眉山"))
                .should(QueryBuilders.rangeQuery("power.keyword").gte(50).lte(80))
                .minimumShouldMatch(1);  // 设置should至少需要满足几个条件

// 将BoolQueryBuilder构建到SearchSourceBuilder中
        searchSourceBuilder.query(boolQueryBuilder);
        System.out.println("searchSourceBuilder=============="+ searchSourceBuilder);
        searchRequest.source(searchSourceBuilder);
        SearchResponse response = restHighLevelClient.search(searchRequest,RequestOptions.DEFAULT);
        System.out.println(JSONObject.toJSON(response));
    }

    //    bool条件查询 filter与must基本一样，不同的是filter不计算评分，效率更高。
    @Test
    void bool_filter_Query() throws IOException {
//        根据索引创建查询请求
        SearchRequest searchRequest = new SearchRequest("jacob_index");
        //构建搜索条件
        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
//        构建查询语句
        searchSourceBuilder.query(QueryBuilders.boolQuery()
                .filter(QueryBuilders.termQuery("age", "45"))
        );
        System.out.println("searchSourceBuilder=============="+ searchSourceBuilder);
        searchRequest.source(searchSourceBuilder);
        SearchResponse response = restHighLevelClient.search(searchRequest,RequestOptions.DEFAULT);
        System.out.println(JSONObject.toJSON(response));
    }
    //    bool条件查询 filter和must、must_not同级，相当于子查询：
    @Test
    void bool_filter_Query2() throws IOException {
//        根据索引创建查询请求
        SearchRequest searchRequest = new SearchRequest("jacob_index");
        //构建搜索条件
        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
//        构建查询语句
        searchSourceBuilder.query(QueryBuilders.boolQuery()
                .must(QueryBuilders.termQuery("name.keyword", "张三33"))
                .filter(QueryBuilders.termQuery("age", "21112"))
        );
        System.out.println("searchSourceBuilder=============="+ searchSourceBuilder);
        searchRequest.source(searchSourceBuilder);
        SearchResponse response = restHighLevelClient.search(searchRequest,RequestOptions.DEFAULT);
        System.out.println(JSONObject.toJSON(response));
    }
    //    bool条件查询 将must、must_not置于filter下，这种方式是最常用的：
    @Test
    void bool_filter_Query3() throws IOException {
//        根据索引创建查询请求
        SearchRequest searchRequest = new SearchRequest("jacob_index");
        //构建搜索条件
        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
//        构建查询语句
        searchSourceBuilder.query(QueryBuilders.boolQuery()
                .filter(QueryBuilders.boolQuery()
                        .must(QueryBuilders.termQuery("name.keyword", "张三33"))
                        .mustNot(QueryBuilders.termQuery("age", "21112")))
        );
        searchSourceBuilder.size(20);
        System.out.println("searchSourceBuilder=============="+ searchSourceBuilder);
        searchRequest.source(searchSourceBuilder);
        SearchResponse response = restHighLevelClient.search(searchRequest,RequestOptions.DEFAULT);
        System.out.println(JSONObject.toJSON(response));
    }
//聚合查询 找出最大age
    @Test
    public void maxQueryTest() throws IOException {
        // 聚合查询条件
//        AggregationBuilder minBuilder = AggregationBuilders.min("min_age").field("age");
//        AggregationBuilder avgBuilder = AggregationBuilders.avg("min_age").field("age");
//        AggregationBuilder sumBuilder = AggregationBuilders.sum("min_age").field("age");
//        AggregationBuilder countBuilder = AggregationBuilders.count("min_age").field("age");
        AggregationBuilder aggBuilder = AggregationBuilders.max("max_age").field("age");
        SearchRequest searchRequest = new SearchRequest("jacob_index");
        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
        // 将聚合查询条件构建到SearchSourceBuilder中
        searchSourceBuilder.aggregation(aggBuilder);
        System.out.println("searchSourceBuilder----->" + searchSourceBuilder);

        searchRequest.source(searchSourceBuilder);
        // 执行查询，获取SearchResponse
        SearchResponse response = restHighLevelClient.search(searchRequest, RequestOptions.DEFAULT);
        System.out.println(JSONObject.toJSON(response));

    }

    //聚合查询 查询一共有多少个age
    @Test
    public void max_age_QueryTest() throws IOException {
        // 聚合查询条件
        SearchRequest searchRequest = new SearchRequest("jacob_index");
        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
        // 聚合查询

        AggregationBuilder aggBuilder = AggregationBuilders.cardinality("age_count").field("age.keyword");
        searchSourceBuilder.size(0);
        // 将聚合查询条件构建到SearchSourceBuilder中
        searchSourceBuilder.aggregation(aggBuilder);
        System.out.println("searchSourceBuilder----->" + searchSourceBuilder);

        searchRequest.source(searchSourceBuilder);
        // 执行查询，获取SearchResponse
        SearchResponse response = restHighLevelClient.search(searchRequest, RequestOptions.DEFAULT);
        System.out.println(JSONObject.toJSON(response));
    }
    //聚合查询 查询多少类name
    @Test
    public void nameage_QueryTest() throws IOException {
        // 聚合查询条件
        SearchRequest searchRequest = new SearchRequest("jacob_index");
        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
        // 聚合查询
        searchSourceBuilder.size(0);
        // 按sect分组
        AggregationBuilder aggBuilder = AggregationBuilders
                .terms("name_count").field("name.keyword");
        // 将聚合查询条件构建到SearchSourceBuilder中
        searchSourceBuilder.aggregation(aggBuilder);
        System.out.println("searchSourceBuilder----->" + searchSourceBuilder);

        searchRequest.source(searchSourceBuilder);
        // 执行查询，获取SearchResponse
        SearchResponse response = restHighLevelClient.search(searchRequest, RequestOptions.DEFAULT);
        System.out.println(JSONObject.toJSON(response));
    }
    //聚合查询 最大值是age 而且name是张*模糊查询
    @Test
    public void nameage_QueryTest2() throws IOException {
        // 聚合查询条件
        SearchRequest searchRequest = new SearchRequest("jacob_index");
        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
        // 聚合查询条件
//        AggregationBuilder maxBuilder = AggregationBuilders.max("max_age").field("age");
// 等值查询
//        searchSourceBuilder.query(QueryBuilders.wildcardQuery("name.keyword", "张*"));
        searchSourceBuilder.query(QueryBuilders.termQuery("name", "张三"));
//        searchSourceBuilder.aggregation(maxBuilder);
        System.out.println("searchSourceBuilder----->" + searchSourceBuilder);

        //高亮
        HighlightBuilder highlightBuilder = new HighlightBuilder();
        //设置高亮字段
        highlightBuilder.field("name");
        //如果要多个字段高亮,这项要为false
        highlightBuilder.requireFieldMatch(true);
        highlightBuilder.preTags("<span style='color:red'>");
        highlightBuilder.postTags("</span>");

        //下面这两项,如果你要高亮如文字内容等有很多字的字段,必须配置,不然会导致高亮不全,文章内容缺失等
        highlightBuilder.fragmentSize(800000); //最大高亮分片数
        highlightBuilder.numOfFragments(0); //从第一个分片获取高亮片段
        searchSourceBuilder.highlighter(highlightBuilder);

        searchRequest.source(searchSourceBuilder);
        // 执行查询，获取SearchResponse
        SearchResponse response = restHighLevelClient.search(searchRequest, RequestOptions.DEFAULT);
        System.out.println(JSONObject.toJSON(response));

//        解析高亮
        List<Map<String, Object>> list = new ArrayList<>();
        for (SearchHit hit : response.getHits().getHits()) {
            Map<String, Object> sourceAsMap = hit.getSourceAsMap();
            //解析高亮字段
            Map<String, HighlightField> highlightFields = hit.getHighlightFields();
            HighlightField field= highlightFields.get("name");
            if(field!= null){
                Text[] fragments = field.fragments();
                String n_field = "";
                for (Text fragment : fragments) {
                    n_field += fragment;
                }
                //高亮标题覆盖原标题
                sourceAsMap.put("name",n_field);
            }
            list.add(hit.getSourceAsMap());
        }
        System.out.println(list);
    }

//    高亮查询
    @Test
    public void searchIndex() throws Exception {
        SearchRequest searchRequest = new SearchRequest("jacob_index");
        SearchSourceBuilder ssb = new SearchSourceBuilder();
        QueryBuilder qb = new MatchQueryBuilder("name","张三");
        ssb.query(qb);

        HighlightBuilder highlightBuilder = new HighlightBuilder();
        highlightBuilder.field("name");

        ssb.highlighter(highlightBuilder);

        searchRequest.source(ssb);
        SearchResponse response = restHighLevelClient.search(searchRequest, RequestOptions.DEFAULT);

        SearchHit[] hits = response.getHits().getHits();
        for (SearchHit hit : hits) {
            String record = hit.getSourceAsString();
            HighlightField highlightField = hit.getHighlightFields().get("name");
            for (Text fragment : highlightField.getFragments()) {
                System.out.println(fragment.string());
            }
        }
    }

    @Test
    void tttt(){
        String xx = "张三,李四";
        String[] split = xx.split(",");
        for (String s:
             split) {
            System.out.println(s);
        }
    }

}

