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
import java.util.Optional;

@Slf4j
@RestController
@RequestMapping("/v1/index")
public class IndexController {

    @Autowired
    ElasticSearchService elasticSearchService;

    @ApiOperation(value = "전체 색인", notes = "elasticsearch 전체 색인")
    @RequestMapping(value = "/bulk", method = RequestMethod.GET)
    public void indexProduct( @ApiParam("인덱스 type: 상품=product / 자동완성=ac / 카테고리부스팅=boost") @RequestParam(value = "type", required = true) String type) throws Exception {

        log.info("type: {}", type);
        elasticSearchService.bulkIndex(type);
    }

    @ApiOperation(value = "형태소분석", notes = "elasticsearch 형태소 분석")
    @RequestMapping(value = "/analyze", method = RequestMethod.GET)
    public ResponseEntity<ApiResult<List<AnalyzeResultDomain>>> analyze(@ApiParam("키워드") @RequestParam(value = "keyword", required = true) String keyword
            , @ApiParam("analyzer 이름") @RequestParam(value = "analyzerName", required = true, defaultValue = "my_analyzer") String analyzerName
    ){
        List<AnalyzeResultDomain> termList = elasticSearchService.analyze(keyword, analyzerName);
        return ResponseEntity.ok(ApiResult.ok(termList));
    }

    @ApiOperation(value = "전체 색인", notes = "elasticsearch 전체 색인")
    @RequestMapping(value = "bulk-index", method = RequestMethod.GET)
    public void indexBulkWithAnalyzer( @ApiParam("인덱스 타켓 type: 상품=product / 자동완성=ac ") @RequestParam(value = "indexType", required = true) String indexType
            ,@ApiParam("분석기 type") @RequestParam(value = "analyzerType", required = false) String analyzerType) throws Exception {

        log.info("indexType: {}", indexType);
        analyzerType = Optional.ofNullable(analyzerType).orElse("my_analyzer");
        log.info("analyzerType: {}", analyzerType);
        elasticSearchService.indexBulkWithAnalyzer(indexType, analyzerType);
    }

    @ApiOperation(value = "템플릿 업데이트", notes = "elasticsearch 색인 템플릿 정보 업데이트")
    @RequestMapping(value = "/change-template", method = RequestMethod.GET)
    public void putTemplate(@ApiParam("analyzerType") @RequestParam(value = "analyzerType", required = true) String analyzerType) {

        log.info("analyzerType: {}", analyzerType);
        elasticSearchService.putTemplate(analyzerType);
    }
}
