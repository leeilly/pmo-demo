package com.pulmuone.demo.api.search.controller;

import com.pulmuone.demo.api.search.domain.CategoryBoostDomain;
import com.pulmuone.demo.api.search.domain.SynonymDomain;
import com.pulmuone.demo.api.search.dto.CategoryBoostScoreDTO;
import com.pulmuone.demo.api.search.dto.SynonymDTO;
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

    @ApiOperation(value="카테고리 부스팅 리스트 조회", notes = "키워드별 카테고리 부스팅 리스트 조회")
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

    @ApiOperation(value="카테고리 부스팅 항목 삭제", notes = "카테고리 부스팅 항목 삭제")
    @RequestMapping(value = "/remove-boost", method = RequestMethod.POST)
    public ResponseEntity<ApiResult<Integer>> removeBoostingScore(@ApiParam("카테고리 부스팅 정보") @RequestBody CategoryBoostScoreDTO categoryBoostScoreDTO) throws Exception {

        int deletedCount = searchAdminService.deleteCategoryBoostScore(categoryBoostScoreDTO);

        return ResponseEntity.ok(ApiResult.ok(deletedCount));
    }

    @ApiOperation(value="카테고리 부스팅 항목 추가", notes = "카테고리 부스팅 항목 추가")
    @RequestMapping(value = "/add-boost", method = RequestMethod.POST)
    public ResponseEntity<ApiResult<Integer>> addBoostingScore(@ApiParam("카테고리 부스팅 정보") @RequestBody CategoryBoostScoreDTO categoryBoostScoreDTO) throws Exception {

        int insertedCount = searchAdminService.insertCategoryBoostScore(categoryBoostScoreDTO);

        return ResponseEntity.ok(ApiResult.ok(insertedCount));
    }


    @ApiOperation(value="카테고리 부스팅 엔진 반영", notes = "카테고리 부스팅 엔진 반영")
    @RequestMapping(value = "/apply-boost", method = RequestMethod.GET)
    public ResponseEntity<ApiResult<String>> applyBoostingDictionary() throws Exception {

        elasticSearchService.bulkIndex("boost");

        return ResponseEntity.ok(ApiResult.ok("반영완료"));
    }


    @ApiOperation(value="동의어 리스트 조회", notes = "동의어 리스트 조회")
    @RequestMapping(value = "/synonym-list", method = RequestMethod.GET)
    public ResponseEntity<ApiResult<List<SynonymDomain>>> synonymList(@ApiParam("검색어") @RequestParam(value = "keyword", required = false) String keyword) throws Exception {

        List<SynonymDomain> list = searchAdminService.getSynonymList(keyword);
        return ResponseEntity.ok(ApiResult.ok(list));
    }


    @ApiOperation(value="동의어 사전 수정", notes = "동의어 사전 항목 수정")
    @RequestMapping(value = "/synonym-edit", method = RequestMethod.POST)
    public ResponseEntity<ApiResult<Integer>> editSynonym(@ApiParam("동의어 사전 정보") @RequestBody SynonymDTO SynonymDTO) throws Exception {

        int updatedCount = searchAdminService.updateSynonym(SynonymDTO);

        return ResponseEntity.ok(ApiResult.ok(updatedCount));
    }

    @ApiOperation(value="동의어 사전 항목 추가", notes = "동의어 사전 항목 추가")
    @RequestMapping(value = "/add-synonym", method = RequestMethod.POST)
    public ResponseEntity<ApiResult<Integer>> addSynonym(@ApiParam("동의어 사전 정보") @RequestBody SynonymDTO synonymDTO) throws Exception {

        int insertedCount = searchAdminService.insertSynonym(synonymDTO);

        return ResponseEntity.ok(ApiResult.ok(insertedCount));
    }

}
