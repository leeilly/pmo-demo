package com.pulmuone.demo.api.search.service;

import com.pulmuone.demo.api.search.domain.CookingMinuteRangeCode;
import com.pulmuone.demo.api.search.domain.KcalRangeCode;
import com.pulmuone.demo.api.search.domain.SortCode;
import com.pulmuone.demo.api.search.dto.SearchRequestDTO;
import com.pulmuone.demo.api.search.parser.KoreanChosungParser;
import com.pulmuone.demo.api.search.parser.KoreanJamoParser;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.Operator;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.index.query.functionscore.ScoreFunctionBuilder;
import org.elasticsearch.index.query.functionscore.ScoreFunctionBuilders;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.elasticsearch.search.sort.SortOrder;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Iterator;

import static org.elasticsearch.index.query.QueryBuilders.matchQuery;



@Slf4j
public class SearchQueryBinder {
    private static final int DEFAULT_SIZE = 30;
    private static final int DEFAULT_FROM = 0;

    @Autowired
    RestHighLevelClient restHighLevelClient;

    public SearchSourceBuilder autoCompleteQuery(SearchRequestDTO requestDTO) throws Exception {

        if(StringUtils.isBlank(requestDTO.getKeyword())) {
            throw new Exception("[Search Exception] Search keyword must not be null.");
        }

        /**
         * POST /auto4/_search
         * {
         *   "query": {
         *     "bool": {
         *       "should": [
         *         {
         *           "prefix": {
         *             "name": "생"
         *           }
         *         },
         *         {
         *           "term": {
         *             "name": "생"
         *           }
         *         },
         *         {
         *           "term": {
         *             "name_ngram": "생"
         *           }
         *         },
         *         {
         *           "term": {
         *             "name_ngram_edge": "생"
         *           }
         *         },
         *         {
         *           "term": {
         *             "name_ngram_edge_back": "생"
         *           }
         *         },
         *         {
         *           "term": {
         *             "name_jamo": "ㅅㅐㅇ"
         *           }
         *         }
         *       ],
         *       "minimum_should_match": 1
         *     }
         *   },
         *    "highlight" : {
         *         "fields" : {
         *             "name" : {}
         *         }
         *     }
         * }
         */

        KoreanJamoParser parser1 = new KoreanJamoParser();
        log.info("jamo parser result: {}", parser1.parse(requestDTO.getKeyword()));

        KoreanChosungParser parser2 = new KoreanChosungParser();
        log.info("chosung parser result: {}", parser2.parse(requestDTO.getKeyword()));

        BoolQueryBuilder query = QueryBuilders.boolQuery();
        QueryBuilder prefixQuery = QueryBuilders.prefixQuery("name", requestDTO.getKeyword());
        query.should(prefixQuery);

        QueryBuilder termQuery1 = QueryBuilders.termQuery("name_ngram", requestDTO.getKeyword());
        QueryBuilder termQuery2 = QueryBuilders.termQuery("name_ngram_edge", requestDTO.getKeyword());
        QueryBuilder termQuery3 = QueryBuilders.termQuery("name_ngram_edge_back", requestDTO.getKeyword());
        //QueryBuilder termQuery4 = QueryBuilders.termQuery("name_chosung", parser2.parse(requestDTO.getKeyword()));
        QueryBuilder termQuery5 = QueryBuilders.termQuery("name_jamo", parser1.parse(requestDTO.getKeyword()));

        query.should(termQuery1);
        query.should(termQuery2);
        query.should(termQuery3);
        //query.should(termQuery4);
        query.should(termQuery5);
        query.minimumShouldMatch(1);

        SearchSourceBuilder sourceQuery = autoCompleteSourceQuery(requestDTO);
        sourceQuery.query(query);

        return sourceQuery;

    }

