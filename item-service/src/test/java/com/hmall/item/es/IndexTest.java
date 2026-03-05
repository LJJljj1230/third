package com.hmall.item.es;

import cn.hutool.json.JSONUtil;
import com.hmall.common.utils.BeanUtils;
import com.hmall.item.domain.po.Item;
import com.hmall.item.domain.po.itemDoc;
import com.hmall.item.service.IItemService;
import org.apache.http.HttpHost;
import org.elasticsearch.action.admin.indices.delete.DeleteIndexRequest;
import org.elasticsearch.action.get.GetRequest;
import org.elasticsearch.action.get.GetResponse;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.client.indices.CreateIndexRequest;

import org.elasticsearch.client.indices.GetIndexRequest;
import org.elasticsearch.common.xcontent.XContentType;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.io.IOException;
@SpringBootTest
public class IndexTest {
    @Autowired
    private  IItemService service;
    private static final String MAPPING_TEMPLATE = "{\n" +
            "  \"mappings\": {\n" +
            "    \"properties\": {\n" +
            "      \"id\":{\n" +
            "        \"type\": \"keyword\"\n" +
            "      },\n" +
            "      \"name\":{\n" +
            "        \"type\": \"text\",\n" +
            "        \"analyzer\": \"ik_max_word\"\n" +
            "      },\n" +
            "      \"price\":{\n" +
            "        \"type\": \"integer\"\n" +
            "      },\n" +
            "      \"stock\":{\n" +
            "        \"type\": \"integer\"\n" +
            "      },\n" +
            "      \"image\":{\n" +
            "        \"type\":\"keyword\",\n" +
            "        \"index\": false\n" +
            "      },\n" +
            "      \"category\":{\n" +
            "        \"type\": \"keyword\"\n" +
            "      },\n" +
            "      \"brand\":{\n" +
            "        \"type\": \"keyword\"\n" +
            "      },\n" +
            "      \"sold\":{\n" +
            "        \"type\": \"integer\"\n" +
            "      },\n" +
            "      \"commentCount\":{\n" +
            "        \"type\": \"integer\"\n" +
            "      },\n" +
            "      \"isAD\":{\n" +
            "        \"type\": \"boolean\"\n" +
            "      },\n" +
            "      \"updateTime\":{\n" +
            "        \"type\": \"date\"\n" +
            "      }\n" +
            "    }\n" +
            "  }\n" +
            "}";
    private RestHighLevelClient client;

    @BeforeEach
    public void init() {
        client = new RestHighLevelClient(
                RestClient.builder(HttpHost.create("http://192.168.133.126:9200")));
    }

    //测试获取连接
    @Test
    public void testGetConnection() {
        System.out.println(client);
    }

    @AfterEach
    public void close() throws Exception {
        client.close();
    }

    //测试创建索引库 items
//    @Test
//    public void testCreateIndex() throws Exception {
//        //1、创建request对象
//        CreateIndexRequest request = new CreateIndexRequest("items");
//        //2、设置参数
//        request.source(MAPPING_TEMPLATE, XContentType.JSON);
//        //3、发送请求；client.indices() 包括对索引库的所有操作
//        client.indices().create(request, RequestOptions.DEFAULT);
//    }
    //判断索引库是否存在
    @Test
    public void isExist() throws Exception{
        GetIndexRequest request=new GetIndexRequest("items");
        boolean exists=client.indices().exists(request,RequestOptions.DEFAULT);
        System.out.println(exists?"索引存在":"索引不存在");

    }
    //删除索引库

    //根据商品id查询商品，并将查询到的商品保存到es中。
//    @Test
//    public void testCreateItem() throws Exception{
//        //1.根据商品id查询mysql数据库中的商品
//        Item item = service.getById(753999L);
//
//        //2.将item对象装换位es可以接受的itemdoc
//      itemDoc itemDoc = BeanUtils.copyBean(item, itemDoc.class);
//
//        //3.创建文档的请求对象
//        IndexRequest request = new IndexRequest("items").id(itemDoc.getId().toString());
//        //4.设置请求参数
//        String jsonStr = JSONUtil.toJsonStr(itemDoc);
//        request.source(jsonStr,XContentType.JSON);
//        //5.发送请求
//        client.index(request,RequestOptions.DEFAULT);
//
//
//    }
    //查询插入es中的商品信息；
    @Test
    public void testInsert() throws IOException {
        GetRequest request=new GetRequest("items","753999");
        GetResponse response = client.get(request, RequestOptions.DEFAULT);
        System.out.println(response);

    }
    //修改插入es中的商品信息；全局修改，再次插入已有的商品id，原来的会被替换
    @Test
    public void testPutItem() throws Exception{
        //1.根据商品id查询mysql数据库中的商品
        Item item = service.getById(753999L);

        //2.将item对象装换位es可以接受的itemdoc
        itemDoc itemDoc = BeanUtils.copyBean(item, itemDoc.class);
        itemDoc.setName("你阿里看见啊打开了房间卡拉萨电极法");

        //3.创建文档的请求对象
        IndexRequest request = new IndexRequest("items").id(itemDoc.getId().toString());
        //4.设置请求参数
        String jsonStr = JSONUtil.toJsonStr(itemDoc);
        request.source(jsonStr,XContentType.JSON);
        //5.发送请求
        client.index(request,RequestOptions.DEFAULT);


    }
}