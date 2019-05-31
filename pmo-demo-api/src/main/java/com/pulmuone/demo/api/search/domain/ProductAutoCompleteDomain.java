package com.pulmuone.demo.api.search.domain;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ProductAutoCompleteDomain {
    @JsonProperty
    private Integer score;

    @JsonProperty
    private String name;

    @JsonProperty
    private String nameNgram;

    @JsonProperty
    private String nameNgramEdge;

    @JsonProperty
    private String nameNgramEdgeBack;

    @JsonProperty
    private String nameChosung;

    @JsonProperty
    private String nameJamo;
}

