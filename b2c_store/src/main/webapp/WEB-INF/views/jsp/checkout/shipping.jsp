<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<spring:url value="${juedangpinStaticPath}" var="staticPath"/>
<c:set var="isDelivery" value="${'PICKUP' ne checkoutOrder.shippingVO.shippingType}"></c:set>
<c:set var="selectedAddress" value="${checkoutOrder.shippingVO.shippingAddress}"></c:set>
<c:set var="selectedPickup" value="${checkoutOrder.shippingVO.shippingMethod}"></c:set>
<c:set var="currentAddress" value="${empty selectedAddress? defaultAddress: selectedAddress}"/>
<c:set var="hasStores" value="${not empty storeList}"/>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <title>点金子典当行绝当品销售平台</title>
    <link rel = "Shortcut Icon" href="<spring:url value="${juedangpinStaticPath}/common/logo.png"/>">
    <link rel="stylesheet" href="${staticPath}/shopping-cart/cart.css"/>
</head>
<body>
<!--主头部-->
<div class="header" id="header">
    <div class="top-box">
        <div class="top-status">
            <div class="status-box">
                <ul class="top-nav">
                    <li>
                        <a href="#">手机绝当品</a>
                    </li>
                    <li><a href="<spring:url value="${urlConfiguration.helpCenterPage}"/>">帮助中心</a></li>
                    <li><a href="#">网站导航</a></li>
                </ul>
                <ul class="have-login">
                    <li>
                        <a href="<spring:url value="${urlConfiguration.myAccountPage}"/>"><span> 欢迎您：</span><span>${currentUser.username}</span></a>
                    </li>
                    <li><a href="<spring:url value="${urlConfiguration.myAccountPage}"/>">账户管理</a></li>
                    <li><a href="<spring:url value="${urlConfiguration.logoutPage}"/>">退出登录</a></li>
                </ul>
            </div>
        </div>
        <div class="top-banner">
            <div class="top-banner-box">
                <a href="#"></a>
            </div>
        </div>
    </div>
    <div class="logo-box">
        <h1>
            <a href="..${urlConfiguration.shoppingCartPage}">
                购物车
                <div class="light"></div>
            </a>
        </h1>
        <ul id="step" class="step2">
            <li>
                <h3 class="step1-text">我的购物车</h3>
            </li>
            <li>
                <h3 class="step2-text">确认订单</h3>
            </li>
            <li>
                <h3 class="step3-text">选择支付方式</h3>
            </li>
            <li>
                <h3 class="step4-text">支付成功</h3>
            </li>
        </ul>
    </div>
</div>

