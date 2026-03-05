package com.hmall.item.es;

import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.hmall.common.utils.BeanUtils;
import com.hmall.common.utils.CollUtils;
import com.hmall.item.domain.po.Item;
import com.hmall.item.domain.po.itemDoc;
import com.hmall.item.service.IItemService;
import org.apache.http.HttpHost;
import org.elasticsearch.action.bulk.BulkRequest;
import org.elasticsearch.action.get.GetRequest;
import org.elasticsearch.action.get.GetResponse;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.client.indices.GetIndexRequest;
import org.elasticsearch.common.xcontent.XContentType;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.io.IOException;
import java.util.List;

@SpringBootTest
public class insertTest {
    @Autowired
    private  IItemService service;



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

   //批量导入商品到es中
    @Test
    public void importItem() throws IOException {
        int page=1;
        int page_size=1000;
        while (true){
            //1.根据页号，页大小，每次查询1000条数据
            System.out.println("------------------正在导入第"+page+"条数据");
            Page<Item> page1 = service.lambdaQuery().eq(Item::getStatus, 1).
                    page(new Page<>(page, page_size));
            List<Item> records = page1.getRecords();
            if(CollUtils.isEmpty(records)){
                break;}
            //2.将每一条数据转换为itemDoc,目的，不是所有实体字段都需要导入的es中，比如创建时间
            List<itemDoc> itemDocs = BeanUtils.copyList(records, itemDoc.class);
            //3.将1000条数据分别设置request并加入bulkrequest
            BulkRequest bulkRequest = new BulkRequest("items");
            for(itemDoc itemDoc:itemDocs){
                IndexRequest request = new IndexRequest("items").id(itemDoc.getId().toString());
                String jsonStr = JSONUtil.toJsonStr(itemDoc);
                request.source(jsonStr,XContentType.JSON);
                bulkRequest.add(request);
            }
            //4.提交bulkrequest。一次发送一千条数据
            client.bulk(bulkRequest,RequestOptions.DEFAULT);
            System.out.println("----------------第"+page+"条数据导入完成");
            //5.继续分页查询，直到没有数据为止
            page++;

        }
    }
}