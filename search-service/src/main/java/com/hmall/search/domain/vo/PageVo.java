package com.hmall.search.domain.vo;


import com.hmall.common.utils.BeanUtils;
import com.hmall.common.utils.CollUtils;
import com.hmall.common.utils.Convert;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PageVo<T> {
    protected Long total;
    protected Long pages;
    protected List<T> list;

    public static <T> PageVo<T> empty(Long total, Long pages) {
        return new PageVo<>(total, pages, CollUtils.emptyList());
    }

    public static <T> PageVo<T> of(Long total, Long pages,  List<T> list) {
        return new PageVo<>(total,pages, list);
    }


}
