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
 * @Date 2020/2/4 下午5:43
 */
@RestController
@RequestMapping("/rmgr")
public class AuthForwardController {
    @Value("${defaultUrl}")
    private String defaultUrl;

    @Resource
    private HttpClient httpClient;

    @Resource
    private HandlerUtil handlerUtil;

    private static Logger logger = LoggerFactory.getLogger( AuthForwardController.class );

    @PostMapping("/media_auth")
    public Object authForward(@RequestBody JSONObject jsonObject) {
        String requestUrl="";
        logger.info("request params:"+jsonObject.toString());

        JSONObject jsonObjectDummy = null;
        int version = 0;

        try {
            String tokenStr = jsonObject.get("token").toString();
            Base64 coder= new Base64(300, new byte[]{}, true);
            byte[] token = coder.decode( tokenStr.getBytes( "UTF-8" ) );
            version = handlerUtil.getTokenVersion(token);
            if(version == 1)
            {
                requestUrl = defaultUrl;
                jsonObjectDummy = HandlerUtil.getV1JsonObject(jsonObject);
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

                jsonObjectDummy = HandlerUtil.getV2JsonObject(jsonObject);
            }
            logger.info("forward url:"+requestUrl+",body json:"+jsonObjectDummy.toString());
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (IllegalArgumentException e)
        {
            //v1 token
            requestUrl = defaultUrl;
            jsonObjectDummy = jsonObject;
            jsonObjectDummy = HandlerUtil.getV1JsonObject(jsonObject);
            logger.info("default forward url:"+requestUrl+",body json:"+jsonObjectDummy.toString());
        }
        //post请求
        HttpMethod method = HttpMethod.POST;

        //发送http请求并返回结果
        return httpClient.client(requestUrl,method,jsonObjectDummy);
    }
}