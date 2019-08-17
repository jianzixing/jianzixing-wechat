<#macro tab index=1>
    <div id="commonNav" style="background-color: rgb(255, 255, 255);">
        <a style="width: 25%" href="${JZXUrl(Request)}/wx/index.jhtml">
            <div class="icon-center">
                <#if index==1>
                    <img class="nav-img" src="${JZXUrl(Request)}/wx/images/icons/icon_1cur.png">
                <#else>
                    <img class="nav-img" src="${JZXUrl(Request)}/wx/images/icons/icon_1.png">
                </#if>
            </div>
        </a>
        <a style="width: 25%" href="/wx/catalog.jhtml">
            <div class="icon-center">
                <#if index==2>
                    <img class="nav-img" src="${JZXUrl(Request)}/wx/images/icons/icon_2cur.png">
                <#else>
                    <img class="nav-img" src="${JZXUrl(Request)}/wx/images/icons/icon_2.png">
                </#if>
            </div>
        </a>
        <a style="width: 25%" href="/wx/cart.jhtml">
            <div class="icon-center">
                <img class="nav-img" src="${JZXUrl(Request)}/wx/images/icons/icon_4.png">
                <span class="cart-num">1</span>
            </div>
        </a>
        <a style="width: 25%" href="${JZXUrl(Request)}/wx/mine/index.jhtml">
            <div class="icon-center">
                <#if index==5>
                    <img class="nav-img" src="${JZXUrl(Request)}/wx/images/icons/icon_5cur.png">
                <#else>
                    <img class="nav-img" src="${JZXUrl(Request)}/wx/images/icons/icon_5.png">
                </#if>
            </div>
        </a>
    </div>
</#macro>
