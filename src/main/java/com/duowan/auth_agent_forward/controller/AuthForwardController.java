package com.duowan.auth_agent_forward.controller;

import com.duowan.auth_agent_forward.service.impl.HttpClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpMethod;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.alibaba.fastjson.JSONObject;
import javax.annotation.Resource;

/**
 * @Author sj
 * @Date 2020/2/4 下午5:43
 */
@RestController
@RequestMapping("/rmgr")
public class AuthForwardController {
    @Value("${defaultUrl}")
    private String defaultUrl;

    @Resource
    private HttpClient httpClient;

    private static Logger logger = LoggerFactory.getLogger( AuthForwardController.class );

    @PostMapping("/media_auth")
    public Object detail(@RequestBody JSONObject jsonObject) {
        String requestUrl;
        String url=jsonObject.get("url").toString();
        String appid=jsonObject.get("appid").toString();
        String appName = jsonObject.get("appName").toString();
        String token = jsonObject.get("token").toString();
        String sid = jsonObject.get("id").toString();
        int  id = Integer.parseInt( sid );
        logger.info("token url %s", url);
        //如果token中带了url，则跳转到定义的url中
        //TODO
        if( !url.isEmpty())
        {
            requestUrl=url;
        }
        //否则,按照默认的url路由
        else{
            requestUrl = defaultUrl;
        }
        //post请求
        HttpMethod method = HttpMethod.POST;
        // 封装参数，千万不要替换为Map与HashMap，否则参数无法传递
        MultiValueMap<String, String> params= new LinkedMultiValueMap<String, String>();
        params.add("appid", appid);
        params.add("appName", appName);
        params.add("token", token);
        //StringEntity s = new StringEntity(jsonParam.toString(), "UTF-8");
        //发送http请求并返回结果
        return httpClient.client(requestUrl,method,jsonObject);
    }
}
