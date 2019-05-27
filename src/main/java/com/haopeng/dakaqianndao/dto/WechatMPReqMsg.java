package com.haopeng.dakaqianndao.dto;

import com.alibaba.fastjson.JSONObject;

import javax.validation.constraints.NotBlank;

public class WechatMPReqMsg extends JSONObject {

    @NotBlank
    public String getFromUserName() {
        return this.getString("FromUserName");
    }

    @NotBlank
    public Integer getCreateTime() {
        return this.getInteger("CreateTime");
    }

    @NotBlank
    public String getMsgType() {
        return this.getString("MsgType");
    }

}
