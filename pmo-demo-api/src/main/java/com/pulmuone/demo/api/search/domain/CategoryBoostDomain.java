package com.pulmuone.demo.api.search.domain;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class CategoryBoostDomain {

    private Integer boostSeq;
    private String keyword;
    private Integer categorySeq;
    private String categoryName;
    private Integer score;
    private String modifiedYmdt;
}
