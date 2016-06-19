package com.niuhp.toolbox.weixin.mp;

/**
 * Created by niuhaipeng on 2016/6/19.
 */
public class ErrorResult {
  private int errcode;
  private String errmsg;

  public int getErrcode() {
    return errcode;
  }

  public void setErrcode(int errcode) {
    this.errcode = errcode;
  }

  public String getErrmsg() {
    return errmsg;
  }

  public void setErrmsg(String errmsg) {
    this.errmsg = errmsg;
  }

  @Override
  public String toString() {
    return "ErrorResult{" +
            "errcode=" + errcode +
            ", errmsg='" + errmsg + '\'' +
            '}';
  }
}
