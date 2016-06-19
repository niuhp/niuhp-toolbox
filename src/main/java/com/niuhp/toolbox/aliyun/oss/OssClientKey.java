package com.niuhp.toolbox.aliyun.oss;

/**
 * Created by niuhp on 2016/4/20.
 */
public class OssClientKey {
  private String endPoint;
  private String accessId;
  private String accessKey;

  public OssClientKey(String endPoint, String accessId, String accessKey) {
    this.endPoint = endPoint;
    this.accessId = accessId;
    this.accessKey = accessKey;
  }

  public String getEndPoint() {
    return endPoint;
  }

  public void setEndPoint(String endPoint) {
    this.endPoint = endPoint;
  }

  public String getAccessId() {
    return accessId;
  }

  public void setAccessId(String accessId) {
    this.accessId = accessId;
  }

  public String getAccessKey() {
    return accessKey;
  }

  public void setAccessKey(String accessKey) {
    this.accessKey = accessKey;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;

    OssClientKey that = (OssClientKey) o;

    if (!endPoint.equals(that.endPoint)) return false;
    if (!accessId.equals(that.accessId)) return false;
    return accessKey.equals(that.accessKey);

  }

  @Override
  public int hashCode() {
    int result = endPoint.hashCode();
    result = 31 * result + accessId.hashCode();
    result = 31 * result + accessKey.hashCode();
    return result;
  }
}
