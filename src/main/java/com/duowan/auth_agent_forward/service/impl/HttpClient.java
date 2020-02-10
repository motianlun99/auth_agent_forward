package com.duowan.auth_agent_forward.service.impl;

import com.alibaba.fastjson.JSONObject;
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
    //public String client(String url, HttpMethod method, MultiValueMap<String, String> params){
        //JSONObject

    public String client(String url, HttpMethod method, JSONObject jsonObj) {
        SimpleClientHttpRequestFactory requestFactory = new SimpleClientHttpRequestFactory();
        requestFactory.setConnectTimeout(5*1000);
        requestFactory.setReadTimeout(5*1000);
        RestTemplate client = new RestTemplate(requestFactory);
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<String> entity = new HttpEntity<>(jsonObj.toString(), headers);
        //HttpEntity<MultiValueMap<String, String>> requestEntity = new HttpEntity<MultiValueMap<String, String>>(params, headers);
        //  执行HTTP请求
        //ResponseEntity<String> response = client.exchange(url, HttpMethod.POST, requestEntity, String.class);
        ResponseEntity<String> response = client.exchange(url, HttpMethod.POST, entity, String.class);
        return response.getBody();
    }
}