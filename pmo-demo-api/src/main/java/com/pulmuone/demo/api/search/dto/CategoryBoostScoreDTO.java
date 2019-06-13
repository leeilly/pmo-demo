package com.pulmuone.demo.api.search.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CategoryBoostScoreDTO {

    private Integer boostSeq;
    private String keyword;
    private Integer categorySeq;
    private String categoryName;
    private Integer score;

}
