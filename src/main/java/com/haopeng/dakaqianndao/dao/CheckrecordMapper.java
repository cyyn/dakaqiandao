package com.haopeng.dakaqianndao.dao;

import com.haopeng.dakaqianndao.po.Checkrecord;
import org.apache.ibatis.annotations.Param;

public interface CheckrecordMapper {
    int deleteByPrimaryKey(Integer id);

    int insert(Checkrecord record);

    int insertSelective(Checkrecord record);

    Checkrecord selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(Checkrecord record);

    int updateByPrimaryKey(Checkrecord record);

//    custom api
    int deleteByOpenid(@Param("openid")String openid);
}