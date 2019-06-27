package com.pulmuone.demo.api.search.service;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.pulmuone.demo.api.search.domain.AnalyzeResultDomain;
import com.pulmuone.demo.api.search.domain.ProductDocumentDomain;
import com.pulmuone.demo.api.search.domain.SearchResult;
import com.pulmuone.demo.api.search.mapper.SearchIndexMapper;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.elasticsearch.action.admin.indices.alias.IndicesAliasesRequest;
import org.elasticsearch.action.admin.indices.alias.get.GetAliasesRequest;
import org.elasticsearch.action.admin.indices.analyze.AnalyzeRequest;
import org.elasticsearch.action.admin.indices.analyze.AnalyzeResponse;
import org.elasticsearch.action.admin.indices.analyze.DetailAnalyzeResponse;
import org.elasticsearch.action.bulk.BulkRequest;
import org.elasticsearch.action.bulk.BulkResponse;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.action.support.master.AcknowledgedResponse;
import org.elasticsearch.client.GetAliasesResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

@Slf4j
@Service
public class ElasticSearchService<T> {

    @Autowired
    RestHighLevelClient restHighLevelClient;

    @Autowired
    SearchIndexMapper searchIndexMapper;


    private static final String PRODUCT_INDEX_ALIAS = "product";
    private static final String CATEGORY_BOOST_INDEX_ALIAS= "category_boost";
    private static final String AC_INDEX_ALIAS = "ac";

