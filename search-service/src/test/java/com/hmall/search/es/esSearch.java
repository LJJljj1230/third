package com.hmall.search.es;

import cn.hutool.core.collection.IterableIter;
import cn.hutool.json.JSONUtil;
import com.hmall.search.domain.po.itemDoc;
import org.apache.http.HttpHost;
import org.apache.lucene.search.TotalHits;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.SearchHits;
import org.elasticsearch.search.aggregations.AggregationBuilder;
import org.elasticsearch.search.aggregations.AggregationBuilders;
import org.elasticsearch.search.sort.SortOrder;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import javax.naming.directory.SearchResult;
import java.io.IOException;

@SpringBootTest
public class esSearch {
    private RestHighLevelClient client;
    @BeforeEach
    public void init(){
        client=new RestHighLevelClient(RestClient.builder(HttpHost.create("http://192.168.133.126:9200")));
    }
    @AfterEach
    public void close() throws IOException {
        client.close();

    }
    //test  match_all进行查询
    //测试match_all搜索
    @Test
    public void testMatchAll() throws IOException {
        //1、创建搜索请求
        SearchRequest request = new SearchRequest("items");
        //2、设置查询参数
        request.source().query(QueryBuilders.matchAllQuery());
        //3、发送请求获取响应结果
        SearchResponse response = client.search(request, RequestOptions.DEFAULT);
        //4、解析响应结果
        handleResponse(response);
    }

    private static void handleResponse(SearchResponse response) {
        System.out.println("共搜索到 " + response.getHits().getTotalHits().value + " 条数据");
        //获取查询数组
        SearchHit[] hits = response.getHits().getHits();
        for (SearchHit hit : hits) {
            //获取_source（原始json数据）
            String jsonStr = hit.getSourceAsString();
            itemDoc itemDoc = JSONUtil.toBean(jsonStr, itemDoc.class);
            System.out.println(itemDoc);
        }
    }@Test
    public void testMatch() throws IOException {
        //1、创建搜索请求
        SearchRequest request = new SearchRequest("items");
        //2、设置查询参数
        request.source().query(QueryBuilders.rangeQuery("name").gte(100).lt(1000));
        //3、发送请求获取响应结果
        SearchResponse response = client.search(request, RequestOptions.DEFAULT);
        //4、解析响应结果
        handleResponse(response);
    }
    //布尔复合查询
    @Test
    public void textBool() throws IOException {
        SearchRequest request=new SearchRequest("items");
        BoolQueryBuilder boolQueryBuilder = QueryBuilders.boolQuery();
        boolQueryBuilder.must(QueryBuilders.matchQuery("name","手机"));
        boolQueryBuilder.filter(QueryBuilders.termQuery("brand","华为"));
        boolQueryBuilder.filter(QueryBuilders.rangeQuery("price").lt(30000));
        request.source().query(boolQueryBuilder);
        SearchResponse response = client.search(request, RequestOptions.DEFAULT);
        handleResponse(response);


    }
    //测试排序和分页
    @Test
    public void textSort() throws IOException {
        SearchRequest request = new SearchRequest("items");
        request.source().query(QueryBuilders.matchQuery("name","华为"));
        request.source().from(0).size(50);
        request.source().sort("price", SortOrder.DESC);
        SearchResponse response = client.search(request, RequestOptions.DEFAULT);
        handleResponse(response);
    }
    //测试聚合查询
    @Test
    public void testOrderBy(){
        SearchRequest request = new SearchRequest("items");
        request.source().size(0);
        request.source().aggregation(AggregationBuilders.terms("brand").field("brand").size(20));

    }

}

