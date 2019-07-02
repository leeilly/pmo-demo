<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ include file="/WEB-INF/jsp/common/menu.jsp"%>
<!DOCTYPE html>
<html lang="ko">
<head>
    <title>pmo search demo</title>
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <style>
        em {
            color: red;
        }
    </style>
</head>
<body>
<div class="container">
    <h3>검색 결과 확인</h3>
    <br/>
    <div>
        <ul class="nav nav-tabs">
            <li class="active"><a href="/search-demo/ac">자동완성 검색</a></li>
            <li><a href="/search-demo">상품 리스팅 검색</a></li>
        </ul>
    </div>
    <br/>
    <br/>
    <div class="alert alert-warning alert-dismissible" role="alert">
        <p>* <b>자동완성 매칭 방식</b>을 확인 하고, 조정 할 수 있습니다.<br/>
           * 단, 색인에 의해 조정되는 부분이 아니고, 검색 쿼리를 변경해야 하는 부분이기 때문에 <b>Front 검색 결과에 반영하기 위해서는 서버 재기동</b>이 필요합니다. <br/>
           * 어떤 방식이 더 좋을지 확인한 후, 개발팀에 반영 요청할 수 있습니다.
        </p>
    </div>
    <div class="form-row">
        <div>
            <hr/>
            <h4>검색 방식</h4>
            <div class="row">
                <div class="col-sm-12 col-md-6">
                    <br/>
                    <div class="thumbnail">
                        <div class="caption">
                            <div class="thumbnail">
                                <p>달콤한 단<span style="color:red;">감</span> 한상자</p>
                                <p>생면식<span style="color:red;">감</span> 꽃게탕면 (4개입)</p>
                                <p>새콤달콤 제주도 <span style="color:red;">감</span>귤</p>
                                <p><span style="color:red;">감</span>자 샐러드 (100g)</p>
                            </div>
                            <hr/>
                            <h5>Type1: Any Match</h5>
                            <div class="alert alert-info alert-dismissible" role="alert">
                                <p>
                                    * 위 예시처럼, 문장의 어느 포지션이든 검색한 키워드가 존재하면 결과로 노출됩니다.<br/>
                                    * 인기도 점수를 조정하여 정렬 순서를 바꿀 수도 있습니다.<br/>
                                </p>
                            </div>
                            <br/>
                            <div class="input-append">
                                <input class="span2 .search-keyword" id="any-match-search-keyword" type="text">
                                <button type="button" class="btn btn-sm btn-primary any-match-search-btn" id="any-match-search-btn">검색</button>
                            </div>
                            <div id="autocomplete-search-result">
                            </div>
                        </div>
                    </div>
                </div>
                <div class="col-sm-12 col-md-6">
<%--                    <input type="radio" id="ac_template_radio_2" value="my_analyzer" name="ac_template_radio"> default--%>
                    <br/>
                    <div class="thumbnail">
                        <div class="caption">
                            <div class="thumbnail">
                                <p>새콤달콤 제주도 <span style="color:red;">감</span>귤</p>
                                <p>쫀득한 청도반시 <span style="color:red;">감</span>말랭이(저탄소,400g)</p>
                                <p><span style="color:red;">감</span>자 샐러드 (100g)</p>
                                <p>..</p>
                            </div>
                            <hr/>
                            <h5>Type2: Prefix</h5>
                            <div class="alert alert-info alert-dismissible" role="alert">
                                <p>
                                    * 공백기준으로 분리된 단어의 '앞머리'에 키워드가 존재하면, 결과로 노출됩니다.<br/>
                                    * 인기도 점수를 조정하여 정렬 순서를 바꿀 수도 있습니다.<br/>
                                </p>
                            </div>
                            <br/>
                            <div class="input-append">
                                <input class="span2 .search-keyword" id="prefix-match-search-keyword" type="text">
                                <button type="button" class="btn btn-sm btn-primary prefix-match-search-btn" id="prefix-match-search-btn">검색</button>
                            </div>
                            <div id="autocomplete-search-result-prefix">
                            </div>
                        </div>
                    </div>
                </div>
            </div>

            <hr/>
            <div>
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
                that.bindAcSearch();
            },

            bindAcSearch : function(){
                var that = this;

                $('#any-match-search-keyword').keyup(function(){
                    var keyword = $(this).val();

                    if( keyword.length <= 0 ) return false;

                    $.ajax({
                        url:  that.url.api + '/v1/search/product/auto_complete?keyword='+keyword+'&matchingType=any'
                        ,type: 'GET'
                        , contentType:"application/json; charset=UTF-8"
                        , success: function (result) {
                            //console.log(result.data.searchResult);

                            $.each(result.data.searchResult, function (i, item) {
                                $('#autocomplete-search-result').append(item.highlight + ': ' + item.score + '<br/>');
                            })
                        }
                        ,async: false
                    });
                });

                $('#any-match-search-keyword').keydown(function(){
                    $('#autocomplete-search-result').text('');

                });



                $('#prefix-match-search-keyword').keyup(function(){
                    var keyword = $(this).val();

                    if( keyword.length <= 0 ) return false;

                    $.ajax({
                        url:  that.url.api + '/v1/search/product/auto_complete?keyword='+keyword+'&matchingType=prefix'
                        ,type: 'GET'
                        , contentType:"application/json; charset=UTF-8"
                        , success: function (result) {
                            //console.log(result.data.searchResult);

                            $.each(result.data.searchResult, function (i, item) {
                                $('#autocomplete-search-result-prefix').append(item.highlight + ': ' + item.score + '<br/>');
                            })
                        }
                        ,async: false
                    });
                });

                $('#prefix-match-search-keyword').keydown(function(){
                    $('#autocomplete-search-result-prefix').text('');

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