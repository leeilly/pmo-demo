package com.pulmuone.demo.api.search.domain;

import com.fasterxml.jackson.annotation.JsonProperty;

public class ProductAutoCompleteDomain {

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

