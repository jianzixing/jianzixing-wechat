package com.jianzixing.webapp.service;

import com.jianzixing.webapp.service.aftersales.AfterSalesService;
import com.jianzixing.webapp.service.balance.BalanceService;
import com.jianzixing.webapp.service.collect.CollectService;
import com.jianzixing.webapp.service.coupon.CouponService;
import com.jianzixing.webapp.service.goods.*;
import com.jianzixing.webapp.service.history.HistoryService;
import com.jianzixing.webapp.service.hotsearch.HotSearchService;
import com.jianzixing.webapp.service.marketing.EmailService;
import com.jianzixing.webapp.service.comment.DiscussCommentService;
import com.jianzixing.webapp.service.comment.SensitiveWordsService;
import com.jianzixing.webapp.service.comment.SuggestionService;
import com.jianzixing.webapp.service.cooperation.AdvertisingService;
import com.jianzixing.webapp.service.cooperation.FriendLinkService;
import com.jianzixing.webapp.service.discount.DiscountService;
import com.jianzixing.webapp.service.file.FileService;
import com.jianzixing.webapp.service.integral.IntegralService;
import com.jianzixing.webapp.service.javascript.JavaScriptService;
import com.jianzixing.webapp.service.area.AreaService;
import com.jianzixing.webapp.service.log.RequestAddressService;
import com.jianzixing.webapp.service.logistics.LogisticsService;
import com.jianzixing.webapp.service.mapapis.MapService;
import com.jianzixing.webapp.service.marketing.MessageService;
import com.jianzixing.webapp.service.marketing.SmsService;
import com.jianzixing.webapp.service.crawler.IPProxyCrawler;
import com.jianzixing.webapp.service.message.SystemMessageService;
import com.jianzixing.webapp.service.notice.NoticeService;
import com.jianzixing.webapp.service.order.OrderService;
import com.jianzixing.webapp.service.order.UserAddressService;
import com.jianzixing.webapp.service.page.PageService;
import com.jianzixing.webapp.service.payment.PaymentService;
import com.jianzixing.webapp.service.recommend.RecommendService;
import com.jianzixing.webapp.service.refund.RefundOrderService;
import com.jianzixing.webapp.service.shopcart.ShoppingCartService;
import com.jianzixing.webapp.service.spcard.ShoppingCardService;
import com.jianzixing.webapp.service.statistics.RankingService;
import com.jianzixing.webapp.service.statistics.StatisticsService;
import com.jianzixing.webapp.service.support.SupportService;
import com.jianzixing.webapp.service.system.*;
import com.jianzixing.webapp.service.trigger.TriggerService;
import com.jianzixing.webapp.service.user.UserLevelService;
import com.jianzixing.webapp.service.user.UserService;
import com.jianzixing.webapp.service.wechatsm.WeChatUserService;
import com.jianzixing.webapp.service.website.WebsiteLetterService;
import com.jianzixing.webapp.service.wecharqrcode.WeChatQRCodeService;
import com.jianzixing.webapp.service.wechat.WeChatService;
import com.jianzixing.webapp.service.wechatsm.*;
import com.jianzixing.webapp.service.wxplugin.WXPluginSignService;
import com.jianzixing.webapp.service.wxplugin.WXPluginVotingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

/**
 * @author yangankang
 */
@Service
public class GlobalService {
    public static IPProxyCrawler ipProxyCrawler;
    public static SmsService smsService;
    public static FileService fileService;
    public static JavaScriptService javaScriptService;
    public static SystemService systemService;
    public static MapService mapService;
    public static AdminService adminService;
    public static UserService userService;
    public static AreaService areaService;
    public static SystemDictService systemDict;
    public static SystemRobotsService systemRobotsService;
    public static FriendLinkService friendLinkService;
    public static NoticeService noticeService;
    public static RequestAddressService requestAddressService;
    public static StatisticsService statisticsService;
    public static RecommendService recommendService;
    public static DiscussCommentService discussCommentService;
    public static CouponService couponService;
    public static ShoppingCardService shoppingCardService;
    public static SensitiveWordsService sensitiveWordsService;
    public static SuggestionService suggestionService;
    public static SystemConfigService systemConfigService;
    public static AdvertisingService advertisingService;
    public static RankingService rankingService;
    public static SystemMessageService systemMessageService;
    public static EmailService emailService;
    public static MessageService messageService;
    public static WeChatService weChatService;
    public static GoodsService goodsService;
    public static GoodsGroupService goodsGroupService;
    public static GoodsParameterService goodsParameterService;
    public static OrderService orderService;
    public static PaymentService paymentService;
    public static UserAddressService userAddressService;
    public static LogisticsService logisticsService;
    public static TriggerService triggerService;
    public static DefaultGoodsBrandService goodsBrandService;
    public static WeChatUserService weChatUserService;
    public static UserLevelService userLevelService;
    public static IntegralService integralService;
    public static BalanceService balanceService;
    public static DiscountService discountService;
    public static AfterSalesService afterSalesService;
    public static RefundOrderService refundOrderService;
    public static GoodsCommentService goodsCommentService;
    public static CollectService collectService;
    public static HistoryService historyService;
    public static SupportService supportService;
    public static WeChatQRCodeService weChatQRCodeService;
    public static WebsiteLetterService websiteLetterService;
    public static WeChatPublicService weChatPublicService;
    public static WeChatOpenService weChatOpenService;
    public static WeChatMaterialService weChatMaterialService;
    public static WeChatReplyService weChatReplyService;
    public static WeChatMassService weChatMassService;
    public static WeChatMiniProgramService weChatMiniProgramService;
    public static WeChatWebSiteService weChatWebSiteService;
    public static WeChatAppService weChatAppService;
    public static ShoppingCartService shoppingCartService;
    public static PageService pageService;
    public static HotSearchService hotSearchService;

