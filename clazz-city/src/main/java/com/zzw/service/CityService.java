package com.zzw.service;

import com.zzw.entity.City;

import java.util.List;

public interface CityService {


    //城市列表
    List<City> queryAll();

    //保存城市
    City insert(City city);

    //城市信息
    City queryById(Integer id);
}
