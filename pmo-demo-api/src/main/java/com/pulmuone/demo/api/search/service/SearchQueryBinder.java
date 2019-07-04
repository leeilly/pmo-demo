package com.pulmuone.demo.api.search.service;

import com.pulmuone.demo.api.search.domain.CookingMinuteRangeCode;
import com.pulmuone.demo.api.search.domain.KcalRangeCode;
import com.pulmuone.demo.api.search.domain.SortCode;
import com.pulmuone.demo.api.search.dto.SearchRequestDTO;
import com.pulmuone.demo.api.search.parser.KoreanJamoParser;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.Operator;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightBuilder;
import org.elasticsearch.search.sort.SortOrder;
import org.springframework.beans.factory.annotation.Autowired;

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

        KoreanJamoParser parser1 = new KoreanJamoParser();
        BoolQueryBuilder query = QueryBuilders.boolQuery();
        QueryBuilder prefixQuery = QueryBuilders.prefixQuery("name", requestDTO.getKeyword());
        query.should(prefixQuery);

        QueryBuilder termQueryNgram= QueryBuilders.termQuery("name_ngram", requestDTO.getKeyword());
        QueryBuilder termQueryEdge = QueryBuilders.termQuery("name_ngram_edge", requestDTO.getKeyword());
        QueryBuilder termQueryEdgeBack = QueryBuilders.termQuery("name_ngram_edge_back", requestDTO.getKeyword());
        QueryBuilder termQueryJamo = QueryBuilders.termQuery("name_jamo", parser1.parse(requestDTO.getKeyword()));

        query.should(termQueryNgram);
        query.should(termQueryEdge);
        query.should(termQueryJamo);

        if("any".equals(requestDTO.getMatchingType())){
            query.should(termQueryEdgeBack);
            query.minimumShouldMatch(1);
        }else if("prefix".equals(requestDTO.getMatchingType())){
            query.minimumShouldMatch(2);
        }

        SearchSourceBuilder sourceQuery = autoCompleteSourceQuery(requestDTO);
        sourceQuery.query(query);

        log.info("query: {}", sourceQuery.toString());

        return sourceQuery;

    }

    public SearchSourceBuilder query(SearchRequestDTO requestDTO) throws Exception {

        if(StringUtils.isBlank(requestDTO.getKeyword())) {
            throw new Exception("[Search Exception] Search keyword must not be null.");
        }

        //fixme: 관리필요함.
        String[] keywordIndexFields = new String[]{
                "name"
                ,"name_ngram"
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

        log.info("query: {}", sourceQuery.toString());

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
        HighlightBuilder highlightBuilder = new HighlightBuilder();

        HighlightBuilder.Field highlightName = new HighlightBuilder.Field("name_ngram");
        HighlightBuilder.Field highlightNameNgramEdge = new HighlightBuilder.Field("name_ngram_edge");
        highlightName.highlighterType("unified");
        highlightBuilder.field(highlightName);
        highlightBuilder.field(highlightNameNgramEdge);
        sourceQuery.highlighter(highlightBuilder);

        //Sort
        //SortCode sortCode = SortCode.SCORE;
        //sourceQuery.sort(sortCode.field(), sortCode.order());
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

        return sourceQuery;



    }
}
