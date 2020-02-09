package com.duowan.auth_agent_forward;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.regex.Pattern;

@SpringBootTest
class AuthAgentForwardApplicationTests {
	@Test
	void testBase64(){

		String token2 ="AAAAAgAAAFAAAAB7AAUxMDAwMAABAAN1cmwAEmh0dHBzOi8vd3d3Lnl5LmNvbQAAAAABcB3PJoAAAAPoRX8fG862RSj6-LZD6DqxXHsV8cw=";
		String token1 = "ADIAAAB7AAAAAAAAA-cAAAFwHc8mgAAAJxAAAgECANb4Q2fxhMlaU-om5Jul4g0d4io=";
		System.out.println(isBase64Encode(token1));
	}
	@Test
	public static boolean isBase64Encode(String content){
		if(content.length()%4!=0){
			return false;
		}
		String pattern = "^[a-zA-Z0-9/+]*={0,2}$";
		return Pattern.matches(pattern, content);
	}
}
