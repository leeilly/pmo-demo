<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>

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
        <button class="btn search-btn" type="button">검색</button>
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
                <td> <input type="checkbox" id="kcal_filter_200" value=200 class="kcal_filter"> 200kcal </td>
                <td> <input type="checkbox" id="kcal_filter_300" value=300 class="kcal_filter"> 300kcal </td>
                <td> <input type="checkbox" id="kcal_filter_400" value=400 class="kcal_filter"> 400kcal </td>
            </tr>
            <tr>
                <th>조리시간: </th>
                <td> <input type="checkbox" id="cooking_min_filter_5" value=5 class="cooking_min_filter"> 5분 이내</td>
                <td> <input type="checkbox" id="cooking_min_filter_10" value=10 class="cooking_min_filter"> 10분 ~ 20분</td>
                <td> <input type="checkbox" id="cooking_min_filter_20" value=20 class="cooking_min_filter"> 20분 이상</td>
            </tr>
            <tr>
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
                    <th scope="col">name</th>
                    <th scope="col">score</th>
                    <th scope="col">kcal</th>
                    <th scope="col">ingredients</th>
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
    $('.search-btn').click(function(){
        var keyword = $("#search-keyword").val();
        console.log("keyword: " + keyword);
        $.ajax({
            url:  'http://localhost:8001/v1/search/product?keyword='+keyword
            ,type: 'GET'
            , contentType:"application/json; charset=UTF-8"
            , success: function (result) {
                console.log(result);

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
        console.log(keyword);
        $.ajax({
            url:  'http://localhost:8001/v1/search/product/auto_complete?keyword='+keyword
            ,type: 'GET'
            , contentType:"application/json; charset=UTF-8"
            , success: function (result) {
                console.log(result.data.searchResult);

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

        //ingredients_except_filter
        var exceptIngredients = "";
        $(".ingredients_except_filter:checked").each(function (index) {
            exceptIngredients += $(this).val() + ",";
        });

        var kcal = "";
        $(".kcal_filter:checked").each(function (index) {
            kcal += $(this).val() + ",";
        });

        var cookingMin = "";
        $(".cooking_min_filter:checked").each(function (index) {
            cookingMin += $(this).val() + ",";
        });

        console.log(kcal);
        console.log(exceptIngredients);
        console.log(cookingMin);

        var param = '?keyword='+keyword+'&excludedFoodIngredients='+exceptIngredients+'&kcal='+kcal;

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