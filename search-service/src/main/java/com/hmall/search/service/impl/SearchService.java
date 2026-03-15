package com.hmall.search.service.impl;

import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSON;
import cn.hutool.json.JSONUtil;
import com.heima.hmall.client.ICartService;
import com.heima.hmall.client.item_client;
import com.heima.hmall.dto.ItemDTO;
import com.hmall.common.utils.BeanUtils;
import com.hmall.search.domain.po.itemDoc;
import com.hmall.search.domain.query.ItemPageQuery;
import com.hmall.search.domain.vo.PageVo;
import com.hmall.search.service.ISearchService;
import org.apache.http.HttpHost;
import org.elasticsearch.action.admin.indices.delete.DeleteIndexRequest;
import org.elasticsearch.action.delete.DeleteRequest;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.common.xcontent.XContentType;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.SearchHits;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightField;
import org.elasticsearch.search.sort.SortOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Map;

@Service
public class SearchService implements ISearchService {

    @Autowired
    private RestHighLevelClient client;
    private static final String index_name="items";

    @Autowired
    private item_client itemClient;

    @Override
    public void saveItemByid(Long itemId)  {
        //根据商品id查询商品并转换为json
        ItemDTO itemDTO = itemClient.queryItemById(itemId);
        itemDoc itemDoc = BeanUtils.copyBean(itemDTO, itemDoc.class);
        String jsonStr = JSONUtil.toJsonStr(itemDoc);

        client = new RestHighLevelClient(RestClient.builder(HttpHost.create("http://192.168.133.126:9200")));
         //创建请求对象
        IndexRequest request = new IndexRequest(index_name).id(itemId.toString());
        //设置请求参数
        request.source(jsonStr, XContentType.JSON);


        //发送请求
        try {
            client.index(request, RequestOptions.DEFAULT);
        } catch (IOException e) {
            throw new RuntimeException("保存商品到es失败");
        }

    }

    @Override
    public void deleteItemByid(Long itemId) {
        client = new RestHighLevelClient(RestClient.builder(HttpHost.create("http://192.168.133.126:9200")));
        DeleteRequest request = new DeleteRequest(index_name).id(itemId.toString());
        try {
            client.delete(request,RequestOptions.DEFAULT);
        } catch (IOException e) {
            throw new RuntimeException("从es中删除下架的商品失败");
        }


    }

    @Override
    public PageVo<itemDoc> search(ItemPageQuery query) {
        try {
            //1.创建搜索请求对象
            SearchRequest request = new SearchRequest(index_name);
            BoolQueryBuilder booledQuery = QueryBuilders.boolQuery();
            //判断是否需要高亮
            boolean isHighlight=false;
            //2.设置搜索请求参数（关键字，过滤，分页，排序）
            if(StrUtil.isNotBlank(query.getKey())){
               booledQuery.must(QueryBuilders.matchQuery("name",query.getKey()));
               isHighlight=true;
            }
            //根据分类条件过滤
            if(StrUtil.isNotBlank(query.getCategory())){
                booledQuery.filter(QueryBuilders.termQuery("category",query.getCategory()));
            }
            //根据品牌条件过滤
            if(StrUtil.isNotBlank(query.getBrand())){
                booledQuery.filter(QueryBuilders.termQuery("brand",query.getBrand()));
            }
            //根据价格条件过滤
            if(query.getMinPrice()!=null){
                booledQuery.filter(QueryBuilders.rangeQuery("price").gte(query.getMinPrice()));
            }
            if(query.getMaxPrice()!=null){
                booledQuery.filter(QueryBuilders.rangeQuery("price").lte(query.getMaxPrice()));
            }
            //排序，如果用户传入了排序方式就按照用户的方式，如果没有就按照更新时间排序
            if(StrUtil.isNotBlank(query.getSortBy())){
                request.source().sort(query.getSortBy(),query.getIsAsc()?SortOrder.ASC:SortOrder.DESC);

            }else {
                request.source().sort("updateTime", SortOrder.DESC);
            }
            //分页
            int  pageNo = query.getPageNo()==null?1: query.getPageNo();
            int pageSize = query.getPageSize() == null ? 20 : query.getPageSize();
            request.source().from((pageNo-1)*pageSize).size(pageSize);
            //设置高亮
            request.source().highlighter(SearchSourceBuilder.highlight()
                            .field("name").preTags("<font color='red' >").postTags("</font>"));



             request.source().query(booledQuery);
            //3.发送搜索请求
            SearchResponse response = client.search(request, RequestOptions.DEFAULT);

            //4.处理响应结果
            SearchHits hits = response.getHits();
            PageVo<itemDoc> pageVo = new PageVo<>();
            //4.1获取总记录数
            long total = hits.getTotalHits().value;
            pageVo.setTotal(total);
            //4.2获取总页数,总页数需要向上取整(总记录数加每一页的数量减一)除以每一页的记录数
           // int totalPages=(int)(total+pageSize-1)/pageSize;
            long totalPages=(long) ((total%pageSize==0)?(total/pageSize):(total/pageSize+1));
            pageVo.setPages(totalPages);
             //4.3获取总列表
            ArrayList<itemDoc> itemDocs = new ArrayList<>(pageSize);

            SearchHit[] hits1 = hits.getHits();
            if(hits1!=null&&hits1.length>0){
                for (SearchHit hit : hits) {
                    String itemJson = hit.getSourceAsString();
                    itemDoc itemDoc = JSONUtil.toBean(itemJson, itemDoc.class);
                    //处理高亮
                    if(isHighlight){
                        Map<String, HighlightField> highlightFields = hit.getHighlightFields();
                        if(highlightFields!=null&&highlightFields.containsKey("name")){
                            HighlightField name = highlightFields.get("name");
                            String heighlightName = name.getFragments()[0].string();
                            itemDoc.setName(heighlightName);
                        }
                    }
                    itemDocs.add(itemDoc);

                }
            }

            pageVo.setList(itemDocs);
            return pageVo;
        } catch (IOException e) {
            throw new RuntimeException("搜索es中的商品失败"+e.getMessage());
        }
    }
}
