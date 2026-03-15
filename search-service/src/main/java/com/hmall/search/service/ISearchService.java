package com.hmall.search.service;

import com.hmall.search.domain.po.itemDoc;
import com.hmall.search.domain.query.ItemPageQuery;
import com.hmall.search.domain.vo.PageVo;

public interface ISearchService {
    void saveItemByid(Long itemId);

    void deleteItemByid(Long itemId);

    PageVo<itemDoc> search(ItemPageQuery query);
}
