<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<!DOCTYPE html>
<html lang="en">
<head>

    <meta charset="UTF-8">
    <title>点金子典当行绝当品销售平台</title>
    <link rel="Shortcut Icon" href="/resources/common/logo.png">
    <link rel="stylesheet" href="/resources/my-account/my_account_base.css"/>
    <link rel="stylesheet" href="/resources/my-account/my-property/my-property.css"/>


</head>
<body>

<!--主头部-->
<jsp:include page="../../core/header-login.jsp"/>

<!--正文-->
<div class="content">
    <jsp:include page="../vertical-my-account-directory.jsp"/>
    <div class="main-area">
        <h2 class="main-head">我的资产</h2>
        <!-- super: will-binding和already-binding,根据不同的情况显示和隐藏-->
        <div class="already-binding" style="display: block;">
            <div class="already-row-1">
                <span></span>
                <span class="yeepay-username">跳转至易宝中......请等待</span>
            </div>
        </div>
        <form action="/yeepay/getSgin" method="post" id="yeepayDataForm" auto-submit="Y" style="display:none">
            <input name="platformUserNo" type="text" value="${platformUserNo }" /><br/>
            <input name="serviceName" type="hidden" value = "${serviceName }"/>
            <input type="submit" value="提交"/>
        </form>

        <jsp:include page="include/yeepayGatewayForm.jsp"></jsp:include>
        <!-- person-info-box end-->
    </div>
</div>

<!--主脚部-->
<jsp:include page="../../core/footer-main.jsp"></jsp:include>
<jsp:include page="../../core/baidu.jsp"></jsp:include>
</body>
<script src="/resources/core/js/jquery.min.js"></script>
<script src="/resources/core/js/jquery.form.js"></script>
<script src="/resources/core/js/require.js"
        data-main="/resources/yeepay/gateway.js"></script>

</html>