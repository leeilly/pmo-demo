<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>



<%@ include file="/WEB-INF/jsp/common/menu.jsp"%>
<!DOCTYPE html>
<html lang="ko">
<head>
    <title>pmo search demo</title>
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
</head>
<body>
<div class="container">
    <h3>검색 결과 확인</h3>
    <br/>
    <div>
        <ul class="nav nav-tabs">
            <li><a href="/search-demo/ac">자동완성 검색</a></li>
            <li class="active"><a href="/search-demo">상품 리스팅 검색</a></li>
        </ul>
    </div>

    <br/>
    <br/>
    <div class="input-append">
        <input class="span2 .search-keyword" id="search-keyword" type="text">
        <button type="button" class="btn btn-sm btn-primary search-btn" id="search-btn">검색</button>
    </div>

    <br/>
    <br/>
    <div id="autocomplete-search-result">
    </div>
    <br/>
    <div class="form-row">
        <form>
            <div class="form-group row">
                <label for="ingredients_except_filter_a" class="col-sm-2 col-form-label">알레르기 유발 식품 제외</label>
                <div class="col-sm-10">
                    <input type="checkbox" id="ingredients_except_filter_a" value="계란" class="ingredients_except_filter"> 계란
                    <input type="checkbox" id="ingredients_except_filter_b" value="새우" class="ingredients_except_filter"> 새우
                    <input type="checkbox" id="ingredients_except_filter_c" value="우유" class="ingredients_except_filter"> 우유
                </div>
            </div>
            <div class="form-group row">
                <label for="kcal_filter_ALL" class="col-sm-2 col-form-label">칼로리</label>
                <div class="col-sm-10">
                    <input type="radio" id="kcal_filter_ALL" value="" name="kcal_filter" checked> 전체
                    <input type="radio" id="kcal_filter_200" value="KCAL200" name="kcal_filter"> 200kcal
                    <input type="radio" id="kcal_filter_300" value="KCAL300" name="kcal_filter"> 300kcal
                    <input type="radio" id="kcal_filter_400" value="KCAL400" name="kcal_filter"> 400kcal
                </div>
            </div>
            <div class="form-group row">
                <label for="cooking_min_filter_ALL" class="col-sm-2 col-form-label">조리시간</label>
                <div class="col-sm-10">
                    <input type="radio" id="cooking_min_filter_ALL" value="" name="cooking_min_filter" checked> 전체
                    <input type="radio" id="cooking_min_filter_5" value="M5" name="cooking_min_filter"> 5분 이내
                    <input type="radio" id="cooking_min_filter_10" value=M10" name="cooking_min_filter"> 10분 이내
                    <input type="radio" id="cooking_min_filter_20" value="M20" name="cooking_min_filter"> 20분 이내
                    <button class="btn btn-xs btn-success filter-search-btn" type="button" style="margin-left: 20px;">선택 검색</button>
                </div>
            </div>
        </form>
        <br/>
        <br/>
        <div>
            <table class="table">
                <thead>
                <tr>
                    <th scope="col">#</th>
                    <th scope="col">상품명</th>
                    <th scope="col">카테고리번호</th>
                    <th scope="col">카테고리명</th>
                    <th scope="col">카테고리부스팅점수</th>
                    <th scope="col">인기도점수</th>
                    <th scope="col">kcal</th>
                    <th scope="col">조리시간</th>
                    <th scope="col">성분</th>
                </tr>
                </thead>
                <tbody id="search-result-list">
                </tbody>
            </table>
        </div>
    </div>
</div>


<script src="/static/jquery/jquery.js"></script>
<script src="/static/bootstrap/js/bootstrap.min.js"></script>
<script type="text/javascript">

