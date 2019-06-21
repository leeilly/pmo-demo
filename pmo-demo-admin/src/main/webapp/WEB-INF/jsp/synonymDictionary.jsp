<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ include file="/WEB-INF/jsp/common/menu.jsp"%>
<!DOCTYPE html>
<html lang="ko">
<head>
    <title>pmo search demo</title>
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
</head>
<body>
<div class="container">
    <h3>사전 관리</h3>
    <div class="input-append">
        <br/>
        <div>
            <ul class="nav nav-tabs">
                <li class="active"><a href="/dic/synonym">동의어 사전</a></li>
                <li><a href="/dic/user">고유어 사전</a></li>
                <li><a href="/dic/stop">불용어 사전</a></li>
            </ul>
        </div>
        <br/>
        <input class="span2 .search-keyword" id="search-keyword" type="text">
        <button class="btn btn-sm btn-primary search-btn" id="search-btn" type="button">검색</button>
    </div>
    <br/>
    <div class="form-row">
        <div>
            <div class="alert alert-warning alert-dismissible" role="alert">
                <p>변경 내용은 우선 DB에만 반영됩니다. </p>
                <p>검색결과에 적용하기 위해서는, </p>
                <p>1) <b>'엔진반영'</b> 버튼을 클릭하여 검색엔진의 동의어 사전을 갱신하고</p>
                <p>2) 다시 <b>색인</b>해야 합니다.</p>
            </div>
            <br/>
            <p style="float:left;"> <button type="button" class="btn btn-sm btn-success" id="add-btn">추가</button></p>
            <p style="float:right; margin-left: 10px;"><button type="button" class="btn btn-sm btn-warning" id="apply-btn"> 엔진반영</button></p>
            <%--            <p style="float:right;"><button type="button" class="btn btn-sm btn-info" id="analyze-btn">저장</button></p>--%>
            <table class="table">
                <thead>
                <tr>
                    <th scope="col">#</th>
                    <th scope="col">동의어</th>
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
                <h4 class="modal-title">동의어 사전 항목 추가</h4>
            </div>
            <div class="modal-body">
                <div class="form-group">
                    <form role="form">
                        <div class="form-group">
                            <label for="synonym-keyword">키워드</label> <input type="text" class="form-control" id="synonym-keyword" placeholder="">
                        </div>
                    </form>
                </div>
            </div>
            <div class="modal-footer">
                <button type="button" class="btn btn-sm btn-primary" id="add-synonym-btn">추가</button>
                <button type="button" class="btn btn-sm btn-default" id="modal-close-btn" data-dismiss="modal">닫기</button>
            </div>
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
                this.bindSearch();
                this.bindEditMode();
                this.bindEdit();
                this.bindAdd();
                this.bindDelete();
                this.bindApply();
            },

            bindSearch : function(){
                var that = this;
                $("#search-btn").click(function(){

                    var url = that.url.api + '/v1/search-admin/synonym-list';
                    var keyword = $("#search-keyword").val().trim();
                    if( keyword != '' ){
                        url = url + '?keyword='+keyword;
                    }

                    $.ajax({
                        url:  url
                        ,type: 'GET'
                        , contentType:"application/json; charset=UTF-8"
                        , success: function (result) {
                            console.log(result);

                            $('#search-result-list').html('');

                            var html = '';
                            $.each(result.data, function (i, item) {

                                html += '<tr>' +
                                    '<th scope="row">'+(i+1)+'</th>' +
                                    '<td>' +
                                    '   <input type="text" id="synonym'+ item.synonymSeq +'" value='+ item.synonym +' readonly="readonly" style="border:none;">' +
                                    '   <span class="glyphicon glyphicon-pencil edit-btn" style="margin-left:20px;" aria-hidden="true" data-synonym-seq='+ item.synonymSeq +' ></span>' +
                                    '   <span class="glyphicon glyphicon-ok edit-ok-btn" style="margin-left:20px; display:none;" aria-hidden="true" data-synonym-seq='+ item.synonymSeq +'></span>' +
                                    '</td>'+
                                    '<td>' + item.modifiedYmdt + '</td>' +
                                    '<td><span class="glyphicon glyphicon-remove remove-btn" aria-hidden="true" data-synonym-seq='+ item.synonymSeq +'></span></td>' +
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
                    var synonymSeq = $(this).data('synonymSeq');
                    console.log('edit -  synonym_seq: ' + synonymSeq );
                    var $input = $("#synonym"+synonymSeq);
                    if ($input.prop("readonly") == true) {
                        $input.prop("readonly", false);
                        $(this).hide();
                        $input.closest('td').find(".edit-ok-btn").show();
                        $input.focus();
                    }

                });
            },

            bindEdit : function(){
                var that = this;
                $(document).on('click', '.edit-ok-btn', function() {

                    var synonymSeq = $(this).data('synonymSeq');
                    var $input = $("#synonym" + synonymSeq);
                    console.log('edit ok - synonym_seq: ' + synonymSeq);

                    var synonymDTO = {};
                    synonymDTO.synonymSeq = synonymSeq;
                    synonymDTO.synonym = $input.val();

                    $.ajax({
                        url:  that.url.api + '/v1/search-admin/synonym-edit'
                        ,type: 'POST'
                        , contentType:"application/json; charset=UTF-8"
                        , data: JSON.stringify(synonymDTO)
                        , success: function (result) {
                            console.log(result);
                            alert('변경 되었습니다.\n(엔진 반영은 안된 상태)');
                            $input.prop("readonly", true);
                            $input.closest('td').find(".edit-ok-btn").hide();
                            $input.closest('td').find(".edit-btn").show();
                        }
                        ,async: false
                    });

                });
            },

            bindAdd : function() {
                var that = this;
                $("#add-btn").click(function () {
                    $("#add-modal").modal();
                });

                $("#add-synonym-btn").click(function () {

                    var $keyword = $("#synonym-keyword");

                    if ($keyword.val().trim() == '') {
                        alert('동의어를 입력해주세요.');
                        $keyword.focus();
                        return;
                    }

                    if (!confirm('등록하시겠습니끼?')) {
                        return;
                    }

                    var synonymDTO = {};
                    synonymDTO.synonym = $keyword.val().trim();

                    $.ajax({
                        url: that.url.api + '/v1/search-admin/add-synonym'
                        , type: 'POST'
                        , contentType: "application/json; charset=UTF-8"
                        , data: JSON.stringify(synonymDTO)
                        , success: function (result) {
                            console.log(result);
                            alert('등록 되었습니다.\n(엔진 반영은 안된 상태)');
                        }
                        , async: false
                    });

                    $("#modal-close-btn").click();
                });
            },

            bindDelete : function(){
                var that = this;
                $(document).on('click', '.remove-btn', function() {
                    var synonymSeq = $(this).data('synonymSeq');
                    console.log('remove - synonym_seq: ' + synonymSeq);

                    if( !confirm("삭제하시겠습니까?")){
                        return;
                    }

                    $(this).closest('tr').remove();

                    var synonymDTO = {};
                    synonymDTO.synonymSeq = synonymSeq;
                    $.ajax({
                        url:  that.url.api + '/v1/search-admin/remove-synonym'
                        ,type: 'POST'
                        , contentType:"application/json; charset=UTF-8"
                        , data: JSON.stringify(synonymDTO)
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
                $("#apply-btn").click(function(){

                    if( confirm("동의어 사전을 검색엔진에 반영하시겠습니까?") ) {
                        $.ajax({
                            url: that.url.api + '/v1/search-admin/upload-synonym'
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
            }

        })

    })(jQuery);

    $(function(){
        pmoApp.init();
    });

</script>

</body>
</html>