<!--正文-->
<div class="content-box">
    <div class="content-2"  id="content2" style="display: block">
        <div class="content-title">
            <h2>确认订单</h2>
            <div class="back-last">
                <a class="link-btn" href="..${urlConfiguration.shoppingCartPage}">
                    返回购物车 >
                </a>
            </div>
        </div>

        <div class="error" <c:if test="${ empty error}">style="display: none;"</c:if> >
            <c:if test="${error eq 'HAS.UNSUBMIT.ORDER'}">
                <span>您有尚未支付的订单，请到账户管理 -> 我的订单页面继续支付</span>
            </c:if>

        </div>

        <form id="addPickup" class="post-way" action="addPickup">
            <h3>配送方式</h3>
            <fieldset>
                <div class="row">
                    <input id="js-delivery-shipping" class="${isDelivery ? 'd-btn': 'l-btn'}" type="button" data-shipping-type="DELIVERY" value="顺丰包邮"/>
	                <c:if test="${hasStores}">  
	                  <input id="js-pickup-shipping" class="${!isDelivery ? 'd-btn': 'l-btn'}" type="button" data-shipping-type="PICKUP" value="上门自提"/>
	                </c:if>
                </div>
                <div class="row" id="js-selected-delivery-info" ${isDelivery ? '':'style="display:none"'}>
                    <span>您的选择是:</span>
                    	<span>顺丰包邮</span>
                    	<span>
	                    	<c:if test="${not empty selectedAddress }">
	                    		${selectedAddress.province}${selectedAddress.city}${selectedAddress.district} ${selectedAddress.address} ${selectedAddress.phone} ${selectedAddress.name}
	                    	</c:if>
	                    </span>
                    <a class="link-btn js-change-shipping" data-is-delivery="${isDelivery}" href="javascript:void(0)" ${empty selectedAddress.id ? 'style="display:none"' : ''}>更改</a>
                </div>
                <c:if test="${hasStores}">
                <div class="row" id="js-selected-pickup-info" ${isDelivery ? 'style="display:none"' : ''}>
                    <span>您的选择是:</span>
                   	<span>上门自提</span>
                   	<span>  
                    	<c:forEach items="${storeList}" var="store">
                    		<c:if test="${store.id eq selectedPickup.locationId }">
                    			${store.address } ${store.phone } ${checkoutOrder.shippingVO.shippingMethod.locationId }
                    		</c:if>
                    	</c:forEach>
                    </span>

                </div>
                </c:if>
                <div class="drop-area" style="display:none">
                    <div class="inner">
                        <div class="row">
                            <span>顺丰包邮</span>
                            <span>预计12月3日到达</span>
                        </div>
                        <div class="row">
                            <input class="d-btn" type="button" value="确定"/>
                            <input class="l-btn" type="button" value="取消"/>
                        </div>
                    </div>
                </div>
                <c:if test="${hasStores}">
                <div id="js-pickup-area" class="drop-area" ${isDelivery || not empty selectedPickup ? 'style="display:none"': ''}>
                    <div class="inner">
                        <p class="choose-has">
                            <a class="link-btn" href="#">查看全部自提点</a>
                        </p>
                        <div class="row">
                            <label>
                                姓名
                                <input type="text" name="name" maxlength="50" value="${selectedPickup.name}"/>
                            </label>
                            <label>
                                手机号码
                                <input type="text" name="phone" maxlength="11" value="${selectedPickup.phone}"/>
                            </label>
                        </div>
                        <div class="row">
                            <h4>请选择自提点:</h4>
                            <c:forEach items="${storeList}" var="store">
                            	<p class="get-self">
	                                <input name="locationId" type="radio" value="${store.id }" ${store.id eq selectedPickup.locationId ? 'checked':'' }/>
	                              ${store.address }
	                                <span>联系电话: ${store.phone }</span>
	                            </p>
                            </c:forEach>
                        </div>
                        <div class="row">
                            <input type="hidden" name="orderId" value="${checkoutOrder.id }"/>
                            <input id="js-add-pickup" class="d-btn" type="button" value="确定"/>
                            <input class="l-btn" type="button" value="取消"/>
                        </div>
                    </div>
                </div>
                </c:if>
            </fieldset>
        </form>
        <form id="addAddressForm" class="address" action="../my-account/person-info/addAddress" ${not empty selectedAddress ? 'style="display:none"':'' }>
            <h3>收货地址</h3>
            <fieldset>

                <p class="choose-has">
                    <a id="show-address-form" class="link-btn" href="javascript:void(0)">新增配送地址</a>
                </p>

                <!--<p class="choose-has" ${empty defaultAddress ? 'style="display:none"':''}>-->
                <!--<a id="show-address-form" class="link-btn" href="javascript:void(0)">新增配送地址</a>-->
                <!--</p>-->
                <c:forEach items="${addressInfoList }" var="addressInfo">
                	<div class="row">
	                    <input type="radio" class="js-address-item" name="addressInfoId" ${addressInfo.id eq selectedAddress.addressInfoId ? 'checked' :''} value="${addressInfo.id}"/>
	                    <span>${addressInfo.name} ${addressInfo.phone} ${addressInfo.province}${addressInfo.city}${addressInfo.district} ${addressInfo.address}</span>
                        <a class="link-btn js-update-address" href="javascript:void(0)"
                           data-href="../my-account/person-info/updateAddress/${addressInfo.id}"
                           data-find-adress-url="/my-account/person-info/findAddress/${addressInfo.id}">修改</a>
	                    <a class="link-btn js-delete-address" href="javascript:void(0)" data-href="../my-account/person-info/deleteAddress/${addressInfo.id}">删除</a>
	                </div>
                </c:forEach>

            </fieldset>

        </form>
		<form id="addAddressToOrder" action="addAddressToOrder" method="post" style="display:none">
			<input type="hidden" name="orderId" value="${checkoutOrder.id }"/>
			<input id="addressInfoId" name="addressInfoId" value="">
			<input id="js-add-address-to-order" type="button"/>
		</form>
        <c:if test="${empty addressInfoList}">
            <span style="color: red; float: right;margin: 5px 0px 0px 0px">请输入收货地址</span>
        </c:if>
        <form class="product-info">
            <h3>商品信息</h3>
            <fieldset>
                <table>
                    <tbody>
	                    <c:forEach var="commerceItem" items="${checkoutOrder.commerceItems}">
		                    <tr>
		                        <td class="img-box">
                                    <c:if test="${not commerceItem.inStock}">
                                        <c:set var="invalidItemCount" value="${invalidItemCount + 1}" />
                                        <img src="${pageContext.request.contextPath}/resources/juedangpin/core/images/productList/out-of-stock-s.png"
                                             class="out-of-stock"/>
                                    </c:if>
                                    <img src="${pageContext.request.contextPath}/resources${commerceItem['snapshotMedia']['path']}"
	                                         alt="${empty commerceItem['snapshotMedia']['title'] ? commerceItem.name : commerceItem['snapshotMedia']['title']}" />
		                        </td>
		                        <td class="product-name">
		                            ${commerceItem.name}
		                        </td>
		                        <td>
		                            <span class="level">十成</span>
		                        </td>
		                        <td>
		                            有货
		                        </td>
		                        <td class="product-now-cost">
		                            ¥<span><fmt:formatNumber value="${commerceItem.salePrice}" pattern="0.00" type="number" /></span>
		                        </td>
		                    </tr>
	                    </c:forEach>
                    </tbody>
                </table>

            </fieldset>
        </form>

        <form action="redirectToPayment" method="get">
	        <div class="sub-box">
	        	<input type="hidden" name="orderId" value="${checkoutOrder.id }"/>
	            <input class="d-btn" type="submit" value="提交订单"/>
                <input class="l-btn" type="button" value="返回我的购物车"
                       onclick="window.location.href='..${urlConfiguration.shoppingCartPage}'"/>
	        </div>
        </form>

    </div>

