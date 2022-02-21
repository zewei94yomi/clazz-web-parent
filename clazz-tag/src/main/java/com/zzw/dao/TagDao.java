package com.zzw.dao;

import com.zzw.entity.Tag;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface TagDao {

    //标签列表
    List<Tag> queryAll();

    //添加标签
    void insert(Tag tag);

    //删除标签
    void deleteById(Integer id);

    //标签详细
    Tag queryById(Integer id);
}

