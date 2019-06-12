package com.pulmuone.demo.api.search.service;

import com.pulmuone.demo.api.search.domain.CategoryBoostDomain;
import com.pulmuone.demo.api.search.dto.CategoryBoostScoreDTO;
import com.pulmuone.demo.api.search.mapper.SearchAdminMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Slf4j
public class SearchAdminService {

    @Autowired
    SearchAdminMapper searchAdminMapper;

    public List<CategoryBoostDomain> getCategoryBoostList(String keyword) {
        log.info("keyword: {}", keyword);

        return searchAdminMapper.selectCategoryBoostList(keyword);
    }

    public int updateCategoryBoostScore(CategoryBoostScoreDTO categoryBoostScoreDTO) {
        return searchAdminMapper.updateCategoryBoostScore(categoryBoostScoreDTO);
    }
}
