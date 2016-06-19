package com.niuhp.toolbox.weixin.mp;

import com.google.gson.Gson;

import org.apache.log4j.Logger;
import org.junit.Before;
import org.junit.Test;

/**
 * Created by niuhaipeng on 2016/6/19.
 */
public class WeixinMpClientTest {
  private static final Logger logger = Logger.getLogger(WeixinMpClientTest.class);
  private WeixinMpClient weixinMpClient;

  @Before
  public void init() {
    weixinMpClient = new WeixinMpClient("wx1d9a742bae5ccb5a", "d4624c36b6795d1d99dcf0547af5443d");
  }

  @Test
  public void testGetUserInfo() {
    WeixinUser weixinUser = weixinMpClient.getUserInfoByOpenid("otKT8wzy8hFQIWDx4xY1TUYUnVdU", "zh-CN");
    Gson gson = new Gson();
    logger.info(gson.toJson(weixinUser));
  }
}
