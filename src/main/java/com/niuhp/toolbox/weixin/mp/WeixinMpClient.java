package com.niuhp.toolbox.weixin.mp;

import com.google.gson.Gson;

import com.niuhp.core.util.CommonUtil;
import com.niuhp.toolbox.httpclient.SimpleHttpClient;

import org.apache.log4j.Logger;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by niuhaipeng on 2016/6/19.
 */
public class WeixinMpClient {

  private static final Logger logger = Logger.getLogger(WeixinMpClient.class);

  private String appid;
  private String secret;

  public WeixinMpClient(String appid, String secret) {
    this.appid = appid;
    this.secret = secret;
  }

  public TokenResult getAccessToken() {
    SimpleHttpClient httpClient = new SimpleHttpClient();
    String url = "https://api.weixin.qq.com/cgi-bin/token";
    Map<String, String> paraMap = new HashMap<String, String>();
    paraMap.put("appid", appid);
    paraMap.put("secret", secret);
    paraMap.put("grant_type", "client_credential");
    String jsonStr = httpClient.doGet2String(url, paraMap);
    return parseTokenResultFrom(jsonStr);
  }

  public TokenResult getAccessToken(String code) {
    SimpleHttpClient httpClient = new SimpleHttpClient();
    String url = "https://api.weixin.qq.com/sns/oauth2/access_token";
    Map<String, String> paraMap = new HashMap<String, String>();
    paraMap.put("appid", appid);
    paraMap.put("secret", secret);
    paraMap.put("code", code);
    paraMap.put("grant_type", "authorization_code");
    String jsonStr = httpClient.doGet2String(url, paraMap);
    return parseTokenResultFrom(jsonStr);
  }

  public TokenResult refreshAccessToken(TokenResult accessToken) {
    String url = "https://api.weixin.qq.com/sns/oauth2/refresh_token";
    Map<String, String> paraMap = new HashMap<String, String>();
    paraMap.put("appid", appid);
    paraMap.put("grant_type", "refresh_token");
    paraMap.put("refresh_token", accessToken.getRefresh_token());
    SimpleHttpClient httpClient = new SimpleHttpClient();
    String jsonStr = httpClient.doGet2String(url, paraMap);
    return parseTokenResultFrom(jsonStr);
  }

  public WeixinUser getUserInfoByOpenid(String openid) {
    return getUserInfoByOpenid(openid, null);
  }

  public WeixinUser getUserInfoByOpenid(String openid, String lang) {
    TokenResult tokenResult = getAccessToken();
    return getUserInfo(tokenResult, openid, lang);
  }

  public WeixinUser getUserInfoByCode(String code) {
    TokenResult tokenResult = getAccessToken(code);
    return getUserInfo(tokenResult);
  }

  public WeixinUser getUserInfo(TokenResult tokenResult) {
    return getUserInfo(tokenResult, "zh_CN");
  }

  public WeixinUser getUserInfo(TokenResult tokenResult, String lang) {
    checkAndValidate(tokenResult);
    SimpleHttpClient httpClient = new SimpleHttpClient();
    String url = "https://api.weixin.qq.com/sns/userinfo";
    Map<String, String> paraMap = new HashMap<String, String>();
    paraMap.put("access_token", tokenResult.getAccess_token());
    paraMap.put("openid", tokenResult.getOpenid());
    paraMap.put("lang", lang);
    String jsonStr = httpClient.doGet2String(url, paraMap);
    return parseWeixinUserFrom(jsonStr);
  }

  public WeixinUser getUserInfo(TokenResult tokenResult, String openid, String lang) {
    checkAndValidate(tokenResult);
    SimpleHttpClient httpClient = new SimpleHttpClient();
    String url = "https://api.weixin.qq.com/cgi-bin/user/info";
    Map<String, String> paraMap = new HashMap<String, String>();
    paraMap.put("access_token", tokenResult.getAccess_token());
    paraMap.put("openid", openid);
    if (!CommonUtil.isBlank(lang)) {
      paraMap.put("lang", lang);
    }
    String jsonStr = httpClient.doGet2String(url, paraMap);
    return parseWeixinUserFrom(jsonStr);
  }

  private void checkAndValidate(TokenResult tokenResult) {
    if (tokenResult.isExpired()) {
      TokenResult newToken;
      String refresh_token = tokenResult.getRefresh_token();
      if (CommonUtil.isBlank(refresh_token)) {
        newToken = getAccessToken();
      } else {
        newToken = refreshAccessToken(tokenResult);
      }
      tokenResult.copyFrom(newToken);
    }
  }

  private static TokenResult parseTokenResultFrom(String jsonStr) {
    Gson gson = new Gson();
    try {
      Class<TokenResult> clazz = TokenResult.class;
      TokenResult tokenResult = gson.fromJson(jsonStr, clazz);
      tokenResult.setGenerateTimeMills(System.currentTimeMillis());
      return tokenResult;
    } catch (Exception e) {
      Class<ErrorResult> clazz = ErrorResult.class;
      ErrorResult errorResult = gson.fromJson(jsonStr, clazz);
      logger.error(String.format("parse TokenResult error,errorResult=%s", errorResult), e);
    }
    return null;
  }

  private static WeixinUser parseWeixinUserFrom(String jsonStr) {
    Gson gson = new Gson();
    try {
      Class<WeixinUser> clazz = WeixinUser.class;
      return gson.fromJson(jsonStr, clazz);
    } catch (Exception e) {
      Class<ErrorResult> clazz = ErrorResult.class;
      ErrorResult errorResult = gson.fromJson(jsonStr, clazz);
      logger.error(String.format("parse WeixinUser error,errorResult=%s", errorResult), e);
    }
    return null;
  }
}
