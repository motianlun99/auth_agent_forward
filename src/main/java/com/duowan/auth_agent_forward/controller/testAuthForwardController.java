package com.duowan.auth_agent_forward.controller;

import com.alibaba.fastjson.JSONObject;
import com.duowan.auth_agent_forward.logic.HandlerUtil;
import com.duowan.auth_agent_forward.service.impl.HttpClient;
import org.apache.tomcat.util.codec.binary.Base64;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpMethod;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Map;

/**
 * @Author sj
 * @create 2020/2/5 10:21
 */
@RestController
@RequestMapping("/test/rmgr")
public class testAuthForwardController {
    @Value("${defaultTestUr}")
    private String defaultUrl;

    @Resource
    private HttpClient httpClient;

    @Resource
    private HandlerUtil handlerUtil;

    private static Logger logger = LoggerFactory.getLogger( AuthForwardController.class );

    @PostMapping("/media_auth")
    public Object authForward(@RequestBody JSONObject jsonObject) {
        String requestUrl="";
        logger.info("jsonObject %s", jsonObject.toString().toString());

        String tokenStr = jsonObject.get("token").toString();
        Base64 coder= new Base64(300, new byte[]{}, true);
        byte[] token = null;
        try {
            token = coder.decode( tokenStr.getBytes( "UTF-8" ) );
            int version = handlerUtil.getTokenVersion(token);
            if(version == 1)
            {
                requestUrl = defaultUrl;
            }
            else if(version == 2)
            {
                short uidLen = handlerUtil.getTokenElement(token, 12, 2).getShort();
                short parameterCount = handlerUtil.getTokenElement(token, 14 + uidLen, 2).getShort();
                Map<String, byte[]> parameterProps = new HashMap();
                int parameterLen = handlerUtil.parseParameterMap(token, 16 + uidLen, parameterCount, parameterProps);
                byte[] url = parameterProps.get("url");
                if(url == null || url.length == 0 )
                {
                    requestUrl = defaultUrl;
                }
                else{
                    requestUrl = new String(url);
                }
            }
        } catch( UnsupportedEncodingException e ) {
            requestUrl = defaultUrl;
        }
        logger.info("token forward url %s", requestUrl);

        //post请求
        HttpMethod method = HttpMethod.POST;
        // 封装参数，千万不要替换为Map与HashMap，否则参数无法传递
        //MultiValueMap<String, String> params= new LinkedMultiValueMap<String, String>();
        //params.add("appid", appid);
        //params.add("appName", appName);
        //params.add("token", token);
        //StringEntity s = new StringEntity(jsonParam.toString(), "UTF-8");
        //发送http请求并返回结果
        return httpClient.client(requestUrl,method,jsonObject);
    }
}
