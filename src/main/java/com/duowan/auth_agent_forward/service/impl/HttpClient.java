package com.duowan.auth_agent_forward.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.duowan.auth_agent_forward.controller.AuthForwardController;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.*;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

/**
 * @Author sj
 * @Date 2020/2/4 下午5:43
 */
@Service
public class HttpClient {
    private static Logger logger = LoggerFactory.getLogger( HttpClient.class );
    public String client(String url, HttpMethod method, JSONObject jsonObj) {
        SimpleClientHttpRequestFactory requestFactory = new SimpleClientHttpRequestFactory();
        requestFactory.setConnectTimeout(5*1000);
        requestFactory.setReadTimeout(5*1000);
        RestTemplate client = new RestTemplate(requestFactory);
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<String> entity = new HttpEntity<>(jsonObj.toString(), headers);
        ResponseEntity<String> response = client.exchange(url, HttpMethod.POST, entity, String.class);
        logger.info(response.getBody());
        return response.getBody();
    }
}