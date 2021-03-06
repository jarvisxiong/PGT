<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%--
  Created by IntelliJ IDEA.
  User: carlwang
  Date: 11/16/15
  Time: 12:20 AM
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<div class="top-box">
    <div class="top-status">
        <div class="status-box">
            <ul class="top-nav">
                <li>
                    <a href="#">手机绝当品</a>
                </li>
                <li><a href="help/helpCenter">帮助中心</a></li>
                <li><a href="#">网站导航</a></li>
            </ul>

            <c:choose>
                <c:when test="${currentUser==null}">
                    <ul class="will-login">
                        <li><a href="<spring:url value="${urlConfiguration.loginPage}"/>">立即登录</a></li>
                        <li><a href="<spring:url value="${urlConfiguration.registerPage}"/>">免费注册</a></li>
                    </ul>
                </c:when>
                <c:otherwise>
                    <ul class="have-login">
                        <li>
                            <a href="<spring:url value="${urlConfiguration.myAccountPage}"/>"><span> 欢迎您：</span><span>${currentUser.username}</span></a>
                        </li>
                        <li><a href="<spring:url value="${urlConfiguration.myAccountPage}"/>">账户管理</a></li>
                        <li><a href="<spring:url value="${urlConfiguration.logoutPage}"/>">退出登录</a></li>
                    </ul>
                </c:otherwise>
            </c:choose>
        </div>
    </div>
    <c:if test="${copyWriter!=null}">
        <div class="top-banner">
            <div class="top-banner-box">
                <a href="#">
                    <img src="<spring:url value="${juedangpinStaticPath}${copyWriter.path}"/>" alt="#" />
                </a>
            </div>
        </div>
    </c:if>
</div>
<div class="head-box">
    <div class="logo-box">
        <h1>
            <a href="<spring:url value="${urlConfiguration.homePage}"/>">
            点金子绝当品——欢迎登录
            <img src="../core/images/header/images/big-logo_pig.jpg" alt="#"/>
            <div class="golds"></div>
            <div class="light"></div>
            </a>
    </h1>
    </div>
    <div class="search-box">
        <form action="${pageContext.request.contextPath}/essearch">
            <input class="search-text" type="text" name="term" value="${term}" /><input class="search-sub" type="submit" value="搜索" />
        </form>
        <ul class="search-tips" id="searchTips">
            <li>钻石饰品</li>
            <li>文玩杂项</li>
            <li>玉石类</li>
            <li>琥珀蜜蜡</li>
            <li>木制品</li>
        </ul>
        <div class="hot-words">

            <c:forEach items="${hotSearchList}" var="hotSearch" varStatus="status">
                <a class="" href="${pageContext.request.contextPath}/essearch?term=${hotSearch.term}">
                    ${hotSearch.term}
                </a>
            </c:forEach>
        </div>
    </div>
    <div class="cart-box">
        <a class="cart" href="<spring:url value="/shoppingCart/cart" />">
            <i class="foundicon-cart"></i>
            <span>我的购物车</span>
            <span id="cartCount" class="cart-count">${empty order.commerceItemCount ? 0 : order.commerceItemCount}</span>
        </a>
    </div>
</div>
<div id="fixedHead" class="fixed-head-box">
    <div class="fixed-head">
        <div class="logo-box">
            <h1>
                <a href="<spring:url value="${urlConfiguration.homePage}"/>">
                    <img src="<spring:url value="${juedangpinStaticPath}/core/images/header/logo.png"/>" alt="点金子名品" />
                </a>
            </h1>
        </div>
        <div class="search-box">
            <form action="${pageContext.request.contextPath}/essearch"><input class="search-text" type="text" name="term" value="${term}" /><input class="search-sub" type="submit" value="搜索" /></form>
        </div>
        <div class="cart-box">
            <a class="cart" href="<spring:url value="/shoppingCart/cart" />">
                <i class="foundicon-cart"></i>
                <span>我的购物车</span>
                <span id="fixedCartCount" class="cart-count">${empty order.commerceItemCount ? 0 : order.commerceItemCount}</span>
            </a>
        </div>
    </div>
</div>


<div class="nav-box">

    <div class="menu">
        <a class="menu-head" href="<spring:url value="/"/>">
            <span>回到首页</span>
        </a>

        <ul id="menuList" class="menu-list">
            
             <c:forEach items="${rootHomeCategories}" var="category">
                <li>
                    <a class="menu-list-tittle"
                       href="${pageContext.request.contextPath}/essearch?rootCategoryId=${category.id}">${category.name}</a>

                    <div class="menu-list-detail">
                        <c:forEach items="${category.children}" var="subCategory">
                            <a href="${pageContext.request.contextPath}/essearch?parentCategoryId=${subCategory.id}">${subCategory.name}</a>
                        </c:forEach>
                    </div>
                </li>
            </c:forEach>

        </ul>
    </div>

    <a href="http://www.dianjinzi.com" target="_blank" class="hottest">
        <h1>点石成金即将上线！</h1>
    </a>

    <div class="nav">
        <ul>
            <c:forEach var="category" items="${rootHomeCategories}" begin="0" end="4">
                <li><a href="${pageContext.request.contextPath}/essearch?rootCategoryId=${category.id}">${category.name}</a>
                </li>
            </c:forEach>
        </ul>
    </div>
</div>

