package com.pulmuone.demo.api.search.domain;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class AnalyzeResultDomain {

    private String term;
    private String type;
}
