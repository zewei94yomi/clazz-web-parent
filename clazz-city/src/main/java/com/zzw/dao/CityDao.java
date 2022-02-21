package com.zzw.dao;

import com.zzw.entity.City;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;


@Mapper //在工厂创建cityDao
public interface CityDao {

    //添加城市
    int insert(City city);

    //查询所有
    List<City> queryAll();

    //城市信息
    City queryById(Integer id);

}

