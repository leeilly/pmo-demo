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
            <p style="float:right; margin-left: 10px;"><button type="button" class="btn btn-sm btn-warning" id="apply-btn"> 엔진반영</button></p>
<%--            <p style="float:right;"><button type="button" class="btn btn-sm btn-info" id="analyze-btn">저장</button></p>--%>
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
                        '<td>' +
                        '   <input type="text" id="score'+ item.boostSeq +'" value='+ item.score +' size="10" readonly="readonly" style="border:none;">' +
                        '   <span class="glyphicon glyphicon-pencil edit-btn" style="margin-left:20px;" aria-hidden="true" data-boost-seq='+ item.boostSeq +' ></span>' +
                        '   <span class="glyphicon glyphicon-ok edit-ok-btn" style="margin-left:20px; display:none;" aria-hidden="true" data-boost-seq='+ item.boostSeq +'></span>' +
                        '</td>'+
                        '<td>' + item.modifiedYmdt + '</td>' +
                        '<td><span class="glyphicon glyphicon-remove remove-btn" aria-hidden="true"></span></td>' +
                        '</tr>';
                });

                $('#search-result-list').append(html);
            }
            ,async: false
        });
    });


    $("#add-btn").click(function(){
       alert('감 키워드에 대해 새 카테고리를 등록합니다.');
    });

    $(document).on('click', '.edit-btn', function() {
        var boostSeq = $(this).data('boostSeq');
        console.log('edit -  boost_seq: ' + boostSeq );

        var $input_score = $("#score"+boostSeq);
        if ($input_score.prop("readonly") == true) {
            $input_score.prop("readonly", false);
            $(this).hide();
            $input_score.closest('td').find(".edit-ok-btn").show();
            $input_score.focus();
        }

    });

    $(document).on('click', '.edit-ok-btn', function() {

        var boostSeq = $(this).data('boostSeq');
        var $input_score = $("#score" + boostSeq);
        console.log('edit ok - boost_seq: ' + boostSeq);

        /*스코어 점수 업데이트 API*/
        var categoryBoostScoreDTO = {};
        categoryBoostScoreDTO.boostSeq = boostSeq;
        categoryBoostScoreDTO.score = $input_score.val();

        $.ajax({
            url:  'http://localhost:8001/v1/search-admin/boost-score'
            ,type: 'POST'
            , contentType:"application/json; charset=UTF-8"
            , data: JSON.stringify(categoryBoostScoreDTO)
            , success: function (result) {
                console.log(result);
                alert('점수가 변경 되었습니다.\n(엔진 반영은 안된 상태)');
                $input_score.prop("readonly", true);
                $input_score.closest('td').find(".edit-ok-btn").hide();
                $input_score.closest('td').find(".edit-btn").show();
            }
            ,async: false
        });

    });

    /* 엔진 반영 */
    $("#apply-btn").click(function(){

        if( confirm("엔진에 반영하시겠습니까?") ) {
            $.ajax({
                url: 'http://localhost:8001/v1/search-admin/apply-boost'
                , type: 'GET'
                , contentType: "application/json; charset=UTF-8"
                , success: function (result) {
                    console.log(result);
                    alert('검색엔진에 반영되었습니다.)');
                }
                , async: false
            });
        }

    });



</script>

</body>
</html>