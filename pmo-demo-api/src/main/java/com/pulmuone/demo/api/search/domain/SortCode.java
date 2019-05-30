package com.pulmuone.demo.api.search.domain;

import org.elasticsearch.search.sort.SortOrder;

/**
 * 상품 검색 정렬코드
 */
public enum SortCode {

    NEW("create_ymdt", SortOrder.DESC)
    ,SCORE("score", SortOrder.DESC)
    ;

    private final String field;
    private final SortOrder order;

    public String field() {
        return field;
    }
    public SortOrder order() {
        return order;
    }

    SortCode(String field, SortOrder order) {
        this.field = field;
        this.order = order;
    }

}