    private static final ObjectMapper MAPPER = new ObjectMapper().setPropertyNamingStrategy(PropertyNamingStrategy.SNAKE_CASE).configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);



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

    public SearchResult<T> search(SearchSourceBuilder query, Class<T> valueType) throws Exception {

        SearchRequest searchRequest = new SearchRequest();
        searchRequest.indices(PRODUCT_INDEX_ALIAS);
        searchRequest.source(query);
        SearchResponse searchResponse = restHighLevelClient.search(searchRequest, RequestOptions.DEFAULT);

        log.info("searchResponse.getHits(): {}", searchResponse.getHits()) ;

        SearchResult result = new SearchResult();
        result.setCount(searchResponse.getHits().getTotalHits().value);
        result.setSearchResult(convertResultList(searchResponse, valueType));

        return result;
    }


    public SearchResult searchAutoComplete(SearchSourceBuilder query, Class<T> valueType) throws IOException {
        SearchRequest searchRequest = new SearchRequest();
        searchRequest.indices(AC_INDEX_ALIAS);
        searchRequest.source(query);
        SearchResponse searchResponse = restHighLevelClient.search(searchRequest, RequestOptions.DEFAULT);

        SearchResult result = new SearchResult();
        result.setCount(searchResponse.getHits().getTotalHits().value);
        result.setSearchResult(convertResultList(searchResponse, valueType));

        return result;
    }

    public SearchResult<T> boostCategorySearch(SearchSourceBuilder query, Class<T> valueType) throws Exception {
        SearchRequest searchRequest = new SearchRequest();
        searchRequest.indices(CATEGORY_BOOST_INDEX_ALIAS);
        searchRequest.source(query);
        SearchResponse searchResponse = restHighLevelClient.search(searchRequest, RequestOptions.DEFAULT);

        log.info("searchResponse.getHits(): {}", searchResponse.getHits()) ;

        SearchResult result = new SearchResult();
        result.setCount(searchResponse.getHits().getTotalHits().value);
        result.setSearchResult(convertResultList(searchResponse, valueType));

        return result;
    }

    /**
     * alias switching.
     * alias: category_boost_dic
     *
     * 0) category_boost_dic_20190611 -------> category_boost
     * 1) create index category_boost_dic_20190612
     * 2) category_boost_dic_20190611 ---X---> category_boost
     * 3) category_boost_dic_20190612 -------> category_boost
     *
     */
    public boolean addAlias(String index, String alias) throws IOException {

        IndicesAliasesRequest request = new IndicesAliasesRequest();
        IndicesAliasesRequest.AliasActions aliasAction =
                new IndicesAliasesRequest.AliasActions(IndicesAliasesRequest.AliasActions.Type.ADD)
                        .index(index)
                        .alias(alias);  //category_boost_dic
        request.addAliasAction(aliasAction);

        AcknowledgedResponse indicesAliasesResponse = restHighLevelClient.indices().updateAliases(request, RequestOptions.DEFAULT);
        boolean acknowledged = indicesAliasesResponse.isAcknowledged();
        log.info("addAlias - alias: {}, index: {}, acknowledged: {}", alias, index,  acknowledged);
        return acknowledged;
    }

    public boolean removeAlias(String index, String alias) throws IOException {

        IndicesAliasesRequest request = new IndicesAliasesRequest();
        IndicesAliasesRequest.AliasActions aliasAction =
                new IndicesAliasesRequest.AliasActions(IndicesAliasesRequest.AliasActions.Type.REMOVE)
                        .index(index)
                        .alias(alias);  //category_boost_dic
        request.addAliasAction(aliasAction);

        AcknowledgedResponse indicesAliasesResponse = restHighLevelClient.indices().updateAliases(request, RequestOptions.DEFAULT);
        boolean acknowledged = indicesAliasesResponse.isAcknowledged();
        log.info("removeAlias - alias: {}, index: {}, acknowledged: {}", alias, index,  acknowledged);
        return acknowledged;
    }

    public boolean existAlias(String alias) throws IOException {

        GetAliasesRequest requestWithAlias = new GetAliasesRequest(alias);
        boolean exists = restHighLevelClient.indices().existsAlias(requestWithAlias, RequestOptions.DEFAULT);
        log.info("existAlias - alias: {}, exists: {}", alias,  exists);
        return exists;
    }


    public boolean switchAlias(String newIndex, String alias) throws IOException {

        boolean switched = false;
        String previousIndex = currentAliasIndex(alias);
        if(StringUtils.isBlank(previousIndex)) {
            log.info("alias 에 기 등록된 인덱스가 없습니다.");
        }

        if( removeAlias(previousIndex, alias) ){
            addAlias(newIndex, alias);
            switched = true;
        }
        log.info("switchAlias - alias: {}, newIndex: {}, prevIndex: {}, switched: {}", alias, newIndex, previousIndex, switched);

        return switched;

    }

    public String currentAliasIndex(String alias) throws IOException {
        GetAliasesRequest requestWithAlias = new GetAliasesRequest(alias);
        GetAliasesResponse response = restHighLevelClient.indices().getAlias(requestWithAlias, RequestOptions.DEFAULT);

        String currentIndex = "";
        if( response.getAliases().keySet().size() == 1 ){
            for( String index : response.getAliases().keySet() ){
                currentIndex = index;
            }
        }
        return currentIndex;
    }


    public IndexRequest createIndexRequest(String indexName, Map<String, Object> data) {
        IndexRequest request = new IndexRequest(indexName);
        request.source(data);
        return request;
    }


    public void bulkIndex(String type) throws IOException {

        List<T> list = null;
        String alias = "";
        String newIndex="";

        if( PRODUCT_INDEX_ALIAS.equals(type) ) {
            newIndex = "product_" + new SimpleDateFormat ( "yyyyMMddHHmm").format(new Date());
            alias = PRODUCT_INDEX_ALIAS;
            list = (List<T>) searchIndexMapper.selectIndexTargetList();
//            List<ProductDocumentDomain> productList = searchIndexMapper.selectIndexTargetList();
//            productList.stream().forEach(l -> {
//                l.setName(l.getName().replaceAll("\\[","").replaceAll("]",""));
//            });
//            list = (List<T>) productList;
        }else if( "ac".equals(type) ){
            newIndex = "ac" + new SimpleDateFormat ( "yyyyMMddHHmm").format(new Date());
            alias = AC_INDEX_ALIAS;
            list = (List<T>) searchIndexMapper.selectAutoCompleteIndexTargetList();
        }else if( "boost".equals(type) ){
            newIndex = "boost" + new SimpleDateFormat ( "yyyyMMddHHmm").format(new Date());
            alias = CATEGORY_BOOST_INDEX_ALIAS;
            list = (List<T>) searchIndexMapper.selectBoostIndexTargetList();
        }else if( "test".equals(type) ) {
            newIndex = "test_" + new SimpleDateFormat ( "yyyyMMddHHmm").format(new Date());
            alias = "test";
            list = (List<T>) searchIndexMapper.selectIndexTargetList();
        }

        BulkRequest request = new BulkRequest();
        String finalNewIndex = newIndex;
        list.stream().forEach(data -> request.add(createIndexRequest(finalNewIndex, MAPPER.convertValue((Object) data, Map.class))));
        BulkResponse response = restHighLevelClient.bulk(request, RequestOptions.DEFAULT);

        //fixme: index 생성되었는지 체크.
        if(existAlias(alias)){
            switchAlias(newIndex, alias);
        }else{
            addAlias(newIndex, alias);
        }
    }


    public List<AnalyzeResultDomain> analyze(String keyword, String analyzerName) {

        List list = new ArrayList();

        AnalyzeRequest analyzeRequest = new AnalyzeRequest();
        analyzeRequest.index(PRODUCT_INDEX_ALIAS);
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

}
