package com.niuhp.toolbox.weixin.mp;

import java.util.List;

/**
 * Created by niuhaipeng on 2016/6/19.
 */
public class WeixinUser {
  private int subscribe;
  private String openid;
  private String nickname;
  private int sex;
  private String language;
  private String province;
  private String city;
  private String counttry;
  private String headimgurl;
  private long subscribe_time;
  private String privilege;
  private String unionid;
  private String remark;
  private int groupid;
  private List<Integer> tagid_list;

  public int getSubscribe() {
    return subscribe;
  }

  public void setSubscribe(int subscribe) {
    this.subscribe = subscribe;
  }

  public String getOpenid() {
    return openid;
  }

  public void setOpenid(String openid) {
    this.openid = openid;
  }

  public String getNickname() {
    return nickname;
  }

  public void setNickname(String nickname) {
    this.nickname = nickname;
  }

  public int getSex() {
    return sex;
  }

  public void setSex(int sex) {
    this.sex = sex;
  }

  public String getLanguage() {
    return language;
  }

  public void setLanguage(String language) {
    this.language = language;
  }

  public String getProvince() {
    return province;
  }

  public void setProvince(String province) {
    this.province = province;
  }

  public String getCity() {
    return city;
  }

  public void setCity(String city) {
    this.city = city;
  }

  public String getCounttry() {
    return counttry;
  }

  public void setCounttry(String counttry) {
    this.counttry = counttry;
  }

  public String getHeadimgurl() {
    return headimgurl;
  }

  public void setHeadimgurl(String headimgurl) {
    this.headimgurl = headimgurl;
  }

  public long getSubscribe_time() {
    return subscribe_time;
  }

  public void setSubscribe_time(long subscribe_time) {
    this.subscribe_time = subscribe_time;
  }

  public String getPrivilege() {
    return privilege;
  }

  public void setPrivilege(String privilege) {
    this.privilege = privilege;
  }

  public String getUnionid() {
    return unionid;
  }

  public void setUnionid(String unionid) {
    this.unionid = unionid;
  }

  public String getRemark() {
    return remark;
  }

  public void setRemark(String remark) {
    this.remark = remark;
  }

  public int getGroupid() {
    return groupid;
  }

  public void setGroupid(int groupid) {
    this.groupid = groupid;
  }

  public List<Integer> getTagid_list() {
    return tagid_list;
  }

  public void setTagid_list(List<Integer> tagid_list) {
    this.tagid_list = tagid_list;
  }
}
