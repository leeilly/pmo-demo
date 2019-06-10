package com.pulmuone.demo.api.search.controller;


import com.pulmuone.demo.api.search.domain.AnalyzeResultDomain;
import com.pulmuone.demo.api.search.service.ElasticSearchService;
import com.pulmuone.demo.common.domain.ApiResult;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/v1/index")
public class IndexController {

    @Autowired
    ElasticSearchService elasticSearchService;

    @ApiOperation(value = "상품색인", notes = "elasticsearch 상품 색인")
    @RequestMapping(value = "/product", method = RequestMethod.GET)
    public void indexProduct( @ApiParam("인덱스명") @RequestParam(value = "indexName", required = true) String indexName) throws Exception {

        if( "product".equals(indexName) || "test".equals(indexName) ) {
            elasticSearchService.createIndex(indexName);
        }else if( "product_ac".equals(indexName) ){
            elasticSearchService.createAutoCompleteIndex(indexName);
        }else if( "boost".equals(indexName) ){
            elasticSearchService.createBoostIndex(indexName);
        }
    }

    @ApiOperation(value = "형태소분석", notes = "elasticsearch 형태소 분석")
    @RequestMapping(value = "/analyze", method = RequestMethod.GET)
    public ResponseEntity<ApiResult<List<AnalyzeResultDomain>>> analyze(@ApiParam("키워드") @RequestParam(value = "keyword", required = true) String keyword
            , @ApiParam("analyzer 이름") @RequestParam(value = "analyzerName", required = true, defaultValue = "my_analyzer") String analyzerName
    ){

        List<AnalyzeResultDomain> termList = elasticSearchService.analyze(keyword, analyzerName);
        termList.stream().forEach(t -> {
            log.info("term: {}, type: {}", t.getTerm(), t.getType());

        });

        return ResponseEntity.ok(ApiResult.ok(termList));

    }



}
