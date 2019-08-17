package com.jianzixing.webapp.service.crawler;

import com.alibaba.fastjson.JSONObject;
import com.jianzixing.webapp.tables.crawler.TableProxyIp;
import org.apache.commons.lang.math.NumberUtils;
import org.apache.http.HttpEntity;
import org.apache.http.HttpHost;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.mimosaframework.core.json.ModelObject;
import org.mimosaframework.orm.SessionTemplate;
import org.mimosaframework.orm.criteria.Criteria;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import us.codecraft.webmagic.Page;
import us.codecraft.webmagic.Site;
import us.codecraft.webmagic.Spider;
import us.codecraft.webmagic.processor.PageProcessor;
import us.codecraft.webmagic.selector.Selectable;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author yangankang
 */
@Service
public class XcdIPProxyCrawler implements IPProxyCrawler, PageProcessor {
    private static Site site = Site.me().setRetryTimes(3).setSleepTime(1000);
    private static AtomicInteger number = new AtomicInteger(173);
    private static String path = "http://www.xicidaili.com/nn/";

    @Autowired
    private SessionTemplate sessionTemplate;

    @Override
    public void prepare() {

    }

    @Override
    public void start() {
        Spider spider = Spider.create(this);
        spider.addUrl(path + number.incrementAndGet())
                .thread(1)
                .run();
    }

    @Override
    public void process(Page page) {
        Selectable selector = page.getHtml().xpath("//table[@id='ip_list']/tbody/tr");
        List<Selectable> nodes = selector.nodes();
        List<IPInfo> list = new ArrayList<>();
        for (Selectable selectable : nodes) {
            List<Selectable> trs = selectable.xpath("/tr/td/text()").nodes();
            if (trs.size() > 8) {
                try {
                    String ip = trs.get(1).get();
                    String portStr = trs.get(2).get();
                    int port = 0;
                    if (NumberUtils.isNumber(portStr)) {
                        port = Integer.parseInt(portStr);
                    } else {
                        continue;
                    }
                    String protocol = trs.get(5).get();
                    String survive = trs.get(8).get();
                    long surviveTime = 0;
                    if (survive != null) {
                        survive = survive.trim();
                        if (survive.endsWith("天")) {
                            String s = survive.replace("天", "");
                            surviveTime = Integer.parseInt(s) * 24 * 60 * 60 * 1000l;
                        } else if (survive.endsWith("小时")) {
                            String s = survive.replace("小时", "");
                            surviveTime = Integer.parseInt(s) * 60 * 60 * 1000l;
                        } else if (survive.endsWith("分钟")) {
                            String s = survive.replace("分钟", "");
                            surviveTime = Integer.parseInt(s) * 60 * 1000l;
                        }
                    }
                    String time = trs.get(9).get();
                    SimpleDateFormat format = new SimpleDateFormat("yy-MM-dd HH:mm");
                    Date date = format.parse(time);

                    long endTime = date.getTime();
                    IPInfo ipInfo = new IPInfo();
                    ipInfo.setIp(ip.trim());
                    ipInfo.setPort(port);
                    ipInfo.setProtocol(protocol.trim());
                    ipInfo.setSurviveTime(surviveTime);
                    ipInfo.setEndTime(endTime + surviveTime);
                    list.add(ipInfo);
                } catch (Exception e) {
                }
            }
        }

        if (list.size() > 0) {
            for (IPInfo ipInfo : list) {
                if (!this.isExist(ipInfo.ip, ipInfo.port)) {
                    ModelObject object = new ModelObject(TableProxyIp.class);
                    object.putAll(JSONObject.parseObject(JSONObject.toJSONString(ipInfo)));
                    sessionTemplate.save(object);
                }
            }
        }

        try {
            Thread.sleep(60000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        page.addTargetRequest(path + number.incrementAndGet());
    }

    @Override
    public Site getSite() {
        return site;
    }

    @Override
    public boolean isExist(String ip, int port) {
        List<ModelObject> objects = sessionTemplate.list(Criteria.query(TableProxyIp.class)
                .and(Criteria.filter().eq(TableProxyIp.ip, ip))
                .and(Criteria.filter().eq(TableProxyIp.port, port)));
        if (objects == null || objects.size() <= 0) {
            return false;
        }
        return true;
    }

    @Override
    public boolean checkProxyValid(String protocol, String ip, int port) {
        boolean isValid = false;
        try {
            HttpClientBuilder httpClientBuilder = HttpClientBuilder.create();
            CloseableHttpClient closeableHttpClient = httpClientBuilder.build();
            RequestConfig config = RequestConfig.custom().setProxy(new HttpHost(ip, port, protocol)).build();
            HttpPost httpPost = new HttpPost("http://www.baidu.com");
            httpPost.setConfig(config);

            CloseableHttpResponse response = closeableHttpClient.execute(httpPost);
            HttpEntity httpEntity = response.getEntity();
            if (httpEntity != null) {
                isValid = true;
            }
            closeableHttpClient.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return isValid;
    }

    @Override
    public void clearExpireIps() {
        Date date = new Date();
        long now = date.getTime();

        sessionTemplate.delete(Criteria.delete(TableProxyIp.class)
                .addFilter().lt(TableProxyIp.endTime, now)
                .delete());
    }

    @Override
    public List<ModelObject> getIps(int start, int limit) {
        return sessionTemplate.list(Criteria.query(TableProxyIp.class)
                .limit().limit(start, limit).goQuery());
    }

    public static class IPInfo {
        private String ip;
        private int port;
        private String protocol;
        private long surviveTime;
        private long endTime;

        public String getIp() {
            return ip;
        }

        public void setIp(String ip) {
            this.ip = ip;
        }

        public int getPort() {
            return port;
        }

        public void setPort(int port) {
            this.port = port;
        }

        public String getProtocol() {
            return protocol;
        }

        public void setProtocol(String protocol) {
            this.protocol = protocol;
        }

        public long getSurviveTime() {
            return surviveTime;
        }

        public void setSurviveTime(long surviveTime) {
            this.surviveTime = surviveTime;
        }

        public long getEndTime() {
            return endTime;
        }

        public void setEndTime(long endTime) {
            this.endTime = endTime;
        }
    }
}
