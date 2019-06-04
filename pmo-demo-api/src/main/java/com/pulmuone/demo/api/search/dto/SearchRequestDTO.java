package com.pulmuone.demo.api.search.dto;
import com.pulmuone.demo.api.search.domain.CookingMinuteRangeCode;
import com.pulmuone.demo.api.search.domain.KcalRangeCode;
import com.pulmuone.demo.api.search.domain.SortCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;


/**
 * 상품 검색 Request DTO
 */
@Setter
@Getter
@ToString
public class SearchRequestDTO {

    /** keyword */
    private String keyword;

    /** filter */
    private String ingredients;
    private String preferredFood;
    private String excludedFoodIngredients;
    private CookingMinuteRangeCode cookingMinuteRangeCode;
    private KcalRangeCode kcalRangeCode;

    /** sort code : default = new */
    private SortCode sortCode = SortCode.NEW;

    /** paging */
    private Integer page = 1;
    private Integer limit = 30;

}
