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
    <h3>색인 관리</h3>
    <br/>
    <br/>
    <div class="form-row">
        <div>
            <div class="alert alert-warning alert-dismissible" role="alert">
                <p>* 색인을 새로 생성하면, 검색결과에 <b>즉시</b> 영향을 줍니다.<br/>
                   * ngram 방식만을 사용할 수도 있지만, <b>동의어 사전을 사용하기 위해서는 '한글형태소분석기'를 사용</b>해야 합니다.<br/>
                </p>
            </div>
            <hr/>
            <div class="form-group row">
                <div>
                    <label for="analyzer_radio_nori" class="col-sm-2 col-form-label">상품 색인</label>
                    <div class="col-sm-10">
                        <button type="button" class="btn btn-sm btn-success" id="indexing-btn">상품 색인</button>
                        <input type="radio" id="analyzer_radio_nori" value="my_analyzer" name="analyzer_radio" style="margin-left: 50px;" checked> 한글형태소분석(nori) + nGram <b>(권장)</b>
                        <input type="radio" id="analyzer_radio_ngram" value="ngram_analyzer" name="analyzer_radio" style="margin-left: 30px;"> nGram
                        <button type="button" class="btn btn-sm btn-primary" id="template-btn" style="margin-left: 30px;">상품 색인 템플릿 변경</button>
                    </div>
                </div>
                <hr/>
                <hr/>
                <div>
                    <label for="ac-indexing-btn" class="col-sm-2 col-form-label">자동완성 색인</label>
                    <div class="col-sm-10">
                        <button type="button" class="btn btn-sm btn-success" id="ac-indexing-btn" >자동완성 색인</button>
                    </div>
                </div>

            </div>
            <hr/>
            </div>
            <br/>
            <br/>
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
                that.bindTemplateChange();
                that.bindIndexing();
                that.bindAutoCompleteIndexing();
            },

            bindTemplateChange : function(){
                var that = this;
                $('#template-btn').click(function () {

                    if(!confirm("색인 템플릿을 변경하시겠습니까?")){
                        return;
                    }
                    var analyzerType = $(":radio[name='analyzer_radio']:checked").val();
                    console.log(analyzerType);

                    var param = '?analyzerType=' + analyzerType;
                    $.ajax({
                        url: that.url.api + '/v1/index/change-template' + param
                        , type: 'GET'
                        , contentType: "application/json; charset=UTF-8"
                        , success: function (result) {
                            alert('템플릿이 변경 되었습니다.');
                        }
                        , async: false
                    });
                });
            },

            bindIndexing : function(){
                var that = this;
                $('#indexing-btn').click(function () {

                    if(!confirm("[상품] 전체색인을 진행하시겠습니까?")){
                        return;
                    }
                    var param = '?type=product';
                    $.ajax({
                        url: that.url.api + '/v1/index/bulk' + param
                        , type: 'GET'
                        , contentType: "application/json; charset=UTF-8"
                        , success: function (result) {
                            alert('[상품] 전체 색인이 완료되었습니다.');
                            console.log(result);
                        }
                        , async: false
                    });
                });

            },

            bindAutoCompleteIndexing : function(){
                var that = this;
                $('#ac-indexing-btn').click(function () {

                    if(!confirm("[자동완성] 색인을 진행하시겠습니까?")){
                        return;
                    }
                    var param = '?type=ac';
                    $.ajax({
                        url: that.url.api + '/v1/index/bulk' + param
                        , type: 'GET'
                        , contentType: "application/json; charset=UTF-8"
                        , success: function (result) {
                            alert('[자동완성] 전체 색인이 완료되었습니다.');
                            console.log(result);
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