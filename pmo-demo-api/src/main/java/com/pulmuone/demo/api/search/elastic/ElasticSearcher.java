package com.pulmuone.demo.api.search.elastic;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.pulmuone.demo.api.search.domain.SearchResult;
import lombok.extern.slf4j.Slf4j;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Component
public class ElasticSearcher<T> {
//
//    private static final String INDEX_ALIAS_NAME = "pmo_product";
//    private static final ObjectMapper MAPPER = new ObjectMapper().setPropertyNamingStrategy(PropertyNamingStrategy.SNAKE_CASE);
//
//
//    @Autowired
//    RestHighLevelClient restHighLevelClient;
//
//
//    public SearchResult<T> search(SearchSourceBuilder query, Class<T> valueType) throws Exception {
//
//        SearchRequest searchRequest = new SearchRequest();
//        searchRequest.indices(INDEX_ALIAS_NAME);
//        searchRequest.source(query);
//        SearchResponse searchResponse = restHighLevelClient.search(searchRequest, RequestOptions.DEFAULT);
//
//        SearchResult result = new SearchResult();
//        result.setCount(searchResponse.getHits().getTotalHits().value);
//        result.setSearchResult(convertResultList(searchResponse, valueType));
//
//        return result;
//    }
//
//
//    public static <T> List<T> convertResultList(SearchResponse response, Class<T> valueType) throws IllegalArgumentException, IOException {
//        int initialSize = (int) (response.getHits().getTotalHits().value * 1.3);
//        List<T> list = new ArrayList<>(initialSize);
//
//        for(SearchHit hit : response.getHits()){
//            list.add(convertResult(hit, valueType));
//        }
//
//        return list;
//    }
//
//    public static <T> T convertResult(SearchHit hit, Class<T> valueType) throws IOException, IllegalArgumentException{
//        T result = MAPPER.readValue(hit.getSourceAsString().replaceAll("@",""), valueType);
//        return result;
//    }



}
