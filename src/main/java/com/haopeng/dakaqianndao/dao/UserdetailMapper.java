package com.haopeng.dakaqianndao.dao;

import com.haopeng.dakaqianndao.po.Userdetail;

public interface UserdetailMapper {
    int deleteByPrimaryKey(String openid);

    int insert(Userdetail record);

    int insertSelective(Userdetail record);

    Userdetail selectByPrimaryKey(String openid);

    int updateByPrimaryKeySelective(Userdetail record);

    int updateByPrimaryKey(Userdetail record);
}