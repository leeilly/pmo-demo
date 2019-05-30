package com.pulmuone.demo.api.search.domain;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ProductAutoCompleteResultDomain {

    private String name;
    private Integer score;

}
