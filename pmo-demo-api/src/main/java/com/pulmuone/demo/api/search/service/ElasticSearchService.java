package com.pulmuone.demo.api.search.service;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.pulmuone.demo.api.search.domain.*;
import com.pulmuone.demo.api.search.parser.KoreanChosungParser;
import com.pulmuone.demo.api.search.parser.KoreanJamoParser;
import com.pulmuone.demo.api.search.mapper.SearchIndexMapper;
import lombok.extern.slf4j.Slf4j;
import org.elasticsearch.action.admin.indices.analyze.AnalyzeRequest;
import org.elasticsearch.action.admin.indices.analyze.AnalyzeResponse;
import org.elasticsearch.action.admin.indices.analyze.DetailAnalyzeResponse;
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
    private static final String BOOST_INDEX_ALIAS_NAME = "boost";
    private static final String AC_INDEX_ALIAS_NAME = "product_ac";

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

    public void createAutoCompleteIndex(String indexName) {
        try {
            KoreanJamoParser jamoParser = new KoreanJamoParser();
            KoreanChosungParser chosungParser = new KoreanChosungParser();
            List<ProductAutoCompleteDomain> list = searchIndexMapper.selectAutoCompleteIndexTargetList();

            if (AC_INDEX_ALIAS_NAME.equals(indexName)) {
                list.forEach(l -> {
                    l.setNameJamo(jamoParser.parse(l.getName()));
                    l.setNameChosung(chosungParser.parse(l.getName()));
                });
            } else {

                list.forEach(l -> {
                    l.setNameJamo(jamoParser.parse(l.getName()));
                    l.setNameChosung(chosungParser.parse(l.getName()));
                });
            }


            BulkRequest request = new BulkRequest();
            list.stream().forEach(data -> request.add(createIndexRequest(indexName, MAPPER.convertValue((Object) data, Map.class))));
            BulkResponse response = restHighLevelClient.bulk(request, RequestOptions.DEFAULT);

            log.info("bulk insert done. total: {}", response.getItems().length);
        }catch (Exception e){
            e.printStackTrace();
        }

    }


    public SearchResult searchAutoComplete(SearchSourceBuilder query, Class<T> valueType) throws IOException {
        SearchRequest searchRequest = new SearchRequest();
        searchRequest.indices(AC_INDEX_ALIAS_NAME);
        searchRequest.source(query);
        SearchResponse searchResponse = restHighLevelClient.search(searchRequest, RequestOptions.DEFAULT);

        SearchResult result = new SearchResult();
        result.setCount(searchResponse.getHits().getTotalHits().value);
        result.setSearchResult(convertResultList(searchResponse, valueType));

        return result;
    }

    public List<AnalyzeResultDomain> analyze(String keyword, String analyzerName) {

        List list = new ArrayList();

        AnalyzeRequest analyzeRequest = new AnalyzeRequest();
        analyzeRequest.index("product");
        analyzeRequest.text(keyword);
        analyzeRequest.analyzer(analyzerName);
        try {
            AnalyzeResponse response = restHighLevelClient.indices().analyze(analyzeRequest, RequestOptions.DEFAULT);
            List<AnalyzeResponse.AnalyzeToken> tokens = response.getTokens();
            DetailAnalyzeResponse detail = response.detail();

            tokens.stream().forEach(t -> {
                    AnalyzeResultDomain analyzeResultDomain = new AnalyzeResultDomain();
                    analyzeResultDomain.setTerm(t.getTerm());
                    analyzeResultDomain.setType(t.getType());
                    list.add(analyzeResultDomain);
                }
            );

        } catch (IOException e) {
            e.printStackTrace();
        }

        return list;

    }

    public void createBoostIndex(String indexName) throws IOException {

        List<BoostDocumentDomain> list = searchIndexMapper.selectBoostIndexTargetList();

        BulkRequest request = new BulkRequest();
        list.stream().forEach(data -> request.add(createIndexRequest(indexName, MAPPER.convertValue((Object) data, Map.class))));
        BulkResponse response = restHighLevelClient.bulk(request, RequestOptions.DEFAULT);

        log.info("boost insert done. total: {}", response.getItems().length);
    }

    public SearchResult<T> boostCategorySearch(SearchSourceBuilder query, Class<T> valueType) throws Exception {
        SearchRequest searchRequest = new SearchRequest();
        searchRequest.indices(BOOST_INDEX_ALIAS_NAME);
        searchRequest.source(query);
        SearchResponse searchResponse = restHighLevelClient.search(searchRequest, RequestOptions.DEFAULT);

        log.info("searchResponse.getHits(): {}", searchResponse.getHits()) ;

        SearchResult result = new SearchResult();
        result.setCount(searchResponse.getHits().getTotalHits().value);
        result.setSearchResult(convertResultList(searchResponse, valueType));

        return result;
    }

}
