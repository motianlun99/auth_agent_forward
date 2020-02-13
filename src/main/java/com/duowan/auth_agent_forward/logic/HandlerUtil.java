package com.duowan.auth_agent_forward.logic;

import com.alibaba.fastjson.JSONObject;
import com.duowan.auth_agent_forward.exception.YCTokenInvalidException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.io.UnsupportedEncodingException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @Author sj
 * @create 2020/2/6 14:38
 */
@Component
public class HandlerUtil {
    private  final Logger LOGGER = LoggerFactory.getLogger( HandlerUtil.class );

    public ByteBuffer getTokenElement(byte[] token, int offset, int length) {
        try {
            ByteBuffer buffer = ByteBuffer.wrap(token, offset, length);
            buffer.order(ByteOrder.BIG_ENDIAN);
            return buffer;
        } catch (Exception var5) {
            var5.printStackTrace();
            throw new YCTokenInvalidException();
        }
    }

    public int getTokenVersion(byte[] token){
        if(token == null){
            return -1;
        }
        short firstTwoBytes = this.getTokenElement(token, 0, 2).getShort();
        if(firstTwoBytes == 0){
            short versionBytes = this.getTokenElement(token, 2, 2).getShort();
            return versionBytes;
        }else if(firstTwoBytes > 0){
            return 1;
        }else{
            return -1;
        }
    }

    public int parseParameterMap(byte[] token, int offset, int elemenetCount, Map<String, byte[]> props) {
        AtomicInteger totalLen = new AtomicInteger();

        while(elemenetCount > 0) {
            try {
                short keyLen = getTokenElement(token, offset, 2).getShort();
                offset += 2;
                byte[] keyByte = new byte[keyLen];
                getTokenElement(token, offset, keyLen).get(keyByte);
                offset += keyLen;
                String key = new String(keyByte, "UTF-8");
                short valLen = getTokenElement(token, offset, 2).getShort();
                offset += 2;
                byte[] valByte = new byte[valLen];
                getTokenElement(token, offset, valLen).get(valByte);
                offset += valLen;
                props.put(key, valByte);
                totalLen.addAndGet(keyLen + 2);
                totalLen.addAndGet(valLen + 2);
                --elemenetCount;
            } catch (UnsupportedEncodingException var11) {
                var11.printStackTrace();
            }
        }
        return totalLen.get();
    }


    public static JSONObject getV1JsonObject(JSONObject jsonObject) {
        JSONObject v1Json = new JSONObject();
        //解析v3 json
        //业务不存在跨频道发布，那就用 uAppId。否则streamAppId。这个还是得看对接的业务。
        int uAppId = (int)jsonObject.get("uAppId");//项目Id
        int streamAppId = (int)jsonObject.get("streamAppId");//发布所在的appid

        //暂定先用字符串。要看业务接入使用那个而定。
        //int uid = (int)jsonObject.get("uid"); //用户Id
        String strUid = jsonObject.get("strUid").toString();//字符串用户Id

        //String channelName = jsonObject.get("channelName").toString();
        int sid = (int)jsonObject.get("sid");
        String name = jsonObject.get("name").toString();
        int type = (int)jsonObject.get("type");
        //String ipv4 = jsonObject.get("ipv4").toString();
        int auth = (int)jsonObject.get("auth");
        Long sendTime = (Long)jsonObject.get("sendTime");
        String token = jsonObject.get("token").toString();
        String session = jsonObject.get("session").toString();

        JSONObject strParams = jsonObject.getJSONObject("strParams");
        JSONObject u32Params = jsonObject.getJSONObject("u32Params");
        JSONObject u64Params = jsonObject.getJSONObject("u64Params");

        //构造V1 json
        v1Json.put("session",session);
        v1Json.put("appId",uAppId);
        v1Json.put("uid",strUid);
        v1Json.put("sid",sid);
        v1Json.put("name",name);
        v1Json.put("type",type);
        v1Json.put("auth",auth);
        v1Json.put("sendTime",sendTime);
        v1Json.put("token",token);

        v1Json.put("strParams",strParams);
        v1Json.put("u32Params",u32Params);
        v1Json.put("u64Params",u64Params);
        return v1Json;
    }

    public static JSONObject getV2JsonObject(JSONObject jsonObject) {
        JSONObject v2Json = new JSONObject();
        //解析v3 json
        //业务不存在跨频道发布，那就用 uAppId。否则streamAppId。这个还是得看对接的业务。
        int uAppId = (int)jsonObject.get("uAppId");//项目Id
        //int streamAppId = (int)jsonObject.get("streamAppId");//发布所在的appid

        //暂定先用字符串。要看业务接入使用那个而定。
        //int uid = (int)jsonObject.get("uid"); //用户Id
        String strUid = jsonObject.get("strUid").toString();//字符串用户Id

        String channelName = jsonObject.get("channelName").toString();
        //int sid = (int)jsonObject.get("sid");
        //String name = jsonObject.get("name").toString();
        //int type = (int)jsonObject.get("type");
        String ipv4 = jsonObject.get("ipv4").toString();
        int auth = (int)jsonObject.get("auth");
        Long sendTime = (Long)jsonObject.get("sendTime");
        String token = jsonObject.get("token").toString();
        String session = jsonObject.get("session").toString();

        JSONObject strParams = jsonObject.getJSONObject("strParams");
        JSONObject u32Params = jsonObject.getJSONObject("u32Params");
        JSONObject u64Params = jsonObject.getJSONObject("u64Params");

        //构造V2 json
        v2Json.put("appId",uAppId);
        v2Json.put("roomId",channelName);
        v2Json.put("uid",strUid);
        v2Json.put("ip",ipv4);
        v2Json.put("auth",auth);
        v2Json.put("sendTime",sendTime);
        v2Json.put("session",session);
        v2Json.put("token",token);

        return v2Json;
    }
}
