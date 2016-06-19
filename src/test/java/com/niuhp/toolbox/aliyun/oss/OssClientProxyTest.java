package com.niuhp.toolbox.aliyun.oss;

import org.junit.Test;

/**
 * Created by niuhp on 2016/4/20.
 */
public class OssClientProxyTest {
  @Test
  public void testPutImg() {
    String imgPath = "C:\\Users\\niuhp\\Desktop/test.jpg";
    String endPoint = "oss-cn-beijing.aliyuncs.com";
    String accessid = "";          // AccessID
    String accesskey = "";     // AccessKey
    String buckName = "niuhp-ishow";
    String url = OssClientProxy.putImg(endPoint, accessid, accesskey, buckName, imgPath, 15);
    System.out.println(url);
  }
}
