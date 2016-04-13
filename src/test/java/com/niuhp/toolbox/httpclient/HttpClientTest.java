/**
 *
 */
package com.niuhp.toolbox.httpclient;

import org.junit.Test;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by niuhp on 2016/4/13.
 */
public class HttpClientTest {

    @Test
    public void testGet2String() {
        SimpleHttpClient client = new SimpleHttpClient();
        String url = "http://jianli.58.com/ajax/getAuthKey";
        Map<String, String> paraMap = new HashMap<String, String>();
        paraMap.put("m", "18701688710");
        while (true) {
            String content = client.doPost2String(url, paraMap);
            System.out.println(content);
            try {
                Thread.sleep(10);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            break;
        }
    }

    @Test
    public void testPost2String() {
        SimpleHttpClient client = new SimpleHttpClient();
        String url = "http://weixin.58.com/wxoauth/request?path=http://hero.fang.58.com/hero/share/004f0a29bd7041a98a104e8970c1bde4/&oauthid=EUixEEeEVBwFn4R2vQlQ&scope=snsapi_userinfo";
        String resultStr = client.doGet2String(url);
        System.out.println(resultStr);
        url = "http://hero.fang.58.com/hero/update";
        Map<String, String> paraMap = new HashMap<String, String>();
        paraMap.put("userid", "004f0a29bd7041a98a104e8970c1bde4");
        paraMap.put("score", "100");
        paraMap.put("token", "e90e8fd174264713b35160a10ef66c0c");

        resultStr = client.doPost2String(url, paraMap);
        System.out.println(resultStr);
    }
}
