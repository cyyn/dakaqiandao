package com.haopeng.dakaqianndao.dto;

import javax.validation.constraints.NotBlank;

public class WechatMPCommonReqMsg extends WechatMPReqMsg {

    @NotBlank
    public Long getMsgId() {
        return this.getLong("MsgId");
    }
}
