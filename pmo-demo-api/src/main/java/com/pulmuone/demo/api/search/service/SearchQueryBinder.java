package com.pulmuone.demo.api.search.service;

import com.pulmuone.demo.api.search.parser.KoreanChosungParser;
import com.pulmuone.demo.api.search.parser.KoreanJamoParser;
import com.pulmuone.demo.api.search.domain.SortCode;
import com.pulmuone.demo.api.search.dto.SearchRequestDTO;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.Operator;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.springframework.beans.factory.annotation.Autowired;


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

        //선호 식품 필터
        if(StringUtils.isNotBlank(requestDTO.getPreferredFood())) {
            QueryBuilder preferredFoodQuery = QueryBuilders.matchQuery("ingredients", requestDTO.getPreferredFood()).operator(Operator.AND);
            query.must(preferredFoodQuery);
        }

        //알레르기 유발 식품 제외
        if(StringUtils.isNotBlank(requestDTO.getExcludedFoodIngredients())) {
            QueryBuilder excludedFoodIngredientsQuery = QueryBuilders.matchQuery("ingredients", requestDTO.getExcludedFoodIngredients()).operator(Operator.AND);
            query.mustNot(excludedFoodIngredientsQuery);
        }

        //칼로리
        if(requestDTO.getKcal() > 0 ) {

            QueryBuilder caloriesQuery = null;

            if(requestDTO.getKcal() <= 200) {
                caloriesQuery = QueryBuilders.rangeQuery("kcal").gte(0).lte(200);
            }else if(requestDTO.getKcal() > 200 && requestDTO.getKcal() <= 300) {
                caloriesQuery = QueryBuilders.rangeQuery("kcal").gte(200).lte(300);
            }else if(requestDTO.getKcal() > 300 && requestDTO.getKcal() <= 400){
                caloriesQuery = QueryBuilders.rangeQuery("kcal").gte(300).lte(400);
            }else if(requestDTO.getKcal() > 400 && requestDTO.getKcal() <= 500){
                caloriesQuery = QueryBuilders.rangeQuery("kcal").gte(400).lte(500);
            }
            query.must(caloriesQuery);
        }

        //조리시간
        if(requestDTO.getCookingMinute() > 0) {

            QueryBuilder cookingMinuteQuery = null;

            if (requestDTO.getCookingMinute() <= 5) {
                cookingMinuteQuery = QueryBuilders.rangeQuery("cooking_minute").gte(0).lte(5);
            } else if (requestDTO.getCookingMinute() > 5 && requestDTO.getCookingMinute() <= 10) {
                cookingMinuteQuery = QueryBuilders.rangeQuery("cooking_minute").gte(5).lte(10);
            } else if (requestDTO.getCookingMinute() > 10 && requestDTO.getCookingMinute() <= 20) {
                cookingMinuteQuery = QueryBuilders.rangeQuery("cooking_minute").gte(10).lte(20);
            }
            query.must(cookingMinuteQuery);
        }

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


}
