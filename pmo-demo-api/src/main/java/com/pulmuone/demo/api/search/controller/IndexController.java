package com.pulmuone.demo.api.search.controller;


import com.pulmuone.demo.api.search.service.ElasticSearchService;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

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
        }else if( "auto".equals(indexName) ){
            elasticSearchService.createAutoCompleteIndex(indexName);
        }else if( "auto2".equals(indexName) ){
            elasticSearchService.createAutoCompleteIndex(indexName);
        }else if( "auto4".equals(indexName) ){
            elasticSearchService.createAutoCompleteIndex(indexName);
        }


    }



}
