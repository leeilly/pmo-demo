package com.pulmuone.demo.api.search.domain;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.ArrayList;
import java.util.List;


@Setter
@Getter
@ToString
public class SearchResult<T> {

    /** total result row count */
    private Long count;

    /** search result(document) list */
    private List<T> searchResult = new ArrayList<>();


}
