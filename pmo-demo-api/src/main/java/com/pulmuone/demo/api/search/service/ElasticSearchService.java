package com.pulmuone.demo.api.search.service;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.pulmuone.demo.api.search.domain.ProductAutoCompleteDomain;
import com.pulmuone.demo.api.search.domain.ProductDocumentDomain;
import com.pulmuone.demo.api.search.domain.SearchResult;
import com.pulmuone.demo.api.search.mapper.SearchIndexMapper;
import lombok.extern.slf4j.Slf4j;
import org.elasticsearch.action.bulk.BulkRequest;
import org.elasticsearch.action.bulk.BulkResponse;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Slf4j
@Service
public class ElasticSearchService<T> {

    @Autowired
    RestHighLevelClient restHighLevelClient;

    @Autowired
    SearchIndexMapper searchIndexMapper;


    private static final String INDEX_ALIAS_NAME = "product";
    private static final ObjectMapper MAPPER = new ObjectMapper().setPropertyNamingStrategy(PropertyNamingStrategy.SNAKE_CASE).configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

    public SearchResult<T> search(SearchSourceBuilder query, Class<T> valueType) throws Exception {
        SearchRequest searchRequest = new SearchRequest();
        searchRequest.indices(INDEX_ALIAS_NAME);
        searchRequest.source(query);
        SearchResponse searchResponse = restHighLevelClient.search(searchRequest, RequestOptions.DEFAULT);

        log.info("searchResponse.getHits(): {}", searchResponse.getHits()) ;

        SearchResult result = new SearchResult();
        result.setCount(searchResponse.getHits().getTotalHits().value);
        result.setSearchResult(convertResultList(searchResponse, valueType));

        return result;
    }


    public static <T> List<T> convertResultList(SearchResponse response, Class<T> valueType) throws IllegalArgumentException, IOException {
        int initialSize = (int) (response.getHits().getTotalHits().value * 1.3);
        List<T> list = new ArrayList<>(initialSize);

        for(SearchHit hit : response.getHits()){
            list.add(convertResult(hit, valueType));
        }

        return list;
    }

    public static <T> T convertResult(SearchHit hit, Class<T> valueType) throws IOException, IllegalArgumentException{
        log.info("hit.getSourceAsString(): {} ", hit.getSourceAsString());
        T result = MAPPER.readValue(hit.getSourceAsString(), valueType);

        log.info("result.toString(): {}", result.toString());;
        return result;
    }


    //fixme: alias switching
    public void createIndex(String indexName) throws IOException {

        List<ProductDocumentDomain> list = searchIndexMapper.selectIndexTargetList();

        BulkRequest request = new BulkRequest();
        list.stream().forEach(data -> request.add(createIndexRequest(indexName, MAPPER.convertValue((Object) data, Map.class))));
        BulkResponse response = restHighLevelClient.bulk(request, RequestOptions.DEFAULT);

        log.info("bulk insert done. total: {}", response.getItems().length);

    }

    public IndexRequest createIndexRequest(String indexName, Map<String, Object> data) {
        IndexRequest request = new IndexRequest(indexName);
        request.source(data);
        return request;
    }

    public void createAutoCompleteIndex(String indexName) throws IOException {

        List<ProductAutoCompleteDomain> list = searchIndexMapper.selectAutoCompleteIndexTargetList();

        BulkRequest request = new BulkRequest();
        list.stream().forEach(data -> request.add(createIndexRequest(indexName, MAPPER.convertValue((Object) data, Map.class))));
        BulkResponse response = restHighLevelClient.bulk(request, RequestOptions.DEFAULT);

        log.info("bulk insert done. total: {}", response.getItems().length);

    }


}
