package com.pulmuone.demo.api.search.domain;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.sql.Timestamp;

/**
 * 상품 검색 결과(Document) Domain
 */
@Getter
@Setter
@ToString
public class ProductDocumentDomain {

    /** 상품번호 */
    @JsonProperty
    private Long productSeq;

    /** 상품명 */
    @JsonProperty
    private String name;

    /** 상품검색어 */
    @JsonProperty
    private String searchKeyword;

    /** 카테고리 명 */
    @JsonProperty
    private String categoryName;

    /** 카테고리 번호 */
    @JsonProperty
    private String categorySeq;

    /** 상품상태코드 */
    @JsonProperty
    private String statusCode;

    /** 상품 등록일 */
    @JsonProperty
    private Timestamp createYmdt;

    /** 점수 */
    @JsonProperty
    private Integer score;

}
