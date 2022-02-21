package com.zzw.service.impl;

import com.zzw.dao.ClazzDAO;
import com.zzw.entity.Clazz;
import com.zzw.entity.Tag;
import com.zzw.feign.TagClient;
import com.zzw.service.ClazzService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class ClazzServiceImpl implements ClazzService {

    private final ClazzDAO clazzDAO;

    private final TagClient tagClient;

    @Autowired
    public ClazzServiceImpl(ClazzDAO clazzDAO, TagClient tagClient) {
        this.clazzDAO = clazzDAO;
        this.tagClient = tagClient;
    }

    @Override
    public List<Clazz> queryAll() {
        List<Clazz> clazzes = clazzDAO.queryAll();
        clazzes.forEach(clazz -> {
            Integer tagId = clazz.getTagId();
            Tag tag = tagClient.tag(tagId);
            clazz.setTag(tag);
        });
        return clazzes;
    }

    @Override
    public Clazz insert(Clazz clazz) {
        clazzDAO.insert(clazz);
        return clazz;
    }
}
