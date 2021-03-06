<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="pgt" tagdir="/WEB-INF/tags" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<form action="/payment/queryTrans" id="conditionForm">
<div class="row">
  <div class="col-md-2 col-sm-2">
    <div class="dataTables_filter">
      <label>id:<input type="search" class="form-control input-small input-inline"
                       placeholder="订单id" aria-controls="sample_3" name="orderId" value="${queryBean.orderId}">
      </label>
    </div>
  </div>
  <div class="col-md-2 col-sm-2">
    <div class="dataTables_filter">
      <label>交易类型:
        <select name="type" aria-controls="sample_3"
                class="form-control input-xsmall input-inline select2-offscreen"
                tabindex="${queryBean.paymentType}" title="">
          <option value="">所有</option>
          <option value="1" <c:if test="${queryBean.paymentType == 1}">selected</c:if> >易宝</option>
          <option value="2" <c:if test="${queryBean.paymentType == 2}">selected</c:if> >支付宝</option>
        </select> </label>
      </label>
    </div>
  </div>
  <div class="col-xs-2">
    <div class="dataTables_filter">
      <label>状态:
        <select name="state" aria-controls="sample_3"
                class="form-control input-xsmall input-inline select2-offscreen"
                tabindex="${queryBean.state}" title="">
          <option value="">所有</option>
          <option value="-1" <c:if test="${queryBean.state == -1}">selected</c:if> >支付失败</option>
          <option value="0" <c:if test="${queryBean.state == 0}">selected</c:if>>支付处理中</option>
          <option value="1"<c:if test="${queryBean.state == 1}">selected</c:if>>支付成功</option>
        </select>
      </label>
    </div>
  </div>
  <div class="col-xs-3">
    <div class="dataTables_filter">
      <label>交易号:<input type="search" class="form-control input-small input-inline"
                        placeholder="订单id" aria-controls="sample_3" name="trackNo" value="${queryBean.trackNo}">

      </label>
    </div>
  </div>
</div>
<div class="row">
  <div class="col-xs-10">
    <div class="dataTables_filter">
      <div class="row">
        <div class="col-xs-1 pgt-time-tittle">
          <span>时间: </span>
        </div>
        <div class="col-xs-2 pgt-begin-date" style="position: relative;width: 180px">
          <input name="startTime" class="timePicker jcDateIco form-control input-small input-inline" style="width: 180px" value="<fmt:formatDate value="${queryBean.startTime}" pattern="MM/dd/yyyy HH:mm:ss"/>">
        </div>
        <div class="col-xs-2 pgt-time" style="position: relative">
          <!--
          <input type="search" class="form-control input-mini input-inline"
                 placeholder="时" aria-controls="sample_3">
          :
          <input type="search" class="form-control input-mini input-inline"
                 placeholder="分" aria-controls="sample_3">-->
        </div>

        <div class="col-xs-1 pgt-date-divide">
          <span>至</span>
        </div>
        <div class="col-xs-2">
          <input name="endTime" class="timePicker jcDateIco form-control input-small input-inline" value="<fmt:formatDate value="${queryBean.endTime}" pattern="MM/dd/yyyy HH:mm:ss"/>">
        </div>
        <div class="col-xs-2 pgt-time">
          <!--
         <input type="search" class="form-control input-mini input-inline"
                placeholder="时" aria-controls="sample_3">
         :
         <input type="search" class="form-control input-mini input-inline"
                placeholder="分" aria-controls="sample_3">-->
       </div>
     </div>
   </div>
 </div>
 <div class="col-xs-1">
   <button class="btn blue" id="conditionSumbitButton">
     搜索
   </button>
     <button class="btn blue" id="reportSumbitButton" data-action="/payment/generateReport">
         生成报表
     </button>
 </div>
</div>
    <input type="hidden" name="currentIndex" id="currentIndex" value="${paginationBean.currentIndex}"/>
    <input type="hidden" name="capacity" id="capacity" value="${paginationBean.capacity}"/>
</form>