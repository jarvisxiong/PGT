<%--
  Created by IntelliJ IDEA.
  User: carlwang
  Date: 12/25/15
  Time: 10:17 AM
  To change this template use File | Settings | File Templates.
--%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="admin" tagdir="/WEB-INF/tags" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<script src="/resources/assets/global/scripts/jquery-1.8.3.min.js"></script>
<script src="/resources/assets/global/scripts/vue.min.js"></script>
<script src="/resources/assets/global/scripts/regex.js"></script>
<script src="/resources/assets/global/scripts/volidata.js"></script>
<admin:container id="tenderList" pageJsPath="/resources/tender/tender.js">
    <style>
        .pgt-error {
            color: red;
        }
    </style>
    <div class="row">
        <div class="col-xs-12">
            <ul class="page-breadcrumb breadcrumb">
                <li>
                    <a href="<spring:url value="/"/>">首页</a>
                    <i class="fa fa-circle"></i>
                </li>
                <li>
                    <a href="#">投资列表</a>
                    <i class="fa fa-circle"></i>
                </li>
                <li class="active">
                    <a href="#">新增投资</a>
                </li>
            </ul>
        </div>
    </div>

    <!-- super:把错误内容放在span里面,有两种提示框 alert-danger 和 alert-success 两种.如果不需要显示时把display改为none-->
    <div class="row" id="alertRow" style="display: none">
        <div class="col-xs-12">
            <div class="Metronic-alerts alert alert-danger fade in">
                <button type="button" class="close" data-dismiss="alert" aria-hidden="true"></button>
                <p id="alertText">错误信息</p>
            </div>
        </div>
    </div>

    <div class="row">
        <div class="col-xs-12">
            <div class="portlet box blue-hoki">
                <div class="portlet-title">
                    <div class="caption">
                        <i class="fa fa-gift"></i>基本信息
                    </div>
                </div>
                <div id="app" class="portlet-body form">
                    <!-- BEGIN FORM-->
                    <div id="error" v-model="error">
                        <form  id = "form"  action="/tender/${tender.tenderId eq null ? 'create' : 'update'} "
                          method="post"
                          class="form-horizontal"
                          v-on:submit.prevent="ajaxSubmit">
                        <div class="form-body">
                            <!-- 只有在修改时才显示id行-->
                            <c:if test="${!empty tender.tenderId}">
                                <div class="form-group">
                                    <label class="col-xs-3 control-label">id</label>
                                    <input type="hidden" name="tenderId" value="${tender.tenderId}"/>

                                    <div class="col-xs-4">
                                        <p class="form-control-static">
                                                ${tender.tenderId}
                                        </p>
                                    </div>
                                </div>
                            </c:if>

                            <div class="form-group">
                                <label class="col-xs-3 control-label">当铺的编号:</label>

                                <div class="col-xs-4">
                                    <input ms-duplex="pawnShopId" path="pawnShopId" name="pawnShopId"
                                           value="${tender.pawnShopId}" placeholder=""
                                           class="form-control  pgt-requisite-name"
                                           v-model="tender.pawnShopId"
                                           v-on:keyup="volidate"/>
                                </div>
                                <div class="col-xs-4">
                                    <p class="form-control-static pgt-error">
                                        <span v-show="error.pawnShopId != true">{{error.pawnShopId}}</span>
                                    </p>
                                </div>
                            </div>


                            <div class="form-group">
                                <label class="col-xs-3 control-label">当票编号:</label>

                                <div class="col-xs-4">
                                    <input ms-duplex="pawnTicketId" path="pawnTicketId" name="pawnTicketId"
                                           value="${tender.pawnTicketId}" placeholder=""
                                           class="form-control  pgt-requisite-name"
                                           v-model="tender.pawnTicketId"
                                           v-on:keyup="volidate"/>
                                </div>
                                <div class="col-xs-4">
                                    <p class="form-control-static pgt-error">
                                        <span v-show="error.pawnTicketId != true">{{error.pawnTicketId}}</span>
                                    </p>
                                </div>
                            </div>

                            <div class="form-group">
                                <label class="col-xs-3 control-label">投资总金额</label>

                                <div class="col-xs-4">
                                    <input id="tenderTotal"
                                           name="tenderTotal"
                                           value="${tender.tenderTotal}"
                                           type="text"
                                           class="form-control"
                                           placeholder="商家id"
                                           v-model="tender.tenderTotal"
                                           v-on:keyup="volidate"/>
                                </div>
                                <div class="col-xs-4">
                                    <p class="form-control-static pgt-error">
                                        <span v-show="error.tenderTotal != true">{{error.tenderTotal}}</span>
                                    </p>
                                </div>
                            </div>
                            <div class="form-group">
                                <label class="col-xs-3 control-label">最小投资金额</label>

                                <div class="col-xs-4">
                                    <input ms-duplex-limit="smallMoney" data-duplex-totalLimit="8" name="smallMoney"
                                           value="${tender.smallMoney}" class="form-control" placeholder="不超过20字"
                                           v-model="tender.smallMoney"
                                           v-on:keyup="volidate"/>
                                </div>
                                <div class="col-xs-4">
                                    <p class="form-control-static pgt-error">
                                        <span v-show="error.smallMoney != true">{{error.smallMoney}}</span>
                                    </p>
                                </div>
                            </div>

                            <div class="form-group">
                                <label class="col-md-3 control-label">开始时间:</label>
                                <div class="col-md-4">
                                    <input id="publishDate" type="text" name="publishDate"
                                           value="<fmt:formatDate value="${tender.publishDate}" type="both" />"
                                           class="form-control" placeholder="" maxlength="35"
                                           onfocus="$(this).calendar()"
                                           v-model="tender.publishDate"
                                           v-on:click="setDate"/>
                                </div>
                                <div class="col-xs-4">
                                    <p class="form-control-static pgt-error">
                                        <span v-show="error.publishDate != true">{{error.publishDate}}</span>
                                    </p>
                                </div>
                            </div>

                            <div class="form-group">
                                <label class="col-md-3 control-label">截止时间:</label>
                                <div class="col-md-4">
                                    <input id="dueDate" type="text" name="dueDate"
                                           value="<fmt:formatDate value="${tender.dueDate}" type="both" />"
                                           class="form-control" placeholder="" maxlength="35"
                                           onfocus="$(this).calendar()"
                                           v-model="tender.dueDate"
                                           v-on:click="setDate"/>
                                </div>
                                <div class="col-xs-4">
                                    <p class="form-control-static pgt-error">
                                        <span v-show="error.dueDate != true">{{error.dueDate}}</span>
                                    </p>
                                </div>
                            </div>


                            <div class="form-group">
                                <label class="col-xs-3 control-label">收益率:</label>
                                <div class="col-xs-4">
                                    <input ms-duplex="interestRate" name="interestRate" value="${tender.interestRate}"
                                           class="form-control" placeholder=""
                                           v-model="tender.interestRate"
                                           v-on:keyup="volidate"/>
                                </div>
                                <div class="col-xs-4">
                                    <p class="form-control-static pgt-error">
                                        <span v-show="error.interestRate != true">{{error.interestRate}}</span>
                                    </p>
                                </div>
                            </div>

                            <div class="form-group">
                                <label class="col-xs-3 control-label">投资名称</label>
                                <div class="col-xs-4">
                                    <input ms-duplex="name" name="name" value="${tender.name}" class="form-control"
                                           placeholder="不超过20字"
                                           v-model="tender.name"
                                           v-on:keyup="volidate"/>
                                </div>
                                <div class="col-xs-4">
                                    <p class="form-control-static pgt-error">
                                        <span v-show="error.name != true">{{error.name}}</span>
                                    </p>
                                </div>
                            </div>

                            <div class="form-group">
                                <label class="col-xs-3 control-label">投资的详情</label>
                                <div class="col-xs-4">
                                    <input ms-duplex="description" name="description" value="${tender.description}"
                                           class="form-control" placeholder="不超过20字"
                                           v-model="tender.description"
                                           v-on:keyup="volidate"/>
                                </div>
                                <div class="col-xs-4">
                                    <p class="form-control-static pgt-error">
                                        <span v-show="error.description != true">{{error.description}}</span>
                                    </p>
                                </div>
                            </div>

                            <div class="form-group">
                                <label class="col-xs-3 control-label">付款后多少天开始算收益</label>
                                <div class="col-xs-4">
                                    <input ms-duplex="prePeriod" name="prePeriod" value="${tender.prePeriod}"
                                           class="form-control" placeholder="不超过20字"
                                           v-model="tender.prePeriod"
                                           v-on:keyup="volidate"/>
                                </div>
                                <div class="col-xs-4">
                                    <p class="form-control-static pgt-error">
                                        <span v-show="error.prePeriod != true">{{error.prePeriod}}</span>
                                    </p>
                                </div>
                            </div>

                            <div class="form-group">
                                <label class="col-xs-3 control-label">无息天数</label>
                                <div class="col-xs-4">
                                    <input ms-duplex="postPeriod" name="postPeriod" value="${tender.postPeriod}"
                                           class="form-control" placeholder="不超过10字"
                                           v-model="tender.postPeriod"
                                           v-on:keyup="volidate"/>
                                </div>
                                <div class="col-xs-4">
                                    <p class="form-control-static pgt-error">
                                        <span v-show="error.postPeriod != true">{{error.postPeriod}}</span>
                                    </p>
                                </div>
                            </div>

                            <div class="form-group">
                                <label class="control-label col-xs-3">是否分类热门</label>

                                <div class="col-xs-9">
                                    <div class="radio-list">
                                        <select ms-duplex="categoryHot" name="categoryHot"
                                                class="form-control input-medium">
                                            <option value="1">是</option>
                                            <option value="0">否</option>
                                        </select>
                                    </div>
                                </div>
                            </div>

                            <div class="form-group">
                                <label class="control-label col-xs-3">是否网站热门</label>

                                <div class="col-xs-9">
                                    <div class="radio-list">
                                        <select ms-duplex="status" name="siteHot" class="form-control input-medium">
                                            <option value="1">是</option>
                                            <option value="0">否</option>
                                        </select>
                                    </div>
                                </div>
                            </div>

                            <div class="form-actions top">
                                <div class="row">
                                    <div class="col-md-offset-3 col-md-9">
                                        <button type="submit" class="btn blue-hoki">确认</button>
                                        <button type="button" class="btn default">取消</button>
                                    </div>
                                </div>
                            </div>

                        </div>




                            <div class="img-body form-horizontal" style="position: absolute; top: 1000px;width: 1300px;">
                                <h2 class="pgt-part-title">图片列表</h2>

                                <div class="form-group">
                                    <label class="col-md-2 control-label">首页图</label>
                                    <div class="col-md-2">
                                        <form class="pgt-file-box" action="" enctype="multipart/form-data">
                                            <input class="pgt-file-btn" name="advertisement" type="file"/>
                                            <button type="button" class="btn blue">选择图片</button>
                                        </form>
                                        <p></p>
                                    </div>
                                    <div class="col-md-8">
                                        <div class="pgt-each-img">
                                            <div class="pgt-handle-box">
                                                <a class="pgt-img-delete" href="#">删除</a>
                                            </div>
                                            <img class="pgt-advertisement-img" src="" alt=""/>
                                            <p>60 * 60</p>
                                        </div>
                                    </div>
                                </div>

                                <div class="form-group">
                                    <label class="col-md-2 control-label">列表图</label>
                                    <div class="col-md-2">
                                        <form class="pgt-file-box" action="" enctype="multipart/form-data">
                                            <input class="pgt-file-btn" name="front" type="file"/>
                                            <button type="button" class="btn blue">选择图片</button>
                                        </form>
                                        <p></p>
                                    </div>
                                    <div class="col-md-8">
                                        <div class="pgt-each-img">
                                            <div class="pgt-handle-box">
                                                <a class="pgt-img-delete" href="#">删除</a>
                                            </div>
                                            <img class="pgt-front-img" src="" alt=""/>
                                            <p>60 * 60</p>
                                        </div>
                                    </div>
                                </div>

                                <div class="form-group">
                                    <label class="col-md-2 control-label">主图</label>
                                    <div class="col-md-2">
                                        <form class="pgt-file-box" action="" enctype="multipart/form-data">
                                            <input class="pgt-file-btn" name="front" type="file"/>
                                            <button type="button" class="btn blue">选择图片</button>
                                        </form>
                                        <p></p>
                                    </div>
                                    <div class="col-md-8">
                                        <div class="pgt-each-img">
                                            <div class="pgt-handle-box">
                                                <a class="pgt-img-delete" href="#">删除</a>
                                            </div>
                                            <img class="pgt-front-img" src="" alt=""/>
                                            <p>60 * 60</p>
                                        </div>
                                    </div>
                                </div>

                                <div class="form-group">
                                    <label class="col-md-2 control-label">详情图</label>
                                    <div class="col-md-2">
                                        <form class="pgt-file-box" action="" enctype="multipart/form-data">
                                            <input class="pgt-file-btn" name="main" type="file"/>
                                            <button type="button" class="btn blue">选择图片</button>
                                        </form>
                                        <p></p>
                                    </div>
                                    <div class="col-md-8">
                                        <div class="pgt-each-img">
                                            <div class="pgt-handle-box">
                                                <a class="pgt-img-pre" href="#">前移</a>
                                                <a class="pgt-img-delete" href="#">删除</a>
                                            </div>
                                            <img class="pgt-main-img" src="" alt=""/>
                                            <p class="pgt-img-size">100 * 100</p>
                                        </div>
                                        <div class="pgt-each-img">
                                            <div class="pgt-handle-box">
                                                <a class="pgt-img-pre" href="#">前移</a>
                                                <a class="pgt-img-delete" href="#">删除</a>
                                            </div>
                                            <img class="pgt-main-img" src="" alt=""/>
                                            <p class="pgt-img-size">100 * 100</p>
                                        </div>
                                        <div class="pgt-each-img">
                                            <div class="pgt-handle-box">
                                                <a class="pgt-img-pre" href="#">前移</a>
                                                <a class="pgt-img-delete" href="#">删除</a>
                                            </div>
                                            <img class="pgt-main-img" src="" alt=""/>
                                            <p class="pgt-img-size">100 * 100</p>
                                        </div>
                                    </div>
                                </div>

                                <div class="form-group">
                                    <label class="col-md-2 control-label">专家点评</label>
                                    <div class="col-md-2">
                                        <form class="pgt-file-box" action="" enctype="multipart/form-data">
                                            <input class="pgt-file-btn" name="expert" type="file"/>
                                            <button type="button" class="btn blue">选择图片</button>
                                        </form>
                                        <p></p>
                                    </div>
                                    <div class="col-md-8">
                                        <div class="pgt-each-img">
                                            <div class="pgt-handle-box">
                                                <a class="pgt-img-pre" href="#">前移</a>
                                                <a class="pgt-img-delete" href="#">删除</a>
                                            </div>
                                            <img class="pgt-expert-img" src="" alt=""/>
                                            <p class="pgt-img-size">100 * 100</p>
                                        </div>
                                        <div class="pgt-each-img">
                                            <div class="pgt-handle-box">
                                                <a class="pgt-img-pre" href="#">前移</a>
                                                <a class="pgt-img-delete" href="#">删除</a>
                                            </div>
                                            <img class="pgt-expert-img" src="" alt=""/>
                                            <p class="pgt-img-size">100 * 100</p>
                                        </div>
                                        <div class="pgt-each-img">
                                            <div class="pgt-handle-box">
                                                <a class="pgt-img-pre" href="#">前移</a>
                                                <a class="pgt-img-delete" href="#">删除</a>
                                            </div>
                                            <img class="pgt-expert-img" src="" alt=""/>
                                            <p class="pgt-img-size">100 * 100</p>
                                        </div>
                                    </div>
                                </div>

                            </div>



                    </form>
                    </div>
                </div>
                <!-- END FORM-->

            </div>
        </div>

    </div>

</admin:container>

<script src="/resources/assets/others/Jquery-date-and-time/jquery-calendar.js"></script>