var pmoApp = (function($) {

    'use strict';

    return $.extend(true, window.pmoApp || {}, {
        url : {
            api: '${apiUrl}'
        },

        data : {
        },

        init : function(){
            var that = this;
            console.log(that.url.api);

            this.bindSearch();
            this.bindFilterSearch();
            //this.bindAcSearch();
        },

        bindSearch : function(){
            var that = this;
            $("#search-btn").click(function () {
                var keyword = $("#search-keyword").val();


                //console.log("keyword: " + keyword);
                $.ajax({
                    url: that.url.api + '/v1/search/product?keyword=' + keyword
                    , type: 'GET'
                    , contentType: "application/json; charset=UTF-8"
                    , success: function (result) {
                        $('#search-result-list').html('');

                        var html = '';
                        $.each(result.data.searchResult, function (i, item) {

                            html += '<tr>' +
                                '<th scope="row">' + item.productSeq + '</th>' +
                                '<td>' + item.name + '</td>' +
                                '<td>' + item.categorySeq + '</td>' +
                                '<td>' + item.categoryName + '</td>' +
                                '<td></td>' +
                                '<td>' + item.score + '</td>' +
                                '<td>' + item.kcal + '</td>' +
                                '<td>' + item.cookingMinute + '</td>' +
                                '<td>' + item.ingredients + '</td>' +
                                '</tr>';
                        });

                        $('#search-result-list').append(html);
                    }
                    , async: false
                });
            });

        },

        bindAcSearch : function(){
            var that = this;

            $('#search-keyword').keyup(function(){
                var keyword = $(this).val();

                if( keyword.length <= 0 ) return false;

                $.ajax({
                    url:  that.url.api + '/v1/search/product/auto_complete?keyword='+keyword
                    ,type: 'GET'
                    , contentType:"application/json; charset=UTF-8"
                    , success: function (result) {
                        //console.log(result.data.searchResult);

                        $.each(result.data.searchResult, function (i, item) {
                            $('#autocomplete-search-result').append(item.name + " : " + item.score + '<br>');
                        })
                    }
                    ,async: false
                });
            });

            $('#search-keyword').keydown(function(){
                $('#autocomplete-search-result').text('');

            });
        },

        bindFilterSearch : function(){
            var that = this;
            $('.filter-search-btn').click(function () {
                var keyword = $('#search-keyword').val();
                var param = '?keyword=' + keyword;

                //ingredients_except_filter
                var exceptIngredients = "";
                $(".ingredients_except_filter:checked").each(function (index) {
                    exceptIngredients += $(this).val() + ",";
                });
                if (exceptIngredients.length > 0) {
                    param += '&excludedFoodIngredients=' + exceptIngredients;
                }

                var kcalRangeCode = "";
                if ($(":radio[name='kcal_filter']:checked").length > 0 && $(":radio[name='kcal_filter']:checked").val() != "") {
                    kcalRangeCode = $(":radio[name='kcal_filter']:checked").val();
                }
                if (kcalRangeCode.length > 0) {
                    param += '&kcalRangeCode=' + kcalRangeCode;
                }

                var cookingMinuteRangeCode = "";
                if ($(":radio[name='cooking_min_filter']:checked").length > 0 && $(":radio[name='cooking_min_filter']:checked").val() != "") {
                    cookingMinuteRangeCode = $(":radio[name='cooking_min_filter']:checked").val();
                }
                if (cookingMinuteRangeCode.length > 0) {
                    param += '&cookingMinuteRangeCode=' + cookingMinuteRangeCode;
                }

                $.ajax({
                    url: that.url.api + '/v1/search/product' + param
                    , type: 'GET'
                    , contentType: "application/json; charset=UTF-8"
                    , success: function (result) {
                        //console.log(result);

                        $('#search-result-list').html('');

                        var html = '';
                        $.each(result.data.searchResult, function (i, item) {

                            html += '<tr>' +
                                '<th scope="row">' + item.productSeq + '</th>' +
                                '<td>' + item.name + '</td>' +
                                '<td>' + item.categorySeq + '</td>' +
                                '<td>' + item.categoryName + '</td>' +
                                '<td></td>' +
                                '<td>' + item.score + '</td>' +
                                '<td>' + item.kcal + '</td>' +
                                '<td>' + item.cookingMinute + '</td>' +
                                '<td>' + item.ingredients + '</td>' +
                                '</tr>';
                        });

                        $('#search-result-list').append(html);
                    }
                    , async: false
                });
            });
        }
    })

})(jQuery);

$(function(){
    pmoApp.init();
});

</script>

</body>
</html>