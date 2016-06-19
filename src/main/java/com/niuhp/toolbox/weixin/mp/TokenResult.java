package com.niuhp.toolbox.weixin.mp;

/**
 * Created by niuhaipeng on 2016/6/19.
 */
public class TokenResult {
  private String access_token;
  private int expires_in;
  private String refresh_token;
  private String openid;
  private String scope;
  private long generateTimeMills;

  public boolean isExpired() {
    return System.currentTimeMillis() - generateTimeMills > (expires_in - 10) * 1000;
  }

  public void copyFrom(TokenResult other) {
    this.access_token = other.access_token;
    this.expires_in = other.expires_in;
    this.refresh_token = other.refresh_token;
    this.openid = other.openid;
    this.scope = other.scope;
    this.generateTimeMills = other.generateTimeMills;
  }

  public String getAccess_token() {
    return access_token;
  }

  public void setAccess_token(String access_token) {
    this.access_token = access_token;
  }

  public int getExpires_in() {
    return expires_in;
  }

  public void setExpires_in(int expires_in) {
    this.expires_in = expires_in;
  }

  public String getRefresh_token() {
    return refresh_token;
  }

  public void setRefresh_token(String refresh_token) {
    this.refresh_token = refresh_token;
  }

  public String getOpenid() {
    return openid;
  }

  public void setOpenid(String openid) {
    this.openid = openid;
  }

  public String getScope() {
    return scope;
  }

  public void setScope(String scope) {
    this.scope = scope;
  }

  public long getGenerateTimeMills() {
    return generateTimeMills;
  }

  public void setGenerateTimeMills(long generateTimeMills) {
    this.generateTimeMills = generateTimeMills;
  }
}
