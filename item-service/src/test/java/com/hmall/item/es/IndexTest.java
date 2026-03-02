package com.hmall.item.es;

import org.apache.http.HttpHost;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestHighLevelClient;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class IndexTest {

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
}