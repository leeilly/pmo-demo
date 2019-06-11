package com.pulmuone.demo.api.search.mapper;

import com.pulmuone.demo.api.search.domain.CategoryBoostDomain;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;


import java.util.List;

@Mapper
public interface SearchAdminMapper {

    List<CategoryBoostDomain> selectCategoryBoostList(@Param("keyword") String keyword);


}
