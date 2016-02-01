<%@ page language="java" pageEncoding="UTF-8" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<!DOCTYPE html>
<html lang="zh-CN">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1, maximum-scale=1, user-scalable=no">
    <meta name="apple-mobile-web-app-status-bar-style" content="black-translucent"/>
    <title></title>
    <link href="${pageContext.request.contextPath }/resources/static/reset/reset.css" rel="stylesheet">
    <script type="text/javascript"
            src="${pageContext.request.contextPath }/resources/static/jquery1.8.3/jquery-1.8.3.min.js"></script>
    <script type="text/javascript" src="${pageContext.request.contextPath }/resources/static/js/right.js"></script>
    <script type="text/javascript"
            src="${pageContext.request.contextPath }/resources/static/reset/resetforget.js"></script>

</head>
<body>
<div class="header">
    <a href="${pageContext.request.contextPath }/user/login"  class="arrow"></a>

    <div class="font">重置密码</div>
    <a href="#" class="dian">
        <ul class="menu">
            <li class="menu2"><a href="#">首页</a></li>
            <li class="menu3"><a href="#">分类</a></li>
            <li class="menu4"><a href="#">搜索</a></li>
            <li class="menu5"><a href="#">购物车</a></li>
            <li class="menu1"><a href="#">我的账户</a></li>
        </ul>
    </a>
</div>
<%--********************************step 1*****************************************--%>
<div class="main1" style="display:
<c:choose>
<c:when test="${step=='CHECK_USER_EXIST'}">
        block
</c:when>
<c:otherwise>
        none
</c:otherwise>
</c:choose> ">
    <form:form modelAttribute="user" class="forget" action="${pageContext.request.contextPath}/user/resetPassword"
               method="post">
        <div class="name">
            <div class="kong2"></div>
            <span>用户名：</span><input class="text" type="text" name="username">

            <div class="kong2"></div>
        </div>

        <div class="name1">
            <div class="kong4">
                    ${loginError}
                <form:errors path="loginError"/>
            </div>
            <span>验证码：</span><input class="text" type="text" name="authCode">
            <img class="btn1" src="<spring:url value="/code/resetPassword"/>">

            <div class="kong2"></div>
        </div>

        <div class="font0">
            <p>

            <h2>密码设置技巧</h2></p>

            <p>密码设置至少6位数以上，由数字，字母和符号混合而成，安全性最高。</p>

            <p>不要和用户名太相似，这样容易被人猜到。</p>

            <p>不要用手机号，电话号码，生日，学号，身份证等个人信息。</p>

            <p>在点金子，支付宝和邮箱中设置不同的密码，以免一个人账户被盗造成其他账户同时
                被盗。</p>

            <p>请你每隔一段时间更新一次账号的密码。同时，新密码不应包括旧密码的内容，并且
                不应与旧密码相似。</p>
        </div>

        <input class="btn-clean" type="submit" value="提交">
        <%--<input class="btn-clean" type="reset" value="取消" />--%>
    </form:form>
</div>
<%--****************************step 2********************************--%>

<div class="main2" style="display:
<c:choose>
<c:when test="${step=='CHECK_PHONE_CODE'}">
        block
</c:when>
<c:otherwise>
        none
</c:otherwise>
        </c:choose>">

    <form class="forget" action="${pageContext.request.contextPath}/user/resetPassword" method="post">
        <div class="name1">
            <div class="kong2"></div>
            <span>手机号：</span>${userResult.phoneNumber}
            <div class="kong2"></div>
        </div>

        <div class="name1">
            <div class="kong4"></div>
            <span>手机验证码：</span><input class="text" type="text" name="smsCode">
            <input type="button" id="getSmsPath" class="btn1" value="获取验证码"
                   onclick="getPhoneCom(${userResult.phoneNumber})">

            <div class="kong2"></div>
        </div>

        <div class="font0">
            <p>

            <h2>密码设置技巧</h2></p>

            <p>密码设置至少6位数以上，由数字，字母和符号混合而成，安全性最高。</p>

            <p>不要和用户名太相似，这样容易被人猜到。</p>

            <p>不要用手机号，电话号码，生日，学号，身份证等个人信息。</p>

            <p>在点金子，支付宝和邮箱中设置不同的密码，以免一个人账户被盗造成其他账户同时
                被盗。</p>

            <p>请你每隔一段时间更新一次账号的密码。同时，新密码不应包括旧密码的内容，并且
                不应与旧密码相似。</p>
        </div>

        <input class="btn-clean" type="submit" value="下一步">
    </form>
</div>
<%--************************************step 3****************************************--%>

<div class="main3" style="display:
<c:choose>
<c:when test="${step=='SET_NEW_PASSWORD'}">
        block
</c:when>
<c:otherwise>
        none
</c:otherwise>
        </c:choose>">

    <form class="forget" action="${pageContext.request.contextPath}/user/resetPassword" method="post">
        <div class="name1">
            <div class="kong2"></div>
            <span>新密码：</span><input name="password" type="password" class="text" type="password">

            <div class="kong2"></div>
        </div>

        <div class="name1">
            <div class="kong3"></div>
            <span>确认密码：</span><input name="password2" class="text" type="password">

            <div class="kong2"></div>
        </div>
        <div class="font0">
            <p>

            <h2>密码设置技巧</h2></p>

            <p>密码设置至少6位数以上，由数字，字母和符号混合而成，安全性最高。</p>

            <p>不要和用户名太相似，这样容易被人猜到。</p>

            <p>不要用手机号，电话号码，生日，学号，身份证等个人信息。</p>

            <p>在点金子，支付宝和邮箱中设置不同的密码，以免一个人账户被盗造成其他账户同时
                被盗。</p>

            <p>请你每隔一段时间更新一次账号的密码。同时，新密码不应包括旧密码的内容，并且
                不应与旧密码相似。</p>
        </div>

        <input class="btn-clean" type="submit" value="提交">
    </form>
</div>

<%--*****************************************************************************--%>
<div class="main4" style="display:
<c:choose>
<c:when test="${step=='COMPLETE'}">
        block
</c:when>
<c:otherwise>
        none
</c:otherwise>
        </c:choose>">


    <div>恭喜你，重置密码成功</div>
    <br/>

    <div>
        请牢记您设置的密码。<a class="link-btn" href="${pageContext.request.contextPath}/user/login">立即登录</a>
    </div>


</div>
<%--*************************************************************************************--%>
<div class="footer">
    <div class="footer-top">
        <div class="kong1"></div>
        <a href="#" class="f1">请登录</a>
        <a href="#" class="f1">请注册</a>
        <a href="#" class="f1">客户端</a>
        <a href="#" class="f1">电脑版</a>
        <a href="#" class="f1">回顶部</a>

        <div class="kong"></div>
    </div>
    <div class="footer-bottom">
        蜀IPC备15022028号 dianjinzi, Inc. All rights reserved
    </div>
</div>


</body>
</html>