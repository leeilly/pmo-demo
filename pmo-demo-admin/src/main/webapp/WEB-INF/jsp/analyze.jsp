<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ include file="/WEB-INF/jsp/common/menu.jsp"%>
<!DOCTYPE html>
<html lang="ko">
<head>
    <title>pmo search demo</title>
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <!-- 부트스트랩 -->
    <link rel="stylesheet" href="https://maxcdn.bootstrapcdn.com/bootstrap/3.3.2/css/bootstrap.min.css">

</head>
<body>
<div class="container">
    <br/>
    <br/>
    <br/>
    <br/>
    <div class="form-row">
        <table>
            <tr>
                <th>분석기</th>
                <td>
                    <select class="form-control" id="analyzer-select">
                        <option value="my_analyzer" selected>nori</option>
                        <option value="ngram_analyzer">ngram</option>
                    </select>
                </td>
            </tr>
            <tr>
                <th>키워드 </th>
                <td><textarea class="form-control col-sm-5" rows="3" id="keyword"></textarea></td>
            </tr>
            <tr>
                <td></td>
                <td>
                    <button type="button" class="btn btn-sm btn-primary" id="analyze-btn">분석</button>
                    <button type="button" class="btn btn-sm btn-info" id="reset-btn">리셋</button>
                </td>
            </tr>
        </table>
    </div>
    <br/>
    <br/>
    <div class="form-row">
        <table>
            <thead>
            <tr>
                <th scope="col">분석결과</th>
            </tr>
            </thead>
            <tbody id="analyze-result">
            </tbody>
        </table>
    </div>


</div>

<script src="http://code.jquery.com/jquery.js"></script>
<script src="/static/js/lib/bootstrap.min.js"></script>
<script>

    $("#analyze-btn").click(function(){
        var keyword = $("#keyword").val();
        var analyzerName = $("#analyzer-select option:selected").val();
        $.ajax({
            url:  'http://13.124.141.46:8001/v1/index/analyze?keyword='+keyword +'&analyzerName='+analyzerName
            ,type: 'GET'
            , contentType:"application/json; charset=UTF-8"
            , success: function (result) {
                var html = '';
                $.each(result.data, function (i, item) {
                    html += '<tr>' +
                        '<td scope="row">'+(i+1)+'</td>' +
                        '<td scope="row">'+item.term+'</td>' +
                        '</tr>';
                });

                $('#analyze-result').append(html);
            }
            ,async: false
        });
    });

    $("#reset-btn").click(function(){
        $('#analyze-result').html('');
        $("#keyword").val('');

    });

</script>

</body>
</html>