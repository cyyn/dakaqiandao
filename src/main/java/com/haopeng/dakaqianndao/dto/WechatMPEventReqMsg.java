package com.haopeng.dakaqianndao.dto;


import javax.validation.constraints.NotBlank;

public class WechatMPEventReqMsg extends WechatMPReqMsg{

    @NotBlank
    public  String getEvent(){
        return this.getString("Event");
    }
}