    public SearchSourceBuilder query(SearchRequestDTO requestDTO) throws Exception {

        if(StringUtils.isBlank(requestDTO.getKeyword())) {
            throw new Exception("[Search Exception] Search keyword must not be null.");
        }




        //fixme: 관리필요함.
        String[] keywordIndexFields = new String[]{
                "name"
                ,"search_keyword"
        };

        BoolQueryBuilder query = QueryBuilders.boolQuery();

        QueryBuilder multiKeywordQuery = QueryBuilders.multiMatchQuery(requestDTO.getKeyword(),keywordIndexFields).operator(Operator.AND);
        query.must(multiKeywordQuery);

        //카테고리 부스팅
        for( Integer categoreSeq : requestDTO.getCategoryBoostingMap().keySet() ){
            QueryBuilder boostQuery = QueryBuilders.matchQuery("category_seq", categoreSeq ).operator(Operator.AND).boost(requestDTO.getCategoryBoostingMap().get(categoreSeq));
            query.should(boostQuery);
        }

        //선호 식품 필터
        if(StringUtils.isNotBlank(requestDTO.getPreferredFood())) {
            QueryBuilder preferredFoodQuery = matchQuery("ingredients", requestDTO.getPreferredFood()).operator(Operator.AND);
            query.must(preferredFoodQuery);
        }

        //알레르기 유발 식품 제외
        if(StringUtils.isNotBlank(requestDTO.getExcludedFoodIngredients())) {
            QueryBuilder excludedFoodIngredientsQuery = matchQuery("ingredients", requestDTO.getExcludedFoodIngredients()).operator(Operator.OR);
            query.mustNot(excludedFoodIngredientsQuery);
        }

        //kcal
        log.info("KcalRangeCode: {}", requestDTO.getKcalRangeCode());
        if( requestDTO.getKcalRangeCode() != null ) {
            QueryBuilder caloriesQuery = null;
            if (KcalRangeCode.KCAL200.equals(requestDTO.getKcalRangeCode())) {
                caloriesQuery = KcalRangeCode.KCAL200.query();
            } else if (KcalRangeCode.KCAL300.equals(requestDTO.getKcalRangeCode())) {
                caloriesQuery = KcalRangeCode.KCAL300.query();
            } else if (KcalRangeCode.KCAL400.equals(requestDTO.getKcalRangeCode())) {
                caloriesQuery = KcalRangeCode.KCAL400.query();
            }
            query.must(caloriesQuery);
        }


        //조리시간
        log.info("cookingMinCode: {}", requestDTO.getCookingMinuteRangeCode());
        if( requestDTO.getCookingMinuteRangeCode() != null ) {
            QueryBuilder cookingMinuteQuery = null;
            if (CookingMinuteRangeCode.M5.equals(requestDTO.getCookingMinuteRangeCode())) {
                cookingMinuteQuery = CookingMinuteRangeCode.M5.query();
            } else if (CookingMinuteRangeCode.M10.equals(requestDTO.getCookingMinuteRangeCode())) {
                cookingMinuteQuery = CookingMinuteRangeCode.M20.query();
            } else if (CookingMinuteRangeCode.M20.equals(requestDTO.getCookingMinuteRangeCode())) {
                cookingMinuteQuery = CookingMinuteRangeCode.M20.query();
            }
            query.must(cookingMinuteQuery);
        }

        SearchSourceBuilder sourceQuery = sourceQuery(requestDTO);
        sourceQuery.query(query);

        log.info("source query: {}", sourceQuery.toString());

        return sourceQuery;
    }

    public SearchSourceBuilder sourceQuery(SearchRequestDTO requestDTO){
        SearchSourceBuilder sourceQuery = new SearchSourceBuilder();

        //Sort
        SortCode sortCode = requestDTO.getSortCode();
        sourceQuery.sort("_score", SortOrder.DESC);
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


    public SearchSourceBuilder autoCompleteSourceQuery(SearchRequestDTO requestDTO){
        SearchSourceBuilder sourceQuery = new SearchSourceBuilder();

        //Sort
        SortCode sortCode = SortCode.SCORE;
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


    public SearchSourceBuilder boostCategorySearchQuery(SearchRequestDTO requestDTO) {
        BoolQueryBuilder query = QueryBuilders.boolQuery();
        QueryBuilder keywordQuery = matchQuery("keyword", requestDTO.getKeyword()).operator(Operator.AND);
        query.must(keywordQuery);

        SearchSourceBuilder sourceQuery = new SearchSourceBuilder();
        sourceQuery.query(query);

        log.info("boost query: {}", query);

        return sourceQuery;



    }
}
