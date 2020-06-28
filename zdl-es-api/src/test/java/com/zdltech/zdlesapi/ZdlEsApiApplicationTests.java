package com.zdltech.zdlesapi;

import com.alibaba.fastjson.JSON;
import com.zdltech.zdlesapi.bean.UserItemBean;
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
import org.elasticsearch.common.unit.TimeValue;
import org.elasticsearch.common.xcontent.XContentType;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.index.query.TermsQueryBuilder;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.io.IOException;
import java.util.ArrayList;


/**
 * API 7.8.0  api接口测试
 */
@SpringBootTest
class ZdlEsApiApplicationTests {
    @Autowired
    private RestHighLevelClient restHighLevelClient;

    @Test
    void contextLoads() {
    }

    /**
     * 索引创建 Request
     */

    @Test
    void testCreateIndex() {
        //创建请求
        CreateIndexRequest createIndexRequest = new CreateIndexRequest("zdljava1");
        // 客户端执行请求
        try {
            CreateIndexResponse createIndexResponse = restHighLevelClient.indices().create(createIndexRequest, RequestOptions.DEFAULT);
            System.out.println(createIndexResponse);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 获取索引
     */
    @Test
    void testExistIndex() {
        //创建索引请求
        GetIndexRequest getIndexRequest = new GetIndexRequest("zdljava1");
        // 客户端执行请求
        boolean exists = false;
        try {
            exists = restHighLevelClient.indices().exists(getIndexRequest, RequestOptions.DEFAULT);
        } catch (IOException e) {
            e.printStackTrace();
        }
        System.out.println(exists);
    }

    /**
     * 删除索引
     */
    @Test
    void testDeleteIndex() {
        //创建删除索引请求
        DeleteIndexRequest deleteIndexRequest = new DeleteIndexRequest("zdljava1");
        //客户端执行请求
        try {
            AcknowledgedResponse delete = restHighLevelClient.indices().delete(deleteIndexRequest, RequestOptions.DEFAULT);
            System.out.println(delete.isAcknowledged());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    /**
     * 添加文档
     */
    @Test
    void testAddDocument() {
        //创建对象
        UserItemBean itemBean = new UserItemBean("张东领", 30);
        //创建请求
        IndexRequest indexRequest = new IndexRequest("zdljava1");//相当于数据库
        //设置规格
        indexRequest.id("1");//文档 相当于 表
        indexRequest.timeout(TimeValue.timeValueSeconds(1));
        //设置数据
        indexRequest.source(JSON.toJSONString(itemBean), XContentType.JSON);
        // 客户端执行请求
        try {
            IndexResponse indexResponse = restHighLevelClient.index(indexRequest, RequestOptions.DEFAULT);
            System.out.println(indexResponse);
            System.out.println(JSON.toJSONString(indexResponse));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 获取文档
     */

    @Test
    void testGetDocument() {
        //构建请求参数
        GetRequest getRequest = new GetRequest("zdljava1", "1");
        try {
            boolean exists = restHighLevelClient.exists(getRequest, RequestOptions.DEFAULT);
            System.out.println(exists);
            GetResponse documentFields = restHighLevelClient.get(getRequest, RequestOptions.DEFAULT);
            System.out.println(JSON.toJSONString(documentFields));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 更新文档
     */
    @Test
    void testUpdateDocument() {
        //构建请求
        UpdateRequest updateRequest = new UpdateRequest("zdljava1", "1");
        updateRequest.timeout("1s");
        UserItemBean userItemBean = new UserItemBean("张东领01", 33);
        updateRequest.doc(JSON.toJSONString(userItemBean), XContentType.JSON);
        try {
            UpdateResponse updateResponse = restHighLevelClient.update(updateRequest, RequestOptions.DEFAULT);
            System.out.println(JSON.toJSONString(updateResponse));
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    /**
     * 删除文档
     */
    @Test
    void testDeleteDocument() {
        //构建请求
        DeleteRequest request = new DeleteRequest("zdljava1");
        request.id("1");
        request.timeout("1s");
        //客户端执行请求
        try {
            DeleteResponse deleteResponse = restHighLevelClient.delete(request, RequestOptions.DEFAULT);
            System.out.println(JSON.toJSONString(deleteResponse));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 批量
     */
    @Test
    void testBRequest() {
        BulkRequest request = new BulkRequest();
        request.timeout("10s");
        ArrayList<UserItemBean> data = new ArrayList<>();
        data.add(new UserItemBean("zhangsan", 6));
        data.add(new UserItemBean("lisi", 18));
        data.add(new UserItemBean("wangwu", 1000));
        data.add(new UserItemBean("liuche", 890));

        for (int i = 0; i < data.size(); i++) {
            request.add(
                    new IndexRequest("zdljava1").id("" + (i + 5)).source(JSON.toJSONString(data.get(i)), XContentType.JSON)
            );
        }
        try {
            BulkResponse bulkItemResponses = restHighLevelClient.bulk(request, RequestOptions.DEFAULT);
            System.out.println(JSON.toJSONString(bulkItemResponses));
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    /**
     * 查询
     */
    @Test
    void testSearchRequest(){
        SearchRequest searchRequest = new SearchRequest("zdljava1");
        //构建搜索条件
        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
        searchSourceBuilder.highlighter();
        // 搜索Query
        TermsQueryBuilder termsQueryBuilder = QueryBuilders.termsQuery("userName.keyword", "张三","李四");
//        TermsQueryBuilder termsQueryBuilder = QueryBuilders.termsQuery("userName", "zhangsan","lisi");
//        QueryBuilders.matchAllQuery();
        searchSourceBuilder.query(termsQueryBuilder);
        searchSourceBuilder.timeout(TimeValue.timeValueSeconds(60));

        searchRequest.source(searchSourceBuilder);

        try {
            SearchResponse searchResponse = restHighLevelClient.search(searchRequest, RequestOptions.DEFAULT);
            System.out.println(JSON.toJSONString(searchResponse));

            for (SearchHit item: searchResponse.getHits().getHits()) {
                System.out.println("================");
                System.out.println(item.getSourceAsMap());
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
