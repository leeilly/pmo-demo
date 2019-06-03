package com.pulmuone.demo.api.search.controller;

import com.pulmuone.demo.api.search.domain.*;
import com.pulmuone.demo.api.search.dto.SearchRequestDTO;
import com.pulmuone.demo.api.search.service.ElasticSearchService;
import com.pulmuone.demo.api.search.service.SearchQueryBinder;
import com.pulmuone.demo.common.domain.ApiResult;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

@Slf4j
@RestController
@RequestMapping("/v1/search")
public class SearchController {

    @Autowired
    private ElasticSearchService elasticSearchService;

    @ApiOperation(value="상품검색", notes = "elasticsearch 상품 검색 결과 리스팅")
    @RequestMapping(value = "/product", method = RequestMethod.GET)
    public ResponseEntity<ApiResult<SearchResult>> searchProduct(
            @ApiParam("검색어") @RequestParam(value = "keyword", required = true) String keyword,
            @ApiParam("선호 식품 (optional)") @RequestParam(value = "preferredFood", required = false) String preferredFood,
            @ApiParam("알레르기 유발 식품 제외(optional)") @RequestParam(value = "excludedFoodIngredients", required = false) String excludedFoodIngredients,
            @ApiParam("조리시간(optional)") @RequestParam(value = "cookingMinute", required = false) Integer cookingMinute,
            @ApiParam("칼로리(optional)") @RequestParam(value = "kcal", required = false) String kcal,
            @ApiParam("정렬 코드 (optional)") @RequestParam(value = "sortCode", required = false) String sortCode,
            @ApiParam("최대 조회 건수(default: false)") @RequestParam(value = "limit", required = false, defaultValue = "30") Integer limit,
            @ApiParam("페이지 번호(default: 0)") @RequestParam(value = "pageNumber", required = false, defaultValue = "0") Integer pageNumber) throws Exception {

        if(StringUtils.isBlank(keyword)) {
            throw new Exception("Search keyword must not be null.");
        }

        SearchRequestDTO dto = new SearchRequestDTO();
        dto.setKeyword(keyword);
        dto.setPage(pageNumber);
        dto.setLimit(limit);
        if(StringUtils.isNotBlank(sortCode)) {
            dto.setSortCode(SortCode.valueOf(sortCode));
        }

        if(StringUtils.isNotBlank(preferredFood)) {
            dto.setPreferredFood(preferredFood);
        }

        if(StringUtils.isNotBlank(excludedFoodIngredients)) {
            dto.setExcludedFoodIngredients(excludedFoodIngredients);
        }

        if(StringUtils.isNotBlank(kcal)) {
            int max = 0;
            String[] kcalArr = kcal.split(",");
            max = Arrays.stream(kcalArr).mapToInt(v-> Integer.parseInt(v)).max().getAsInt();
            dto.setKcal(max);
            log.info("max: {}", max);
        }else{
            dto.setKcal(0);
        }

       // dto.setKcal(Optional.ofNullable(kcal).orElse(0));
        dto.setCookingMinute(Optional.ofNullable(cookingMinute).orElse(0));

        SearchQueryBinder queryBinder = new SearchQueryBinder();
        SearchSourceBuilder query = queryBinder.query(dto);
        log.info("query= {}", query.toString());

        SearchResult searchResult = elasticSearchService.search(query, ProductDocumentDomain.class);

        List<ProductDocumentDomain> list = searchResult.getSearchResult();
        searchResult.setSearchResult(list);

        return ResponseEntity.ok(ApiResult.ok(searchResult));
    }

    @ApiOperation(value="자동완성 검색", notes = "elasticsearch 상품 자동완성 검색")
    @RequestMapping(value = "/product/auto_complete", method = RequestMethod.GET)
    public ResponseEntity<ApiResult<SearchResult>> searchAutoComplete(
            @ApiParam("검색어") @RequestParam(value = "keyword", required = true) String keyword,
            @ApiParam("정렬 코드 (optional)") @RequestParam(value = "sortCode", required = false) String sortCode,
            @ApiParam("최대 조회 건수(default: false)") @RequestParam(value = "limit", required = false, defaultValue = "10") Integer limit,
            @ApiParam("페이지 번호(default: 0)") @RequestParam(value = "pageNumber", required = false, defaultValue = "0") Integer pageNumber) throws Exception {


        SearchRequestDTO dto = new SearchRequestDTO();
        dto.setKeyword(keyword);
        dto.setPage(pageNumber);
        dto.setLimit(limit);
        if(StringUtils.isNotBlank(sortCode)) {
            dto.setSortCode(SortCode.valueOf(sortCode));
        }

        SearchQueryBinder queryBinder = new SearchQueryBinder();
        SearchSourceBuilder query = queryBinder.autoCompleteQuery(dto);
        log.info("query= {}", query.toString());

        SearchResult searchResult = elasticSearchService.searchAutoComplete(query, ProductAutoCompleteResultDomain.class);

        List<ProductAutoCompleteResultDomain> list = searchResult.getSearchResult();
        searchResult.setSearchResult(list);

        return ResponseEntity.ok(ApiResult.ok(searchResult));
    }




}
