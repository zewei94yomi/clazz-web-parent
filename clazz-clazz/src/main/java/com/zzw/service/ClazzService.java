package com.zzw.service;

import com.zzw.entity.Clazz;

import java.util.List;

public interface ClazzService {

    // 查询所有班级
    List<Clazz> queryAll();

    // 添加班级
    Clazz insert(Clazz clazz);
}
