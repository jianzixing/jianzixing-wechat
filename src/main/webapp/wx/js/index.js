$(function () {
    var searchWrapper = $("#searchWrapper");
    var scrollNews = $(".j_scroll_news");

    $(window).scroll(function () {
        var scrollTop = $(document).scrollTop();
        if (scrollTop === 0) {
            searchWrapper.removeClass("main-page");
            searchWrapper.addClass("main-page2");
        } else {
            searchWrapper.removeClass("main-page2");
            searchWrapper.addClass("main-page");
        }
    });
    var second = 0;
    if($('.news_item').length >1){
        setInterval(function () {
            second++;
            if (second % 2 === 0) {
                scrollNews.css('transform', 'translate3d(0px, -24px, 0px)');
                scrollNews.css('transition', 'transform 500ms ease-in-out 0s');
            } else {
                var li = scrollNews.children('li')[0];
                scrollNews.append(li);
                scrollNews.css('transform', 'translate3d(0px, 0px, 0px)');
                scrollNews.css('transition', 'none 0s ease 0s');
            }
        }, 1000);
    }

    var slideList = $('.j_slide_list');
    var slideNav = $('.j_slide_nav');
    var length = slideList.children('li').length;
    var item = 1;
    if($('.j_slide_li').length >1){
        setInterval(function () {
            if (length == item) {
                slideList.css('transform', 'translateX(0%)');
                slideList.css('transition', 'all 0ms ease 0s');
                item = 1;
            }else{
                slideList.css('transform', 'translateX(-' + item + '00%)');
                slideList.css('transition', 'all 0.3s ease 0s');
                item++;
            }
            slideNav.children('span').removeClass('active');
            slideNav.children('span').eq([item - 1]).addClass('active');

        }, 3000);
    }

    var search=false;
    $("#msKeyWord").click(function () {
        if(!search){
            search=true;
            $(".search-tip").show();
            $("#msSearchBtn").show();

            $("#commonNav").hide();
            $("#msShortcutLogin").hide();
            $("#msShortcutMenu").hide();
            $('.m-common-header-search').css('background-color', '#fff');
            $("#msSearchBox").css("margin-right", "52px");
            $("#msCategoryBtn").hide();
            $("#msCancelBtn").show();
        }
    });
    $("#msCancelBtn").click(function () {
        if(search){
            search=false;
            $(".search-tip").hide();
            $("#msSearchBtn").hide();

            $("#commonNav").show();
            $("#msShortcutLogin").show();
            $("#msShortcutMenu").show();
            $('.m-common-header-search').css('background-color', '');
            $("#msSearchBox").css("margin-right", "32px");
            $("#msCategoryBtn").show();
            $("#msCancelBtn").hide();
        }
    });
    $("#msSearchBtn").click(function () {
        var msKeyWord=$("#msKeyWord").val();
        if(msKeyWord!=''){
            addSearchHistory(msKeyWord);
            $("#searchForm").submit();
        }
    });
});
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
    $(".search-history").remove();
});