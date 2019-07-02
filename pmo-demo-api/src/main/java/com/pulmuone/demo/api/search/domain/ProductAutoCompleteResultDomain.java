package com.pulmuone.demo.api.search.domain;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.elasticsearch.common.text.Text;

@Getter
@Setter
@ToString
public class ProductAutoCompleteResultDomain {

    private String name;
    private Integer score;

    //private String highlight;
    private String highlight;

}
