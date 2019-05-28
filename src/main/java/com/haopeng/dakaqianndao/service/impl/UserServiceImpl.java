package com.haopeng.dakaqianndao.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.grum.geocalc.Coordinate;
import com.grum.geocalc.EarthCalc;
import com.grum.geocalc.Point;
import com.haopeng.dakaqianndao.api.WechatMPApi;
import com.haopeng.dakaqianndao.component.UserPosition;
import com.haopeng.dakaqianndao.component.WechatMPVariable;
import com.haopeng.dakaqianndao.constant.ErrConstant;
import com.haopeng.dakaqianndao.constant.WechatConstant;
import com.haopeng.dakaqianndao.dao.CheckrecordMapper;
import com.haopeng.dakaqianndao.dao.UserMapper;
import com.haopeng.dakaqianndao.dao.UserdetailMapper;
import com.haopeng.dakaqianndao.enumeration.CheckType;
import com.haopeng.dakaqianndao.enumeration.UserStatus;
import com.haopeng.dakaqianndao.exception.ClientException;
import com.haopeng.dakaqianndao.po.Checkrecord;
import com.haopeng.dakaqianndao.po.User;
import com.haopeng.dakaqianndao.po.Userdetail;
import com.haopeng.dakaqianndao.service.UserService;
import com.haopeng.dakaqianndao.vo.Position;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;

@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private UserPosition userPosition;

    @Autowired
    private WechatMPApi wechatMPApi;

    @Autowired
    private WechatMPVariable wechatMPVariable;

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private UserdetailMapper userdetailMapper;

    @Autowired
    private CheckrecordMapper checkrecordMapper;

    @Value("${check.latitude}")
    private Double checkLatitude;

    @Value("${check.longitude}")
    private Double checkLongitude;

    @Value("${check.distance}")
    private Double checkDistance;

    @Override
    @Transactional
    public void create(User user, Userdetail userDetail) {
        userMapper.insert(user);
        userdetailMapper.insert(userDetail);
    }


    @Override
    @Transactional
    public void delete(String openid) {
        checkrecordMapper.deleteByOpenid(openid);
        userdetailMapper.deleteByPrimaryKey(openid);
        userMapper.deleteByPrimaryKey(openid);
    }

    @Override
    public void savePosition(String openId, Position position) {
        userPosition.put(openId, position);
    }

    @Override
    public Position loadPosition(String openId) {
        Position position = userPosition.get(openId);
        return position;
    }

    @Override
    @Transactional
    public void checkIn(String openid) throws ClientException {

        checkPosition(openid);

        //todo use redis? or mybatis cache second level
        User user = userMapper.selectByPrimaryKey(openid);
        if (user == null){
            throw new  ClientException(openid, ErrConstant.USER_NOT_EXIST, ErrConstant.USER_NOT_EXIST_TEXT);
        }
        Byte status = user.getStatus();
        if (status == UserStatus.OnWorking.ordinal()){
            throw new ClientException(openid, ErrConstant.ALREADY_CHECK_IN, ErrConstant.ALREADY_CHECK_IN_TEXT);
        }
        Checkrecord checkRecord = new Checkrecord();
        checkRecord.setOpenid(openid);
        checkRecord.setType((byte) CheckType.CheckIn.ordinal());
        checkRecord.setTime(new Date());
        checkrecordMapper.insert(checkRecord);

        user.setStatus((byte) UserStatus.OnWorking.ordinal());
        userMapper.updateByPrimaryKey(user);
    }

    @Override
    @Transactional
    public void checkOut(String openid) throws ClientException {

        checkPosition(openid);

        User user = userMapper.selectByPrimaryKey(openid);
        if (user == null){
            throw new  ClientException(openid, ErrConstant.USER_NOT_EXIST, ErrConstant.USER_NOT_EXIST_TEXT);
        }
        Byte status = user.getStatus();

        if (status == UserStatus.OffWorking.ordinal()){
            throw new ClientException(openid, ErrConstant.ALREADY_CHECK_OUT, ErrConstant.ALREADY_CHECK_OUT_TEXT);
        }
        Checkrecord checkRecord = new Checkrecord();
        checkRecord.setOpenid(openid);
        checkRecord.setType((byte) CheckType.CheckOut.ordinal());
        checkRecord.setTime(new Date());
        checkrecordMapper.insert(checkRecord);

        user.setStatus((byte) UserStatus.OffWorking.ordinal());
        userMapper.updateByPrimaryKey(user);
    }

    @Override
    public User getUserFromWechatMP(String openid) throws ClientException {
        JSONObject userInfo = wechatMPApi.getUserInfo(wechatMPVariable.getAccessToken(), openid, WechatConstant.ZH_CN_LANG);
        openid = userInfo.getString("openid");
        if (openid == null){
            throw new ClientException(openid, ErrConstant.CANNOT_GET_USER_FROM_WECHATMP, ErrConstant.CANNOT_GET_USER_FROM_WECHATMP_TEXT);
        }
        User user = new User();
        user.setOpenid(openid);
        user.setNickname(userInfo.getString("nickname"));
        user.setGender(userInfo.getByte("sex"));
        user.setAvatarUrl(userInfo.getString("headimgurl"));
        user.setStatus(((byte) UserStatus.OffWorking.ordinal()));
        return user;
    }

    private void checkPosition(String openid) throws ClientException {
        Position position = loadPosition(openid);

        if (position == null){
            throw new ClientException(openid, ErrConstant.CANNOT_GET_POSITION, ErrConstant.CANNOT_GET_POSITION_TEXT);
        }

        Coordinate lat = Coordinate.fromDegrees(checkLatitude);
        Coordinate lng = Coordinate.fromDegrees(checkLongitude);
        Point checkPosition = Point.at(lat, lng);

        lat = Coordinate.fromDegrees(position.getLatitude());
        lng = Coordinate.fromDegrees(position.getLongitude());
        Point userPosition = Point.at(lat, lng);

        double distance = EarthCalc.harvesineDistance(checkPosition, userPosition); //in meters
        if (distance > checkDistance) {
            throw new ClientException(openid, ErrConstant.EXCEED_DISTANCE, ErrConstant.EXCEED_DISTANCE_TEXT);
        }
    }
}
