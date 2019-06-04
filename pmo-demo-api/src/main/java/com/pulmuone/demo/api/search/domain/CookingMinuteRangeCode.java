package com.pulmuone.demo.api.search.domain;

import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.index.query.RangeQueryBuilder;

public enum CookingMinuteRangeCode {

    M5("5분이내", QueryBuilders.rangeQuery("cooking_minute").lte(5) )
    ,M10("10분이내", QueryBuilders.rangeQuery("cooking_minute").lte(10))
    ,M20( "20분이내", QueryBuilders.rangeQuery("cooking_minute").lte(20))
    ;


    private String name;
    public String getName(){
        return name;
    }

    private RangeQueryBuilder query;
    public RangeQueryBuilder query() {
        return query;
    }

    CookingMinuteRangeCode(String name, RangeQueryBuilder query) {
        this.query = query;
    }

}
