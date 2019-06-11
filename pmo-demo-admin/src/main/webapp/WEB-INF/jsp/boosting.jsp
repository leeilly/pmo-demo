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
    <h3>카테고리 부스팅</h3>
    <div class="input-append">
        <br/>
        <input class="span2 .search-keyword" id="search-keyword" type="text">
        <button class="btn btn-sm btn-primary search-btn" id="search-btn" type="button">검색</button>
    </div>
    <br/>
    <div class="form-row">
        <br/>
        <br/>
        <div>

            <p style="float:left;"> <button type="button" class="btn btn-sm btn-success" id="add-btn">추가</button></p>
            <p style="float:right; margin-left: 10px;"><button type="button" class="btn btn-sm btn-warning" id="reset-btn"> 엔진반영</button></p>
            <p style="float:right;"><button type="button" class="btn btn-sm btn-info" id="analyze-btn">저장</button></p>
            <table class="table">
                <thead>
                <tr>
                    <th scope="col">#</th>
                    <th scope="col">카테고리번호</th>
                    <th scope="col">카테고리명</th>
                    <th scope="col">부스팅점수</th>
                    <th scope="col">수정일시</th>
                    <th scope="col">관리</th>
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
        $.ajax({
            url:  'http://localhost:8001/v1/search-admin/boosting-list?keyword='+keyword
            ,type: 'GET'
            , contentType:"application/json; charset=UTF-8"
            , success: function (result) {
                console.log(result);

                if( result.data.length <= 0 ){
                    alert('해당 키워드로 등록된 카테고리가 없습니다.');
                    return;
                }

                $('#search-result-list').html('');

                var html = '';
                $.each(result.data, function (i, item) {

                    html += '<tr>' +
                        '<th scope="row">'+(i+1)+'</th>' +
                        '<td>' + item.categorySeq + '</td>' +
                        '<td>' + item.categoryName + '</td>' +
                        '<td>' + item.score + '</td>' +
                        '<td>' + item.modifiedYmdt + '</td>' +
                        '<td></td>' +
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