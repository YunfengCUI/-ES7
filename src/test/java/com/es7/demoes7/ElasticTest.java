package com.es7.demoes7;

import org.elasticsearch.action.get.GetRequest;
import org.elasticsearch.action.get.GetResponse;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.client.indices.CreateIndexRequest;
import org.elasticsearch.client.indices.CreateIndexResponse;
import org.elasticsearch.client.indices.GetIndexRequest;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.io.IOException;

@SpringBootTest
public class ElasticTest {

    @Autowired
    RestHighLevelClient restHighLevelClient;

    @Test
    public void test() throws IOException {

        final GetIndexRequest indexRequest = new GetIndexRequest("boot");
        final boolean exists = restHighLevelClient.indices().exists(indexRequest, RequestOptions.DEFAULT);
        System.out.println(exists);

    }

    @Test
    public void test01() throws Exception {
        final GetRequest request = new GetRequest("boot", "1");
        final GetResponse response = restHighLevelClient.get(request, RequestOptions.DEFAULT);
        final String string = response.toString();
        System.out.println(string);
        System.out.println("-------------------------");


    }

//    {"_index":"boot","_type":"_doc","_id":"1","_version":2,"_seq_no":4,"_primary_term":3,"found":true,"_source":{"name":"Jaskson","age":18,"gender":"male","desc":["码农","直男","女装大佬"]}}


    @Test
    public void test02() throws Exception {
        SearchResponse search = restHighLevelClient.search(new SearchRequest("boot"), RequestOptions.DEFAULT);
        System.out.println(search);
    }
    //测试索引的创建
    @Test
    void creatIndex() throws IOException {
        //1.创建索引请求
        CreateIndexRequest boot = new CreateIndexRequest("boot");
        //2.执行创建请求 indicesClient,请求后获得相应
        CreateIndexResponse createIndexResponse = restHighLevelClient.indices().create(boot, RequestOptions.DEFAULT);
        System.out.println(createIndexResponse.toString());
    }

}

