<!DOCTYPE html>
<html lang="zh-CN">
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
    <title>退换/售后</title>
    <meta name="keywords" content="">
    <meta name="description" content="">
    <meta name="apple-mobile-web-app-capable" content="yes">
    <meta name="apple-mobile-web-app-status-bar-style" content="black">
    <meta name="format-detection" content="telephone=no">
    <meta name="viewport"
          content="width=device-width, initial-scale=1.0, maximum-scale=1.0, user-scalable=0, shrink-to-fit=no, viewport-fit=cover">
    <link rel="stylesheet" href="${JZXUrl(Request)}/wx/css/common.css">
    <link rel="stylesheet" href="${JZXUrl(Request)}/wx/css/mine/after_sale.css">
    <script type="text/javascript" src="${JZXUrl(Request)}/resources/libs/jquery.min.js"></script>
    <script type="text/javascript" src="${JZXUrl(Request)}/resources/libs/velocity.min.js"></script>
</head>
<body>
<div class="wx_wrap" id="wrapBody"
     img-url="${JZXFile(Request,'')}"
     load1-url="${JZXUrl(Request)}/wx/mine/after_sale_order.jhtml"
     load2-url="${JZXUrl(Request)}/wx/mine/after_sale_list_proc.jhtml"
     load3-url="${JZXUrl(Request)}/wx/mine/after_sale_list.jhtml"
     detail-url="${JZXUrl(Request)}/wx/mine/after_sale_detail.jhtml?n=">
    <div class="m_header" style="">
        <div class="m_header_bar">
            <div data-url="${JZXUrl(Request)}/wx/mine/index.jhtml" class="m_header_bar_back"></div>
            <div class="m_header_bar_title">退换/售后</div>
        </div>
        <div id="m_common_header_key" class="jzx-header-icon-new-shortcut">
            <span></span>
        </div>
    </div>

    <#include "../include/header_shortcut.ftl"/>

    <div id="m_common_content">
        <div class="react-root react-view">
            <div class="react-view homePageBody">
                <div class="react-view navs">
                    <div class="react-view item cur" data-id="1">
                        <div class="react-view box">
                            <div class="react-view" style="height: 15px;"></div>
                            <div class="react-view bottom">
                                <div class="react-view">
                                    <span type="Normal">售后申请</span>
                                </div>
                            </div>
                        </div>
                    </div>
                    <div class="react-view item" data-id="2">
                        <div class="react-view box">
                            <div class="react-view" style="height: 15px;"></div>
                            <div class="react-view bottom">
                                <div class="react-view">
                                    <span type="Normal">处理中</span>
                                </div>

                                <#if processCount?? && processCount gt 0>
                                    <div class="react-view pops">
                                        <div class="react-view pops_box">
                                            <div class="react-view">
                                                <div class="react-view icon">
                                                    <span type="Normal">${processCount}</span>
                                                </div>
                                            </div>
                                        </div>
                                    </div>
                                </#if>
                            </div>
                        </div>
                    </div>
                    <div class="react-view item" data-id="3">
                        <div class="react-view box">
                            <div class="react-view" style="height: 15px;"></div>
                            <div class="react-view bottom">
                                <div class="react-view">
                                    <span type="Normal">申请记录</span>
                                </div>
                            </div>
                        </div>
                    </div>
                </div>


                <div class="as_contents list_1" type-id="1" style="">
                    <div class="react-view hd_bar">
                        <div class="react-view search">
                            <div class="react-view icon_box">
                                <div class="react-view icon"></div>
                            </div>
                            <div class="react-view input">
                                <input placeholder="商品名称/商品编号/订单编号/序列号"
                                       type="text" value="">
                            </div>
                            <div class="react-view clear">
                                <div class="react-view icon"></div>
                            </div>
                        </div>
                        <div class="react-view filter">
                            <div class="hd_bar_btn white">取消</div>
                        </div>
                    </div>
                </div>

                <div class="as_contents list_2" type-id="2" style="display:none ">
                </div>
                <div class="as_contents list_3" type-id="3" style="display: none">
                    <div class="react-view hd_bar">
                        <div class="react-view search">
                            <div class="react-view icon_box">
                                <div class="react-view icon"></div>
                            </div>
                            <div class="react-view input">
                                <input placeholder="商品名称/商品编号/订单编号/序列号"
                                       type="text" value="">
                            </div>
                            <div class="react-view clear">
                                <div class="react-view icon"></div>
                            </div>
                        </div>
                        <div class="react-view filter">
                            <div class="hd_bar_btn white">取消</div>
                        </div>
                    </div>
                </div>
            </div>
        </div>
    </div>
    <div class="onsearch_mask"></div>
</div>
<script type="text/javascript" src="${JZXUrl(Request)}/wx/js/common.js"></script>
<script type="text/javascript" src="${JZXUrl(Request)}/wx/js/mine/after_sale.js"></script>
</body>
</html>
