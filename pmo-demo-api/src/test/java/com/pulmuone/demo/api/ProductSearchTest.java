package com.pulmuone.demo.api;


import com.pulmuone.demo.api.search.domain.ProductAutoCompleteResultDomain;
import com.pulmuone.demo.api.search.domain.SearchResult;
import com.pulmuone.demo.api.search.dto.SearchRequestDTO;
import com.pulmuone.demo.api.search.service.ElasticSearchService;
import com.pulmuone.demo.api.search.service.SearchQueryBinder;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.List;

@RunWith(SpringRunner.class)
@SpringBootTest
public class ProductSearchTest {

    @Autowired
    private ElasticSearchService elasticSearchService;

    @Test
    public void searchAutoCompleteTest_검색어없음(){

        /**
         * 입력된 값이 없으면, 검색엔진으로 쿼리하지 않는다.
         */

        String input = "";

        SearchRequestDTO dto = new SearchRequestDTO();
        dto.setKeyword(input);

        try {
            SearchQueryBinder queryBinder = new SearchQueryBinder();
            SearchSourceBuilder query = queryBinder.autoCompleteQuery(dto);

            SearchResult searchResult = elasticSearchService.searchAutoComplete(query, ProductAutoCompleteResultDomain.class);

            List<ProductAutoCompleteResultDomain> list = searchResult.getSearchResult();
            searchResult.setSearchResult(list);

        } catch (Exception e) {
            Assert.assertEquals("[Search Exception] Search keyword must not be null.", e.getMessage());
        }

    }

    @Test
    public void searchAutoCompleteTest_1_자음한개() throws Exception {

        /**
         * 입력된 값이 없으면, 검색엔진으로 쿼리하지 않는다.
         * ㅅ을 입력하면, ㅅ으로 시작하는 상품명들이 검색되어야 함
         *
         * 샐러드 Best 4종 세트 (8개)
         * 새우듬뿍만두(300gx2)
         * 생면식감 꽃게탕면 (4개입)
         * 새콤달콤 유부초밥 (4인분)
         * 새알 동지팥죽 (2인분)
         * 새알동지팥죽 세트 (6인분)
         * 새콤달콤 제주도 감귤
         */

        String input = "ㅅ";

        SearchRequestDTO dto = new SearchRequestDTO();
        dto.setKeyword(input);

        SearchQueryBinder queryBinder = new SearchQueryBinder();
        SearchSourceBuilder query = queryBinder.autoCompleteQuery(dto);

        SearchResult searchResult = elasticSearchService.searchAutoComplete(query, ProductAutoCompleteResultDomain.class);

        List<ProductAutoCompleteResultDomain> list = searchResult.getSearchResult();
        searchResult.setSearchResult(list);

        System.out.println(searchResult.getCount());

        Assert.assertEquals(7, searchResult.getCount().intValue());
    }

    @Test
    public void searchAutoCompleteTest_2_새() throws Exception {

        /**
         * 입력된 값이 없으면, 검색엔진으로 쿼리하지 않는다.
         * '새'를 입력하면, '새'로 시작하거나 | '새'가 포함되거나 |  자소분석결과가 'ㅅㅐ'로 시작하는 상품명들이 검색되어야 함.
         *
         * 샐러드 Best 4종 세트 (8개)
         * 새우듬뿍만두(300gx2)
         * 생면식감 꽃게탕면 (4개입)
         * 새콤달콤 유부초밥 (4인분)
         * 새알 동지팥죽 (2인분)
         * 새알동지팥죽 세트 (6인분)
         * 새콤달콤 제주도 감귤
         */

        String input = "새";

        SearchRequestDTO dto = new SearchRequestDTO();
        dto.setKeyword(input);

        SearchQueryBinder queryBinder = new SearchQueryBinder();
        SearchSourceBuilder query = queryBinder.autoCompleteQuery(dto);

        SearchResult searchResult = elasticSearchService.searchAutoComplete(query, ProductAutoCompleteResultDomain.class);

        List<ProductAutoCompleteResultDomain> list = searchResult.getSearchResult();
        searchResult.setSearchResult(list);

        System.out.println(searchResult.getCount());

        Assert.assertEquals(7, searchResult.getCount().intValue());


    }

    @Test
    public void searchAutoCompleteTest_2_생() throws Exception {

        /**
         * 입력된 값이 없으면, 검색엔진으로 쿼리하지 않는다.
         * '생'을 입력하면, '생'으로 시작하거나 | '생'이 포함되거나 |  자소분석결과가 'ㅅㅐㅇ' 으로 시작하는 상품명들이 검색되어야 함.
         *
         * 샐러드 Best 4종 세트 (8개)  ------ X
         * 새우듬뿍만두(300gx2)
         * 생면식감 꽃게탕면 (4개입)
         * 새콤달콤 유부초밥 (4인분)     ------ X
         * 새알 동지팥죽 (2인분)
         * 새알동지팥죽 세트 (6인분)
         * 새콤달콤 제주도 감귤          ------ X
         */

        String input = "생";

        SearchRequestDTO dto = new SearchRequestDTO();
        dto.setKeyword(input);

        SearchQueryBinder queryBinder = new SearchQueryBinder();
        SearchSourceBuilder query = queryBinder.autoCompleteQuery(dto);

        SearchResult searchResult = elasticSearchService.searchAutoComplete(query, ProductAutoCompleteResultDomain.class);

        List<ProductAutoCompleteResultDomain> list = searchResult.getSearchResult();
        searchResult.setSearchResult(list);

        System.out.println(searchResult.getCount());

        Assert.assertEquals(4, searchResult.getCount().intValue());

    }

    @Test
    public void searchAutoCompleteTest_2_새우() throws Exception {

        /**
         * 입력된 값이 없으면, 검색엔진으로 쿼리하지 않는다.
         * '새우'를 입력하면, '새우'로 시작하거나 | '새우'기 포함되거나 |  자소분석결과가 'ㅅㅐㅇㅜ' 로 시작하는 상품명들이 검색되어야 함.
         *
         * 샐러드 Best 4종 세트 (8개)  ------ X
         * 새우듬뿍만두(300gx2)
         * 생면식감 꽃게탕면 (4개입)     -------X
         * 새콤달콤 유부초밥 (4인분)     ------ X
         * 새알 동지팥죽 (2인분)        ------ X
         * 새알동지팥죽 세트 (6인분)     ------ X
         * 새콤달콤 제주도 감귤          ------ X
         */

        String input = "새우";

        SearchRequestDTO dto = new SearchRequestDTO();
        dto.setKeyword(input);

        SearchQueryBinder queryBinder = new SearchQueryBinder();
        SearchSourceBuilder query = queryBinder.autoCompleteQuery(dto);

        SearchResult searchResult = elasticSearchService.searchAutoComplete(query, ProductAutoCompleteResultDomain.class);

        List<ProductAutoCompleteResultDomain> list = searchResult.getSearchResult();
        searchResult.setSearchResult(list);

        System.out.println(searchResult.getCount());

        Assert.assertEquals(1, searchResult.getCount().intValue());

    }




}
