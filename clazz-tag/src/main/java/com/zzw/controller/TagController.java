package com.zzw.controller;


import com.zzw.entity.Tag;
import com.zzw.service.TagService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/tags")
public class TagController {

    private final TagService tagService;

    @Autowired
    public TagController(TagService tagService) {
        this.tagService = tagService;
    }


    //标签列表
    @GetMapping
    public List<Tag> tags() {
        return tagService.queryAll();
    }

    //添加标签
    @PostMapping
    public Tag create(@RequestBody Tag tag) {
        return tagService.insert(tag);
    }

    //删除标签
    @DeleteMapping("{id}")
    public void delete(@PathVariable("id") Integer id) {
        tagService.deleteById(id);
    }

    //标签信息
    @GetMapping("{id}")
    public Tag tag(@PathVariable("id") Integer id) {
        return tagService.queryById(id);
    }
}

