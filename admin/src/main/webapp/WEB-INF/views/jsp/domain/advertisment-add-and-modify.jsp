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
<%@ taglib prefix="C" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<admin:container id="productList" pageCssPath="/resources/category/category-add-and-modify.css" pageJsPath="/resources/category/category-add-and-modify.js">
    <input type="hidden" value="${staticServer}" id="staticServer"/>
    <div class="row">
        <div class="col-xs-12">
            <ul class="page-breadcrumb breadcrumb">
                <li>
                    <a href="#">首页</a>
                    <i class="fa fa-circle"></i>
                </li>
                <li>
                    <a href="#">分类列表</a>
                    <i class="fa fa-circle"></i>
                </li>
                <li class="active">
                    <a href="#">b2c</a>
                </li>
            </ul>
        </div>
    </div>

    <!-- super:把错误内容放在span里面,有两种提示框 alert-danger 和 alert-success 两种.如果不需要显示时把display改为none-->
    <div class="row" style="display: none">
        <div class="col-xs-12">
            <div class="Metronic-alerts alert alert-danger fade in">
                <button type="button" class="close" data-dismiss="alert" aria-hidden="true"></button>
                <p>错误信息</p>
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
                <div class="portlet-body form">
                    <!-- BEGIN FORM-->
                    <form:form action="onlinepawmadd" class="form-horizontal" modelAttribute="category">
                        <div class="form-body">
                            <!-- 只有在修改时才出现id行-->
                            <div class="form-group">
                                <label class="col-md-3 control-label">分类产品id:</label>
                                <div class="col-md-4">
                                    <form:input type="text" class="form-control" placeholder="6个字以下" path="recommendedProductId"/>
                                    </p>
                                </div>
                            </div>
                            <div class="form-group">
                                <label class="col-md-3 control-label">分类名称:</label>
                                <div class="col-md-4">
                                    <form:input type="text" class="form-control" placeholder="6个字以下" path="name"/>
                                    </p>
                                </div>
                            </div>
                            <div class="form-group">
                                <label class="col-md-3 control-label">网站:</label>
                                <div class="col-md-4">
                                    <form:select class="form-control input-medium" path="productType" items="${categories}" itemLabel="name" itemValue="id">
                                        <option value="PRODUCT">b2c</option>
                                        <option value="TENDER">p2p</option>
                                    </form:select>
                                    </p>
                                </div>
                            </div>
                            <div class="form-group">
                                <label class="col-md-3 control-label">推荐类型:</label>
                                <div class="col-md-4">
                                    <form:input type="text" class="form-control" placeholder="6个字以下" path="type"/>
                                    </p>
                                </div>
                            </div>
                            <div class="form-group">
                                <label class="col-md-3 control-label">分类链接:</label>
                                <div class="col-md-4">
                                    <form:input type="text" class="form-control" placeholder="6个字以下" path="url"/>
                                    </p>
                                </div>
                            </div>
                            <div style="margin-top: 320px"></div>
                        </div>
                        <!-- super:修改产品时才出现下面一个div.form-group-->

                        <div class="form-actions top">
                            <div class="row">
                                <div class="col-md-offset-3 col-md-9">
                                    <button type="submit" class="btn blue-hoki">确认</button>
                                    <button type="button" class="btn default">取消</button>
                                </div>
                            </div>
                        </div>
                        <form:input cssClass="hidden" path="mediaId" id="frontMedia"/>
                    </form:form>
                </div>
                <!-- END FORM-->
            </div>
        </div>
    </div>
    <div id="imgUpload" class="pgt-img-upload">
        <div class="row">
            <div class="col-md-8">
                <div class="pgt-each-img">
                    <div class="pgt-handle-box">
                        <a class="pgt-img-delete" href="#">删除</a>
                    </div>
                    <img class="pgt-category-img" src="" alt=""/>
                    <p>200 * 200</p>
                </div>
            </div>
        </div>
        <div class="row">
            <label class="col-md-12 control-label">分类主图</label>
            <div class="col-md-12">
                <form class="pgt-file-box" action="/upload/image" enctype="multipart/form-data">
                    <input class="pgt-file-btn" name="uploadPicture" data-pgt-btn="single" type="file"/>
                    <input name="mediaType" type="hidden" value="ADVERTISMENT"/>
                    <button type="button" class="btn blue">选择图片</button>
                </form>
                <p></p>
            </div>
        </div>
    </div>
    <div id="testbox"></div>
</admin:container>

