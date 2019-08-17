$(function () {
    $('.m_header_bar .m_header_bar_back').unbind("click");
    var isSearch = false;
    $(".m_header_bar .m_header_bar_back").click(function () {
        if (isSearch) {
            isSearch = false;
            $(".search .search-btn").hide();
            $(".search .menu").show();
            $(".search-tip").hide();
            $("#m_common_header_key").show();
        } else {
            history.go(-1);
        }
    });

    $("#keyword").click(function () {
        isSearch = true;
        $(".search .search-btn").show();
        $(".search .menu").hide();
        $(".search-tip").show();
        $("#m_common_header_key").hide();
    });

    $("#barTabs a").click(function () {
        var a = $(this);
        var sort = a.attr("sort");
        if (sort) {
            sort = parseInt(sort);
            if (sort == 3) {
                if (!a.hasClass('cur')) {
                    sort = 4;
                }
            }
            var url = location.href;
            if (url.indexOf("sort=") > 0) {
                url = location.href = url.replace(/sort=[1-4]/g, 'sort=' + sort);
            } else {
                url += "&sort=" + sort;
            }
            location.href = url;
        } else { //筛选
            $("#sfLayerBg").addClass("show");
            $("#filterBlock").addClass("show");
        }

    });

    $("#filterCBtn").click(function () {
        $("#sfLayerBg").removeClass("show");
        $("#filterBlock").removeClass("show");
    });

    $("#sfLayerBg").click(function () {
        $("#sfLayerBg").removeClass("show");
        $("#filterBlock").removeClass("show");
    });

    $('.search-btn').click(function () {
        if($("#keyword").val()==''){
            return false;
        }
        addSearchHistory($("#keyword").val());
        $("#searchForm").submit();
    });

    $(".J_ping.option").click(function () {
        var option=$(this);
        if(option.hasClass("selected")){
            option.removeClass("selected");
        }else{
            option.addClass("selected");
        }
    });

    $("#filterFinishBtn").click(function () {
        var uri=location.href;

        var minPrice=$("#minPrice").val();
        uri=addParam(uri, /minPrice=[0-9.]+/, 'minPrice', minPrice);

        var maxPrice=$('#maxPrice').val();
        uri=addParam(uri, /maxPrice=[0-9.]+/, 'maxPrice', maxPrice);

        var brand='';
        $(".brandFilter .J_ping.selected").each(function () {
            brand+=$(this).attr('bid')+",";
        });
        if(brand!=''){
            brand=brand.substr(0, brand.length-1);
            uri=addParam(uri, /brandId=[1-9][0-9]*[0-9,]*/, "brandId", brand)
        }else{
            uri=addParam(uri, /brandId=[1-9][0-9]*[0-9,]*/, "brandId", '')
        }

        var parameter='';
        $(".parameterFilter").each(function () {
            var parameterNode=$(this);
            var value='';
            parameterNode.find(".J_ping.selected").each(function () {
                value+=$(this).attr("valueId")+",";
            });
            if(value!=''){
                value=value.substr(0, value.length-1);
                parameter+=parameterNode.attr('parameterId')+"-"+value+"_";
            }
        });
        if(parameter!=''){
            parameter=parameter.substr(0, parameter.length-1);
            uri=addParam(uri, /parameter=[1-9][0-9]*-[1-9][0-9]*[0-9,]*[|\-0-9,]*/, "parameter", parameter);
        }else{
            uri=addParam(uri, /parameter=[1-9][0-9]*-[1-9][0-9]*[0-9,]*[|\-0-9,]*/, "parameter", '');
        }
        location.href=uri;
    });

    $(window).scroll(function () { //加载下一页
        if ($(window).scrollTop() + $(window).height() == $(document).height()) {
            if(page*10 < total){ //还有下一页
                var uri= location.href.replace("item_list.jhtml", "item_list_data.jhtml");
                page=page+1;
                uri=addParam(uri, /page=[1-9][0-9]*/, "page", page);
                //效果
                $("#loadingLogo").removeClass("hide");

                $.get(uri, function (res) {
                    $("#loadingLogo").addClass("hide");
                    res=JSON.parse(res);
                    var more=Et.template($("#moreItem").html(),{
                        goods:res.objects,
                        baseUrl: baseUrl,
                        fileBaseUrl:fileBaseUrl
                    });
                    $("#itemList").append(more);
                });
            }else{
                $("#noMoreTips").removeClass("hide");
            }
        }
    });
});

function addParam(url, reg, name, value) {
    if(value!=''){
        if(url.indexOf(name+"=")>=0){
            url=url.replace(reg, name+"="+value);
        }else{
            url+="&"+name+"="+value;
        }
    }else{
        if(url.indexOf(name+"=")>=0){
            url=url.replace(reg, '');
        }
    }
    return url;
}
//加载搜索历史模板
(function () {
    var history=getSearchHistory();
    if (history && history.length>0){
        var more=Et.template($("#searchHistoryTmpl").html(),{
            baseUrl: baseUrl,
            history:history
        });
        $(".history-list").append(more);
    }else{
        $(".search-history").hide();
    }
})();
$('.search-history .del').click(function () {
    clearSearchHistory();
    $(".history-list").remove();
});