<jsp:include page="../shopping-cart/horizontal-recommend-bar.jsp" />
<!--主脚部-->
<jsp:include page="../core/footer-main.jsp"></jsp:include>

<jsp:include page="../core/baidu.jsp"></jsp:include>

</body>
<!-- 弹出框-->
<!--<div id="popUp" class="pop-up">-->
<!--<div class="inner">-->
<!--<h3>-->
<!--<span id="popTitle" class="pop-title">填写收货信息</span>-->
<!--<span id="popClose" class="close">X</span>-->
<!--</h3>-->
<!--<form id="popForm" class="pop-content" action="../my-account/person-info/addAddress">-->

<!--<div class="row1">-->
<!--<label for="#">-->
<!--<span class="must-write">*</span>&lt;!&ndash;-->
<!--&ndash;&gt;<span>收货人:</span>-->
<!--<span class="pop-tips"></span>-->
<!--</label>-->

<!--<div class="text">-->
<!--<input type="text" name="name" maxlength="50"/>-->
<!--</div>-->
<!--</div>-->

<!--<div class="row2">-->
<!--<label for="#">-->
<!--<span class="must-write">*</span>&lt;!&ndash;-->
<!--&ndash;&gt;<span>所在区域:</span>-->
<!--<span class="pop-tips"></span>-->
<!--</label>-->

<!--<div class="text">-->
<!--<div class="province">-->
<!--<a id="province" class="select-view"  href="#">-->
<!--<span class="selected">请选择</span>-->
<!--<i class="foundicon-down-arrow"></i>-->
<!--</a>-->
<!--<ul class="options">-->
<!--<c:forEach items="${provinceList}" var="province">-->
<!--<li><a class="option-view" data-value="${province.id}" href="#">${province.name}</a></li>-->
<!--</c:forEach>-->
<!--</ul>-->
<!--<input class="select-value" name="province" type="hidden" value=""/>-->
<!--</div>-->
<!--省-->
<!--<div class="city" >-->
<!--<a id="city" class="select-view"  href="#">-->
<!--<span class="selected">请选择</span>-->
<!--<i class="foundicon-down-arrow"></i>-->
<!--</a>-->
<!--<ul class="options">-->

<!--</ul>-->
<!--<input class="select-value" name="city" type="hidden" value=""/>-->
<!--</div>-->
<!--市-->
<!--<div class="country" >-->
<!--<a id="country" class="select-view"  href="#">-->
<!--<span class="selected">请选择</span>-->
<!--<i class="foundicon-down-arrow"></i>-->
<!--</a>-->
<!--<ul class="options">-->

<!--</ul>-->
<!--<input class="select-value" name="district" type="hidden" value=""/>-->
<!--</div>-->
<!--区\县-->
<!--</div>-->
<!--</div>-->

<!--<div class="row3">-->
<!--<label for="#">-->
<!--<span class="must-write">*</span>&lt;!&ndash;-->
<!--&ndash;&gt;<span>详细地址:</span>-->
<!--<span class="pop-tips"></span>-->
<!--</label>-->

<!--<div class="text">-->
<!--<input type="text" name="address" maxlength="100"/>-->
<!--</div>-->
<!--</div>-->

