package com.jianzixing.webapp.service.wechat;

import com.jianzixing.webapp.service.wechat.model.AccountConfig;
import org.mimosaframework.core.json.ModelObject;

/**
 * 获得所有关注用户时的回调函数
 */
public interface GetUserListener {
    /**
     * {
     * "count":2,//这次获取的粉丝数量
     * "data":{//粉丝列表
     * "openid":[
     * "ocYxcuAEy30bX0NXmGn4ypqx3tI0",
     * "ocYxcuBt0mRugKZ7tGAHPnUaOW7Y"  ]
     * },
     * "next_openid":"ocYxcuBt0mRugKZ7tGAHPnUaOW7Y"//拉取列表最后一个用户的openid
     * }
     *
     * @param config
     * @param json
     */
    void callback(AccountConfig config, ModelObject json) throws Exception;
}
