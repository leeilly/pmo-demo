package com.pulmuone.demo.api.search.domain;

import lombok.Getter;
import lombok.Setter;


@Getter
@Setter
public class BoostDocumentDomain {

    private Integer boostSeq;
    private String keyword;
    private Integer categorySeq;
    private Integer score;
    private String createYmdt;
}
