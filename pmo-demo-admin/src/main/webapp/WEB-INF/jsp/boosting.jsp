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
            <div class="alert alert-warning alert-dismissible" role="alert">
                <p>변경 내용은 우선 DB에만 반영됩니다. </p>
                <p>검색결과에 적용하기 위해서는, <b>'엔진반영'</b> 버튼을 클릭하여 검색엔진의 부스팅 사전을 갱신해야합니다.</p>
            </div>
            <br/>

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
    <div class="modal fade">
        <div class="modal-dialog">
            <div class="modal-content">
                <!-- remote ajax call이 되는영역 -->
            </div>
        </div>
    </div>
</div>


<!-- Modal -->
<div class="modal fade" id="add-modal" role="dialog">
    <div class="modal-dialog">
        <!-- Modal content-->
        <div class="modal-content">
            <div class="modal-header">
                <button type="button" class="close" data-dismiss="modal">×</button>
                <h4 class="modal-title">카테고리 부스팅 항목 추가</h4>
            </div>
            <div class="modal-body">
                <div class="form-group">
                    <form role="form">
                        <div class="form-group">
                            <label for="boost-keyword">키워드</label> <input type="text" class="form-control" id="boost-keyword" placeholder="">
                        </div>
                        <div class="form-group">
                            <label for="boost-ctgr">카테고리</label>
                            <select class="form-control" id="boost-ctgr">
                                <option value="">----선택----</option>
                                <option value="1">농산물/과일/견과</option>
                                <option value="2">샐러드/푸딩</option>
                                <option value="3">생라면</option>
                                <option value="4">스프/죽</option>
                                <option value="5">만두</option>
                                <option value="6">김밥/유부/주먹밥재료</option>
                                <option value="7">우동/국수</option>
                                <option value="8">콩나물/숙주/나물</option>
                                <option value="9">생수/스파클링</option>
                                <option value="11">생두유/우유</option>
                                <option value="12">파스타/커리</option>
                                <option value="13">냉면/쫄면</option>

                            </select>
                        </div>
                        <div class="form-group">
                            <label for="boost-score">점수</label> <input type="text" class="form-control" id="boost-score" placeholder="">
                        </div>
                    </form>
                </div>
            </div>
            <div class="modal-footer">
                <button type="button" class="btn btn-sm btn-primary" id="add-boosting-btn">추가</button>
                <button type="button" class="btn btn-sm btn-default" id="modal-close-btn" data-dismiss="modal">닫기</button>
            </div>
        </div>

    </div>
</div>




<script src="http://code.jquery.com/jquery.js"></script>
<script src="/static/js/lib/bootstrap.min.js"></script>

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
                this.bindSearch();
                this.bindEditMode();
                this.bindEdit();
                this.bindDelete();
                this.bindApply();
                this.bindAdd();

            },

            bindSearch : function(){
                var that = this;
                $("#search-btn").click(function(){
                    var keyword = $("#search-keyword").val().trim();
                    $.ajax({
                        url:  that.url.api + '/v1/search-admin/boosting-list?keyword='+keyword
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
                                    '<td><span class="glyphicon glyphicon-remove remove-btn" aria-hidden="true" data-boost-seq='+ item.boostSeq +'></span></td>' +
                                    '</tr>';
                            });

                            $('#search-result-list').append(html);
                        }
                        ,async: false
                    });
                });

            },

            bindEditMode : function(){
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
            },

            bindEdit : function(){
                var that = this;
                $(document).on('click', '.edit-ok-btn', function() {

                    var boostSeq = $(this).data('boostSeq');
                    var $input_score = $("#score" + boostSeq);
                    console.log('edit ok - boost_seq: ' + boostSeq);

                    /*스코어 점수 업데이트 API*/
                    var categoryBoostScoreDTO = {};
                    categoryBoostScoreDTO.boostSeq = boostSeq;
                    categoryBoostScoreDTO.score = $input_score.val();

                    $.ajax({
                        url:  that.url.api + '/v1/search-admin/boost-score'
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
            },

            bindDelete : function(){
                var that = this;
                $(document).on('click', '.remove-btn', function() {
                    var boostSeq = $(this).data('boostSeq');
                    console.log('remove - boost_seq: ' + boostSeq);

                    if( !confirm("삭제하시겠습니까?")){
                        return;
                    }

                    $(this).closest('tr').remove();

                    var categoryBoostScoreDTO = {};
                    categoryBoostScoreDTO.boostSeq = boostSeq;
                    $.ajax({
                        url:  that.url.api + '/v1/search-admin/remove-boost'
                        ,type: 'POST'
                        , contentType:"application/json; charset=UTF-8"
                        , data: JSON.stringify(categoryBoostScoreDTO)
                        , success: function (result) {
                            console.log(result);
                            alert('삭제 되었습니다.\n(엔진 반영은 안된 상태)');
                        }
                        ,async: false
                    });
                });
            },

            bindApply : function(){
                var that = this;
                /* 엔진 반영 */
                $("#apply-btn").click(function(){

                    if( confirm("부스팅 사전을 검색엔진에 반영하시겠습니까?") ) {
                        $.ajax({
                            url: that.url.api + '/v1/search-admin/apply-boost'
                            , type: 'GET'
                            , contentType: "application/json; charset=UTF-8"
                            , success: function (result) {
                                console.log(result);
                                if(result.data.code=='0') {
                                    alert('검색엔진에 반영되었습니다.)');
                                }
                            }
                            , async: false
                        });
                    }
                });
            },

            bindAdd : function(){

                var that = this;

                $("#add-btn").click(function(){
                    console.log('lp');
                    $("#boost-keyword").val($("#search-keyword").val());
                    $("#add-modal").modal();
                });

                $("#add-boosting-btn").click(function(){

                    var $keyword = $("#boost-keyword");
                    var $score = $("#boost-score");

                    if( $keyword.val().trim() == '' ){
                        alert('키워드를 입력해주세요.');
                        $keyword.focus();
                        return;
                    }

                    var categorySeq = $("#boost-ctgr option:selected").val();
                    var categoryName = $("#boost-ctgr option:selected").text();
                    if(categorySeq == ""){
                        alert('카테고리를 선택해주세요.');
                        $("#boost-ctgr").focus();
                        return;
                    }

                    if( $score.val().trim() == '' ){
                        alert('점수를 입력해주세요.');
                        $score.focus();
                        return;
                    }

                    if(!confirm('등록하시겠습니끼?')){
                        return;
                    }

                    var categoryBoostScoreDTO = {};
                    categoryBoostScoreDTO.keyword = $keyword.val().trim();
                    categoryBoostScoreDTO.score = $score.val();
                    categoryBoostScoreDTO.categorySeq = categorySeq;
                    categoryBoostScoreDTO.categoryName = categoryName;

                    $.ajax({
                        url: that.url.api + '/v1/search-admin/add-boost'
                        , type: 'POST'
                        , contentType: "application/json; charset=UTF-8"
                        , data: JSON.stringify(categoryBoostScoreDTO)
                        , success: function (result) {
                            console.log(result);
                            alert('등록 되었습니다.\n(엔진 반영은 안된 상태)');
                        }
                        , async: false
                    });

                    $("#modal-close-btn").click();
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