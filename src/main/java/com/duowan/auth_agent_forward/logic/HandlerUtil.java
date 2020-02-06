package com.duowan.auth_agent_forward.logic;

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
}
