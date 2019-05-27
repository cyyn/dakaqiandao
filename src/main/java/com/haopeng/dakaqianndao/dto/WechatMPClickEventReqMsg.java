package com.haopeng.dakaqianndao.dto;

import javax.validation.constraints.NotBlank;

public class WechatMPClickEventReqMsg extends WechatMPEventReqMsg{

    @NotBlank
    public String getEventKey() {
        return this.getString("EventKey");
    }
}
