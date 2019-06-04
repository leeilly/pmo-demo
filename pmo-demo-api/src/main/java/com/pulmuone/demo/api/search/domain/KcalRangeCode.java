package com.pulmuone.demo.api.search.domain;

import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.index.query.RangeQueryBuilder;

public enum KcalRangeCode {

    KCAL200("200Kcal", QueryBuilders.rangeQuery("kcal").lte(200) )
    ,KCAL300("300Kcal", QueryBuilders.rangeQuery("kcal").lte(300))
    ,KCAL400( "400Kcal", QueryBuilders.rangeQuery("kcal").lte(400))
    ;


    private String name;
    public String getName(){
        return name;
    }

    private RangeQueryBuilder query;
    public RangeQueryBuilder query() {
        return query;
    }

    KcalRangeCode(String name, RangeQueryBuilder query) {
        this.query = query;
    }

}
