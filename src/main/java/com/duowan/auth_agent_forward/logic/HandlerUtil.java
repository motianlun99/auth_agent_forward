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
        ParseV3JsonObject parseV3JsonObject = new ParseV3JsonObject(jsonObject).invoke();
        int uAppId = parseV3JsonObject.getuAppId();
        String strUid = parseV3JsonObject.getStrUid();
        int sid = parseV3JsonObject.getSid();
        String name = parseV3JsonObject.getName();
        int type = parseV3JsonObject.getType();
        int auth = parseV3JsonObject.getAuth();
        Long sendTime = parseV3JsonObject.getSendTime();
        String token = parseV3JsonObject.getToken();
        String session = parseV3JsonObject.getSession();
        JSONObject strParams = parseV3JsonObject.getStrParams();
        JSONObject u32Params = parseV3JsonObject.getU32Params();
        JSONObject u64Params = parseV3JsonObject.getU64Params();

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
        ParseV3JsonObject parseV3Json = new ParseV3JsonObject(jsonObject).invoke();
        int uAppId = parseV3Json.getuAppId();
        String strUid = parseV3Json.getStrUid();
        String channelName = parseV3Json.getChannelName();
        String ipv4 = parseV3Json.getIpv4();
        int auth = parseV3Json.getAuth();
        Long sendTime = parseV3Json.getSendTime();
        String token = parseV3Json.getToken();
        String session = parseV3Json.getSession();
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

    public static class ParseV3JsonObject {
        private JSONObject jsonObject;
        private int uAppId;
        private String strUid;
        private int sid;
        private String name;
        private int type;
        private int auth;
        private Long sendTime;
        private String token;
        private String session;
        private String channelName;
        private String ipv4;
        private JSONObject strParams;
        private JSONObject u32Params;
        private JSONObject u64Params;

        public ParseV3JsonObject(JSONObject jsonObject) {
            this.jsonObject = jsonObject;
        }

        public int getuAppId() {
            return uAppId;
        }

        public String getStrUid() {
            return strUid;
        }

        public int getSid() {
            return sid;
        }

        public String getName() {
            return name;
        }

        public int getType() {
            return type;
        }

        public int getAuth() {
            return auth;
        }

        public Long getSendTime() {
            return sendTime;
        }

        public String getToken() {
            return token;
        }

        public String getSession() {
            return session;
        }

        public String getChannelName() {
            return channelName;
        }
        public String getIpv4() {
            return ipv4;
        }
        public JSONObject getStrParams() {
            return strParams;
        }

        public JSONObject getU32Params() {
            return u32Params;
        }

        public JSONObject getU64Params() {
            return u64Params;
        }

        public ParseV3JsonObject invoke() {
            //解析v3 json
            //业务不存在跨频道发布，那就用 uAppId。否则streamAppId。这个还是得看对接的业务。
            uAppId = 0;
            if(jsonObject.get("uAppId") != null) {
                uAppId = (int) jsonObject.get("uAppId");//项目Id
            }
            int streamAppId;
            if(jsonObject.get("streamAppId") != null){
                streamAppId = (int)jsonObject.get("streamAppId");//发布所在的appid
            }
            //暂定先用字符串。要看业务接入使用那个而定。
            int uid;
            if(jsonObject.get("uid") != null){
                uid = (int)jsonObject.get("uid"); //用户Id
            }
            strUid = null;
            if(jsonObject.get("strUid") != null){
                strUid = jsonObject.get("strUid").toString();//字符串用户Id
            }

            channelName = null;
            if(jsonObject.get("channelName") != null){
                channelName = jsonObject.get("channelName").toString();
            }
            sid = 0;
            if(jsonObject.get("sid") != null){
                sid = (int)jsonObject.get("sid");
            }
            name = null;
            if(jsonObject.get("name") != null){
                name = jsonObject.get("name").toString();
            }
            type = 0;
            if(jsonObject.get("type") !=null){
                type= (int)jsonObject.get("type");
            }
            ipv4 = "";
            if(jsonObject.get("ipv4")!=null){
                ipv4 = jsonObject.get("ipv4").toString();
            }
            auth = 0;
            if(jsonObject.get("auth")!=null){
                auth = (int)jsonObject.get("auth");
            }
            sendTime = null;
            if(jsonObject.get("sendTime")!=null){
                sendTime = (Long)jsonObject.get("sendTime");
            }
            token = null;
            if(jsonObject.get("token")!=null){
                token = jsonObject.get("token").toString();
            }
            session = "";
            if(jsonObject.get("session")!=null){
                session = jsonObject.get("session").toString();
            }

            strParams = jsonObject.getJSONObject("strParams");
            u32Params = jsonObject.getJSONObject("u32Params");
            u64Params = jsonObject.getJSONObject("u64Params");
            return this;
        }
    }
}
