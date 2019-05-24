package com.pulmuone.demo.api.search.elastic;

import com.pulmuone.demo.api.search.domain.SortCode;
import com.pulmuone.demo.api.search.dto.SearchRequestDTO;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.Operator;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.builder.SearchSourceBuilder;


@Slf4j
public class SearchQueryBinder {
    private static final int DEFAULT_SIZE = 30;
    private static final int DEFAULT_FROM = 0;

    public SearchSourceBuilder query(SearchRequestDTO requestDTO) throws Exception {

        if(StringUtils.isBlank(requestDTO.getKeyword())) {
            throw new Exception("[Search Exception] Search keyword must not be null.");
        }

        //fixme: 관리필요함. 따로 뺄 것.
        String[] keywordIndexFields = new String[]{
                "name"
        };

        BoolQueryBuilder query = QueryBuilders.boolQuery();

        QueryBuilder multiKeywordQuery = QueryBuilders.multiMatchQuery(requestDTO.getKeyword(),keywordIndexFields).operator(Operator.AND);
        query.must(multiKeywordQuery);

        SearchSourceBuilder sourceQuery = sourceQuery(requestDTO);
        sourceQuery.query(query);

        return sourceQuery;
    }

    public SearchSourceBuilder sourceQuery(SearchRequestDTO requestDTO){
        SearchSourceBuilder sourceQuery = new SearchSourceBuilder();

        //Sort
        SortCode sortCode = requestDTO.getSortCode();
        sourceQuery.sort(sortCode.field(), sortCode.order());
        sourceQuery.trackScores(true);

        //Paging
        int size = requestDTO.getLimit();
        int from = (requestDTO.getPage() - 1) * size;
        if( from < 0 ){
            from = DEFAULT_FROM;
            size = DEFAULT_SIZE;
        }
        sourceQuery.from(from);
        sourceQuery.size(size);

        return sourceQuery;
    }



}