    // 以下是微信扩展功能的全局类
    public static WXPluginSignService pluginSignService;
    public static WXPluginVotingService pluginVotingService;

    @Resource
    public void setHotSearchService(HotSearchService hotSearchService) {
        GlobalService.hotSearchService = hotSearchService;
    }

    @Resource
    public void setPluginVotingService(WXPluginVotingService pluginVotingService) {
        GlobalService.pluginVotingService = pluginVotingService;
    }

    @Resource
    public void setPluginSignService(WXPluginSignService pluginSignService) {
        GlobalService.pluginSignService = pluginSignService;
    }

    @Resource
    public void setWeChatWebSiteService(WeChatWebSiteService weChatWebSiteService) {
        GlobalService.weChatWebSiteService = weChatWebSiteService;
    }

    @Resource
    public void setWeChatAppService(WeChatAppService weChatAppService) {
        GlobalService.weChatAppService = weChatAppService;
    }

    @Resource
    public void setWeChatMiniProgramService(WeChatMiniProgramService weChatMiniProgramService) {
        GlobalService.weChatMiniProgramService = weChatMiniProgramService;
    }

    @Resource
    public void setWeChatMassService(WeChatMassService weChatMassService) {
        GlobalService.weChatMassService = weChatMassService;
    }

    @Resource
    public void setWeChatReplyService(WeChatReplyService weChatReplyService) {
        GlobalService.weChatReplyService = weChatReplyService;
    }

    @Resource
    public void setWeChatMaterialService(WeChatMaterialService weChatMaterialService) {
        GlobalService.weChatMaterialService = weChatMaterialService;
    }

    @Resource
    public void setDiscountService(DiscountService discountService) {
        GlobalService.discountService = discountService;
    }

    @Resource
    public void setWeChatOpenService(WeChatOpenService weChatOpenService) {
        GlobalService.weChatOpenService = weChatOpenService;
    }

    @Resource
    public void setWeChatPublicService(WeChatPublicService weChatPublicService) {
        GlobalService.weChatPublicService = weChatPublicService;
    }

    @Resource
    public void setWebsiteLetterService(WebsiteLetterService websiteLetterService) {
        GlobalService.websiteLetterService = websiteLetterService;
    }

    @Resource
    public void setSupportService(SupportService supportService) {
        GlobalService.supportService = supportService;
    }

    @Resource
    public void setGoodsCommentService(GoodsCommentService goodsCommentService) {
        GlobalService.goodsCommentService = goodsCommentService;
    }

    @Resource
    public void setRefundOrderService(RefundOrderService refundOrderService) {
        GlobalService.refundOrderService = refundOrderService;
    }

    @Resource
    public void setAfterSalesService(AfterSalesService afterSalesService) {
        GlobalService.afterSalesService = afterSalesService;
    }

    @Resource
    public void setCollectService(CollectService collectService) {
        GlobalService.collectService = collectService;
    }

    @Resource
    public void setHistoryService(HistoryService historyService) {
        GlobalService.historyService = historyService;
    }

    @Resource
    public void setWeChatQRCodeService(WeChatQRCodeService weChatQRCodeService) {
        GlobalService.weChatQRCodeService = weChatQRCodeService;
    }

    @Resource
    public void setBalanceService(BalanceService balanceService) {
        GlobalService.balanceService = balanceService;
    }

    @Resource
    public void setIntegralService(IntegralService integralService) {
        GlobalService.integralService = integralService;
    }

    @Resource
    public void setUserLevelService(UserLevelService userLevelService) {
        GlobalService.userLevelService = userLevelService;
    }

    @Resource
    public void setWeChatUserService(WeChatUserService weChatUserService) {
        GlobalService.weChatUserService = weChatUserService;
    }

    @Resource
    public void setGoodsBrandService(DefaultGoodsBrandService goodsBrandService) {
        GlobalService.goodsBrandService = goodsBrandService;
    }

    @Resource
    public void setTriggerService(TriggerService triggerService) {
        GlobalService.triggerService = triggerService;
    }

