package com.zzw.dao;

import com.zzw.entity.Clazz;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface ClazzDAO {

    // 查询班级列表
    List<Clazz> queryAll();

    // 添加班级
    void insert(Clazz clazz);
}
