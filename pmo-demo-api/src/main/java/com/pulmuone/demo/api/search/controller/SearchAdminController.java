package com.pulmuone.demo.api.search.controller;

import com.pulmuone.demo.api.search.domain.CategoryBoostDomain;
import com.pulmuone.demo.api.search.dto.CategoryBoostScoreDTO;
import com.pulmuone.demo.api.search.service.ElasticSearchService;
import com.pulmuone.demo.api.search.service.SearchAdminService;
import com.pulmuone.demo.common.domain.ApiResult;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/v1/search-admin")
public class SearchAdminController {

    @Autowired
    SearchAdminService searchAdminService;

    @Autowired
    ElasticSearchService elasticSearchService;

    @ApiOperation(value="카테고리 부스팅 리스틑 조회", notes = "키워드별 카테고리 부스팅 리스트 조회")
    @RequestMapping(value = "/boosting-list", method = RequestMethod.GET)
    public ResponseEntity<ApiResult<List<CategoryBoostDomain>>> searchProduct(
            @ApiParam("검색어") @RequestParam(value = "keyword", required = true) String keyword,
            @ApiParam("최대 조회 건수(default: false)") @RequestParam(value = "limit", required = false, defaultValue = "30") Integer limit,
            @ApiParam("페이지 번호(default: 0)") @RequestParam(value = "pageNumber", required = false, defaultValue = "0") Integer pageNumber) throws Exception {


        List<CategoryBoostDomain> list = searchAdminService.getCategoryBoostList(keyword);

        return ResponseEntity.ok(ApiResult.ok(list));
    }


    @ApiOperation(value="카테고리 부스팅 점수 업데이트", notes = "키워드별 카테고리 부스팅 점수 조정")
    @RequestMapping(value = "/boost-score", method = RequestMethod.POST)
    public ResponseEntity<ApiResult<Integer>> editBoostingScore(@ApiParam("카테고리 부스팅 정보") @RequestBody CategoryBoostScoreDTO categoryBoostScoreDTO) throws Exception {

        int updatedCount = searchAdminService.updateCategoryBoostScore(categoryBoostScoreDTO);

        return ResponseEntity.ok(ApiResult.ok(updatedCount));
    }


    @ApiOperation(value="카테고리 부스팅 엔진 반영", notes = "카테고리 부스팅 엔진 반영")
    @RequestMapping(value = "/apply-boost", method = RequestMethod.GET)
    public ResponseEntity<ApiResult<String>> applyBoostingDictionary() throws Exception {

        elasticSearchService.bulkIndex("boost");

        return ResponseEntity.ok(ApiResult.ok("반영완료"));
    }


}
