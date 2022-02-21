package com.zzw.controller;

import com.zzw.entity.City;
import com.zzw.service.CityService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/cities")
public class CityController {


    //使用cityService
    private final CityService cityService;

    @Autowired
    public CityController(CityService cityService) {
        this.cityService = cityService;
    }


    //城市列表
    @GetMapping
    public List<City> cities() {
        return cityService.queryAll();
    }

    //添加城市
    @PostMapping
    public City create(@RequestBody City city) {
        return cityService.insert(city);
    }

    //城市信息
    @GetMapping("{id}")
    public City city(@PathVariable("id") Integer id) {
        return cityService.queryById(id);
    }
}
