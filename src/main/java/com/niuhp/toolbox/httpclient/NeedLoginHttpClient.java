/**
 *
 */
package com.niuhp.toolbox.httpclient;

import org.apache.http.HttpResponse;
import org.apache.log4j.Logger;

import java.util.Map;

/**
 * Created by niuhp on 2016/4/13.
 */
public class NeedLoginHttpClient extends SimpleHttpClient {
  private static final Logger logger = Logger.getLogger(NeedLoginHttpClient.class);

  private volatile boolean login = false;
  private String loginUrl;
  private Map<String, String> loginPara;

  public NeedLoginHttpClient(String loginUrl, Map<String, String> loginPara) {
    this.loginUrl = loginUrl;
    this.loginPara = loginPara;
  }

  public void login() {
    if (login) {
      logger.warn(String.format("http client is logined in %s", loginUrl));
      return;
    }
    HttpResponse response = doPost(loginUrl, loginPara);
    closeHttpResponse(response);
    login = true;
  }

  public void clearLoginState() {
    login = false;
  }

  public boolean isLogin() {
    return login;
  }

}
