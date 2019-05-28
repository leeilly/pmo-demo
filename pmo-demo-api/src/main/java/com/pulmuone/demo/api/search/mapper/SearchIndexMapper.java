package com.pulmuone.demo.api.search.mapper;

import com.pulmuone.demo.api.search.domain.ProductAutoCompleteDomain;
import com.pulmuone.demo.api.search.domain.ProductDocumentDomain;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;
@Mapper
public interface SearchIndexMapper {

    List<ProductDocumentDomain> selectIndexTargetList();

    List<ProductAutoCompleteDomain> selectAutoCompleteIndexTargetList();
}
