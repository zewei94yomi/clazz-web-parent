package com.zzw.service;


import com.zzw.entity.Tag;

import java.util.List;

public interface TagService {

    //标签列表
    List<Tag> queryAll();

    //添加标签
    Tag insert(Tag tag);

    //删除标签
    void deleteById(Integer id);

    //标签详细
    Tag queryById(Integer id);
}
