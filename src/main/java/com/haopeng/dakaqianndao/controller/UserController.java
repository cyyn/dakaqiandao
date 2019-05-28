package com.haopeng.dakaqianndao.controller;

import com.alibaba.fastjson.JSONObject;
import com.grum.geocalc.Coordinate;
import com.grum.geocalc.EarthCalc;
import com.grum.geocalc.Point;
import com.haopeng.dakaqianndao.dao.CheckrecordMapper;
import com.haopeng.dakaqianndao.dao.UserMapper;
import com.haopeng.dakaqianndao.exception.WebClientException;
import com.haopeng.dakaqianndao.po.Checkrecord;
import com.haopeng.dakaqianndao.po.User;
import com.haopeng.dakaqianndao.service.WechatMPService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;

import java.util.Date;


@RestController
@RequestMapping("/user")
@CrossOrigin
public class UserController {

    @Value("${check.latitude}")
    private Double checkLatitude;

    @Value("${check.longitude}")
    private Double checkLongitude;

    @Value("${check.distance}")
    private Double checkDistance;

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private CheckrecordMapper checkrecordMapper;

    @Autowired
    private WechatMPService wechatMPService;

    @GetMapping("/canCheck")
    public void canCheck(@RequestParam Double latitude,
                         @RequestParam Double longitude) throws WebClientException {
        Coordinate lat = Coordinate.fromDegrees(checkLatitude);
        Coordinate lng = Coordinate.fromDegrees(checkLongitude);
        Point checkPosition = Point.at(lat, lng);

        lat = Coordinate.fromDegrees(latitude);
        lng = Coordinate.fromDegrees(longitude);
        Point userPosition = Point.at(lat, lng);

        double distance = EarthCalc.harvesineDistance(checkPosition, userPosition); //in meters
        if (distance > checkDistance) {
            throw new WebClientException("不在打卡范围");
        }
    }

    @GetMapping("/getCurrentStatus")
    public Byte getCurrentStatus(String openid){
        User user = userMapper.selectByPrimaryKey(openid);
        Byte status = user.getStatus();
        return status;
    }

    @PostMapping("/check")
    public Integer check(@RequestParam String openid,
                         @RequestParam Byte type){
        //todo check if is the right status
        Checkrecord checkRecord = new Checkrecord();
        checkRecord.setOpenid(openid);
        checkRecord.setType(type);
        checkRecord.setTime(new Date());
        checkrecordMapper.insert(checkRecord);
        Integer id = checkRecord.getId();
        return id;
    }

    @GetMapping("/getToken")
    public JSONObject getToken(@RequestParam String code){
        JSONObject jsonObject = wechatMPService.getUserAccessToken(code);
        return jsonObject;
    }

}
