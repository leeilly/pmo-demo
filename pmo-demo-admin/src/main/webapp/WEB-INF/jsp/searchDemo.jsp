<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ include file="/WEB-INF/jsp/common/menu.jsp"%>
<!DOCTYPE html>
<html lang="ko">
<head>
    <title>pmo search demo</title>
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <!-- 부트스트랩 -->
    <link href="/static/css/lib/bootstrap.min.css" rel="stylesheet" media="screen">
</head>
<body>
<div class="container">
    <h3>pmo search demo.</h3>
    <div class="input-append">
        <input class="span2 .search-keyword" id="search-keyword" type="text">
        <button class="btn search-btn" id="search-btn" type="button">검색</button>
    </div>

    <br/>
    <br/>
    <div id="autocomplete-search-result">
    </div>
    <br/>
    <%--    <b>검색결과: <span id="totalItemCnt"></span> 개</b>--%>


    <div class="form-row">
        <table>
            <tr>
                <th>알레르기 유발 식품 제외: </th>
                <td> <input type="checkbox" id="ingredients_except_filter_a" value="계란" class="ingredients_except_filter"> 계란 </td>
                <td> <input type="checkbox" id="ingredients_except_filter_b" value="새우" class="ingredients_except_filter"> 새우 </td>
                <td> <input type="checkbox" id="ingredients_except_filter_c" value="우유" class="ingredients_except_filter"> 우유 </td>
            </tr>
            <tr>
                <th>칼로리: </th>
                <td> <input type="radio" id="kcal_filter_ALL" value="" name="kcal_filter" checked> 전체</td>
                <td> <input type="radio" id="kcal_filter_200" value="KCAL200" name="kcal_filter"> 200kcal</td>
                <td> <input type="radio" id="kcal_filter_300" value="KCAL300" name="kcal_filter"> 300kcal</td>
                <td> <input type="radio" id="kcal_filter_400" value="KCAL400" name="kcal_filter"> 400kcal</td>
            </tr>
            <tr>
                <th>조리시간: </th>
                <td> <input type="radio" id="cooking_min_filter_ALL" value="" name="cooking_min_filter" checked> 전체</td>
                <td> <input type="radio" id="cooking_min_filter_5" value="M5" name="cooking_min_filter"> 5분 이내</td>
                <td> <input type="radio" id="cooking_min_filter_10" value=M10" name="cooking_min_filter"> 10분 이내</td>
                <td> <input type="radio" id="cooking_min_filter_20" value="M20" name="cooking_min_filter"> 20분 이내</td>
            </tr>
            <tr>
                <td></td>
                <td></td>
                <td></td>
                <td></td>
                <td></td>
                <td><button class="btn filter-search-btn" type="button">검색</button></td>
            </tr>
        </table>
        <br/>
        <br/>
        <div>
            <table class="table">
                <thead>
                <tr>
                    <th scope="col">#</th>
                    <th scope="col">상품명</th>
                    <th scope="col">점수</th>
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




<script src="http://code.jquery.com/jquery.js"></script>
<script src="/static/js/lib/bootstrap.min.js"></script>
<script>

    $("#search-btn").click(function(){
        var keyword = $("#search-keyword").val();
        //console.log("keyword: " + keyword);
        $.ajax({
            url:  'http://localhost:8001/v1/search/product?keyword='+keyword
            ,type: 'GET'
            , contentType:"application/json; charset=UTF-8"
            , success: function (result) {
                //console.log(result);

                //$('#totalItemCnt').html('');
                $('#search-result-list').html('');

                //$('#totalItemCnt').append(result.data.count);
                var html = '';
                $.each(result.data.searchResult, function (i, item) {

                    html += '<tr>' +
                        '<th scope="row">'+item.productSeq+'</th>' +
                        '<td>' + item.name + '</td>' +
                        '<td>' + item.score + '</td>' +
                        '<td>' + item.kcal + '</td>' +
                        '<td>' + item.cookingMinute + '</td>' +
                        '<td>' + item.ingredients + '</td>'+
                        '</tr>';
                });

                $('#search-result-list').append(html);
            }
            ,async: false
        });
    });

    $('#search-keyword').keyup(function(){
        var keyword = $(this).val();
        //console.log('keyword:[' + keyword + ']');

        if( keyword.length <= 0 ) return false;

        $.ajax({
            url:  'http://localhost:8001/v1/search/product/auto_complete?keyword='+keyword
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


    $('.filter-search-btn').click(function(){
        var keyword = $('#search-keyword').val();
        var param = '?keyword='+keyword;

        //ingredients_except_filter
        var exceptIngredients = "";
        $(".ingredients_except_filter:checked").each(function (index) {
            exceptIngredients += $(this).val() + ",";
        });
        if( exceptIngredients.length > 0 ){
            param += '&excludedFoodIngredients='+exceptIngredients;
        }

        var kcalRangeCode = "";
        if( $(":radio[name='kcal_filter']:checked").length > 0 && $(":radio[name='kcal_filter']:checked").val() != ""){
            kcalRangeCode = $(":radio[name='kcal_filter']:checked").val();
        }
        if(kcalRangeCode.length > 0){
            param += '&kcalRangeCode='+kcalRangeCode;
        }

        var cookingMinuteRangeCode = "";
        if( $(":radio[name='cooking_min_filter']:checked").length > 0 && $(":radio[name='cooking_min_filter']:checked").val() != "" ){
            cookingMinuteRangeCode = $(":radio[name='cooking_min_filter']:checked").val();
        }
        if(cookingMinuteRangeCode.length > 0){
            param += '&cookingMinuteRangeCode='+cookingMinuteRangeCode;
        }


        //console.log(kcalRangeCode);
        //console.log(exceptIngredients);
        //console.log(cookingMinuteRangeCode);
        //console.log(param);

        //var param = '?keyword='+keyword+'&excludedFoodIngredients='+exceptIngredients+'&cookingMin='+cookingMin;

        $.ajax({
            url:  'http://localhost:8001/v1/search/product' + param
            ,type: 'GET'
            , contentType:"application/json; charset=UTF-8"
            , success: function (result) {
                console.log(result);

                $('#search-result-list').html('');

                var html = '';
                $.each(result.data.searchResult, function (i, item) {

                    html += '<tr>' +
                        '<th scope="row">'+item.productSeq+'</th>' +
                        '<td>' + item.name + '</td>' +
                        '<td>' + item.score + '</td>' +
                        '<td>' + item.kcal + '</td>' +
                        '<td>' + item.cookingMinute + '</td>'+
                        '<td>' + item.ingredients + '</td>'+
                        '</tr>';
                });

                $('#search-result-list').append(html);
            }
            ,async: false
        });


    });


</script>

</body>
</html>