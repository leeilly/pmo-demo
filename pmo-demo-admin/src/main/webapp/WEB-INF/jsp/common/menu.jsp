<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>


<!DOCTYPE html>
<html lang="ko">
<head>
    <title>pmo search demo</title>
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <!-- 부트스트랩 -->
    <link rel="stylesheet" href="/static/bootstrap/css/bootstrap.min.css">
<%--    <link rel="stylesheet" href="https://maxcdn.bootstrapcdn.com/bootstrap/3.3.2/css/bootstrap.min.css">--%>
    <!-- 부가적인 테마 -->
    <link href="/static/bootstrap/css/bootstrap-theme.min.css" rel="stylesheet" media="screen">
</head>
<body>
<nav class="navbar navbar-inverse navbar-fixed-top">
    <div class="navbar-header">
        <button type="button" class="navbar-toggle collapsed" data-toggle="collapse" data-target="#navbar" aria-expanded="false" aria-controls="navbar">
            <span class="sr-only">Toggle navigation</span>
            <span class="icon-bar"></span>
            <span class="icon-bar"></span>
            <span class="icon-bar"></span>
        </button>
        <a class="navbar-brand" href="#">풀무원 검색 관리</a>
    </div>
    <div id="navbar" class="collapse navbar-collapse">
        <ul class="nav navbar-nav">
            <li><a href="/">메인</a></li>
            <li><a href="/search-demo/ac">검색 결과 확인</a></li>
            <li><a href="/analyze">형태소 분석 확인</a></li>
            <li><a href="/boosting">카테고리 부스팅</a></li>
            <li><a href="/dic/synonym">사용자 정의 사전 관리</a></li>
            <li><a href="/indexing">색인 관리</a></li>
        </ul>
    </div><!--/.nav-collapse -->
</nav>
</body>
</html>