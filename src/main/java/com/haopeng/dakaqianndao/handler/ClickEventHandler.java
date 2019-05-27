package com.haopeng.dakaqianndao.handler;

import org.springframework.stereotype.Service;
import com.haopeng.dakaqianndao.constant.WechatEventKeyConstant;
import com.haopeng.dakaqianndao.dto.TextResMsg;
import com.haopeng.dakaqianndao.dto.WechatMPClickEventReqMsg;
import com.haopeng.dakaqianndao.dto.WechatMPEventReqMsg;
import com.haopeng.dakaqianndao.dto.WechatMPResMsg;
import com.haopeng.dakaqianndao.exception.ClientException;
import com.haopeng.dakaqianndao.service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.validation.constraints.NotBlank;

@Service
public class ClickEventHandler {

    private Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private UserService userService;

    public WechatMPResMsg handle(WechatMPClickEventReqMsg reqMsg) throws ClientException {

        WechatMPResMsg resMsg = null;

        String eventKey = reqMsg.getEventKey();

        switch (eventKey) {
            case WechatEventKeyConstant.CHECK_IN:
                logger.info("receive {}", WechatEventKeyConstant.CHECK_IN);
                resMsg = handleCheckIn(reqMsg);
                break;
            case WechatEventKeyConstant.CHECK_OUT:
                logger.info("receive {}", WechatEventKeyConstant.CHECK_OUT);
                resMsg = handleCheckOut(reqMsg);
                break;
            default:
                logger.info("it doesn't match any event key");
        }

        return resMsg;
    }

    private TextResMsg handleCheckIn(WechatMPEventReqMsg reqMsg) throws ClientException {
        @NotBlank String openid = reqMsg.getFromUserName();
        userService.checkIn(openid);
        TextResMsg textResMsg = new TextResMsg(openid, "thx, check in succeed");
        return textResMsg;
    }

    private TextResMsg handleCheckOut(WechatMPEventReqMsg reqMsg) throws ClientException {
        @NotBlank String openid = reqMsg.getFromUserName();
        userService.checkOut(openid);
        TextResMsg textResMsg = new TextResMsg(openid, "thx, check out succeed");
        return textResMsg;
    }
}
