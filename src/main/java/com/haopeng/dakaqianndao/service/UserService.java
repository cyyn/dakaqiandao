package com.haopeng.dakaqianndao.service;

import com.haopeng.dakaqianndao.po.User;
import com.haopeng.dakaqianndao.vo.Position;
import com.haopeng.dakaqianndao.po.Userdetail;
import com.haopeng.dakaqianndao.exception.ClientException;


public interface UserService {
    void create(User user, Userdetail userDetail);

    void delete(String openid);

    void savePosition(String openId, Position position);

    Position loadPosition(String openid);

    void checkIn(String openid) throws ClientException;

    void checkOut(String openid) throws ClientException;

    User getUserFromWechatMP(String openId) throws ClientException;
}