<!--<div class="row4">-->
<!--<label for="#">-->
<!--<span class="must-write">*</span>&lt;!&ndash;-->
<!--&ndash;&gt;<span>手机号码:</span>-->
<!--<span class="pop-tips"></span>-->
<!--</label>-->

<!--<div class="text">-->
<!--<input type="text" name="phone" maxlength="11"/>-->
<!--</div>-->
<!--</div>-->

<!--<div class="row5">-->
<!--<label for="#">-->
<!--<span>固定号码:</span>-->
<!--<span class="pop-tips"></span>-->
<!--</label>-->

<!--<div class="text">-->
<!--<input type="text" name="telephone" maxlength="20"/>-->
<!--</div>-->
<!--</div>-->

<!--<div class="row6">-->
<!--<label for="#">-->
<!--<span>邮箱:</span>-->
<!--<span class="pop-tips"></span>-->
<!--</label>-->

<!--<div class="text">-->
<!--<input type="text" name="email" maxlength="100"/>-->
<!--</div>-->
<!--</div>-->


<!--<div class="row7">-->
<!--<input id="popSubmit"  class="d-btn" type="button" value="确定"/>-->
<!--<input id="popReset" class="l-btn" type="reset" value="取消"/>-->
<!--</div>-->
<!--</form>-->
<!--</div>-->
<!--</div>-->

<div id="popUp" class="pop-up">
    <div class="inner">
        <h3>
            <span id="popTitle" class="pop-title">填写收货信息</span>
            <span id="popClose" class="close">X</span>
        </h3>
        <form id="popForm" class="pop-content" action="/my-account/person-info/addAddress">

            <div class="row1">
                <label for="#">
                    <span class="must-write">*</span><!--
                            --><span>收货人:</span>
                    <span class="pop-tips"></span>
                </label>

                <div class="text">
                    <input type="text" name="name" maxlength="50"/>
                </div>
            </div>

            <div class="row2">
                <label for="#">
                    <span class="must-write">*</span><!--
                            --><span>所在区域:</span>
                    <span class="pop-tips"></span>
                </label>

                <div class="text">
                    <!-- 仿select-->
                    <div class="province">
                        <a id="province" class="select-view" href="#">
                            <span class="selected">请选择</span>
                            <i class="foundicon-down-arrow"></i>
                        </a>
                        <ul class="options">
                            <c:forEach items="${provinceList}" var="province">
                                <li><a class="option-view" data-value="${province.id}" href="#">${province.name}</a>
                                </li>
                            </c:forEach>
                        </ul>
                        <input class="select-value" name="province" type="hidden" value=""/>
                    </div>
                    省

                    <!-- 仿select-->
                    <div class="city">
                        <a id="city" class="select-view" href="#">
                            <span class="selected">请选择</span>
                            <i class="foundicon-down-arrow"></i>
                        </a>
                        <ul class="options">

                        </ul>
                        <input class="select-value" name="city" type="hidden" value=""/>
                    </div>
                    市

                    <!-- 仿select-->
                    <div class="country">
                        <a id="country" class="select-view" href="#">
                            <span class="selected">请选择</span>
                            <i class="foundicon-down-arrow"></i>
                        </a>
                        <ul class="options">
                        </ul>
                        <input class="select-value" name="district" type="hidden" value=""/>
                    </div>
                    区/县
                </div>
            </div>

            <div class="row3">
                <label for="#">
                    <span class="must-write">*</span><!--
                            --><span>详细地址:</span>
                    <span class="pop-tips"></span>
                </label>

                <div class="text">
                    <input type="text" name="address" maxlength="100"/>
                </div>
            </div>

            <div class="row4">
                <label for="#">
                    <span class="must-write">*</span><!--
                            --><span>手机号码:</span>
                    <span class="pop-tips"></span>
                </label>

                <div class="text">
                    <input type="text" name="phone" maxlength="11"/>
                </div>
            </div>

            <div class="row5">
                <label for="#">
                    <span>固定号码:</span>
                    <span class="pop-tips"></span>
                </label>

                <div class="text">
                    <input type="text" name="telephone" maxlength="20"/>
                </div>
            </div>

            <div class="row6">
                <label for="#">
                    <span>邮箱:</span>
                    <span class="pop-tips"></span>
                </label>

                <div class="text">
                    <input type="text" name="email" maxlength="100"/>
                </div>
            </div>

            <div class="row7">
                <input id="popSubmit" class="d-btn" type="button" value="确定"/>
                <input id="popReset" class="l-btn" type="reset" value="取消"/>
            </div>
        </form>
    </div>
</div>

<script src="${staticPath}/core/js/require.js" defer async="true" data-main="${staticPath}/shopping-cart/shipping.js"></script>

</html>