    @Resource
    public void setLogisticsService(LogisticsService logisticsService) {
        GlobalService.logisticsService = logisticsService;
    }

    @Resource
    public void setUserAddressService(UserAddressService userAddressService) {
        GlobalService.userAddressService = userAddressService;
    }

    @Resource
    public void setPaymentService(PaymentService paymentService) {
        GlobalService.paymentService = paymentService;
    }

    @Resource
    public void setGoodsParameterService(GoodsParameterService goodsParameterService) {
        GlobalService.goodsParameterService = goodsParameterService;
    }

    @Resource
    public void setGoodsGroupService(GoodsGroupService goodsGroupService) {
        GlobalService.goodsGroupService = goodsGroupService;
    }

    @Resource
    public void setGoodsService(GoodsService goodsService) {
        GlobalService.goodsService = goodsService;
    }

    @Resource
    public void setOrderService(OrderService orderService) {
        GlobalService.orderService = orderService;
    }

    @Resource
    public void setWeChatService(WeChatService weChatService) {
        GlobalService.weChatService = weChatService;
    }

    @Resource
    public void setEmailService(EmailService emailService) {
        GlobalService.emailService = emailService;
    }

    @Resource
    public void setMessageService(MessageService messageService) {
        GlobalService.messageService = messageService;
    }

    @Resource
    public void setSystemMessageService(SystemMessageService systemMessageService) {
        GlobalService.systemMessageService = systemMessageService;
    }

    @Resource
    public void setRankingService(RankingService rankingService) {
        GlobalService.rankingService = rankingService;
    }

    @Resource
    public void setAdvertisingService(AdvertisingService advertisingService) {
        GlobalService.advertisingService = advertisingService;
    }

    @Resource
    public void setSystemConfigService(SystemConfigService systemConfigService) {
        GlobalService.systemConfigService = systemConfigService;
    }

    @Resource
    public void setSuggestionService(SuggestionService suggestionService) {
        GlobalService.suggestionService = suggestionService;
    }

    @Resource
    public void setSensitiveWordsService(SensitiveWordsService sensitiveWordsService) {
        GlobalService.sensitiveWordsService = sensitiveWordsService;
    }

    @Resource
    public void setShoppingCardService(ShoppingCardService shoppingCardService) {
        GlobalService.shoppingCardService = shoppingCardService;
    }

    @Resource
    public void setCouponService(CouponService couponService) {
        GlobalService.couponService = couponService;
    }

    @Resource
    public void setDiscussCommentService(DiscussCommentService discussCommentService) {
        GlobalService.discussCommentService = discussCommentService;
    }

    @Resource
    public void setRecommendService(RecommendService recommendService) {
        GlobalService.recommendService = recommendService;
    }

    @Resource
    public void setStatisticsService(StatisticsService statisticsService) {
        GlobalService.statisticsService = statisticsService;
    }

    @Resource
    public void setRequestAddressService(RequestAddressService requestAddressService) {
        GlobalService.requestAddressService = requestAddressService;
    }

    @Resource
    public void setSystemRobotsService(SystemRobotsService systemRobotsService) {
        GlobalService.systemRobotsService = systemRobotsService;
    }

    @Resource
    public void setSystemDict(SystemDictService systemDict) {
        GlobalService.systemDict = systemDict;
    }

    @Resource
    public void setMapService(MapService mapService) {
        GlobalService.mapService = mapService;
    }

    @Resource
    public void setIpProxyCrawler(IPProxyCrawler ipProxyCrawler) {
        GlobalService.ipProxyCrawler = ipProxyCrawler;
    }

    @Resource
    public void setSmsService(SmsService smsService) {
        GlobalService.smsService = smsService;
    }

    @Resource
    public void setFileService(FileService fileService) {
        GlobalService.fileService = fileService;
    }

    @Resource
    public void setJavaScriptService(JavaScriptService javaScriptService) {
        GlobalService.javaScriptService = javaScriptService;
    }

    @Resource
    public void setSystemService(SystemService systemService) {
        GlobalService.systemService = systemService;
    }

    @Resource
    public void setAdminService(AdminService adminService) {
        GlobalService.adminService = adminService;
    }

    @Resource
    public void setUserService(UserService userService) {
        GlobalService.userService = userService;
    }

    @Resource
    public void setAreaService(AreaService areaService) {
        GlobalService.areaService = areaService;
    }

    @Resource
    public void setFriendLinkService(FriendLinkService friendLinkService) {
        GlobalService.friendLinkService = friendLinkService;
    }

    @Resource
    public void setNoticeService(NoticeService noticeService) {
        GlobalService.noticeService = noticeService;
    }

    @Resource
    public void setShoppingCartService(ShoppingCartService shoppingCartService) {
        GlobalService.shoppingCartService = shoppingCartService;
    }

    @Resource
    public void setPageService(PageService pageService) {
        GlobalService.pageService = pageService;
    }
}
