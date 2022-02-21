package com.zzw.controller;

import com.zzw.entity.Clazz;
import com.zzw.service.ClazzService;
import org.apache.commons.io.FilenameUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/clazzs")
public class ClazzController {

    private static final Logger log = LoggerFactory.getLogger(ClazzController.class);
    private final ClazzService clazzService;

    @Autowired
    public ClazzController(ClazzService clazzService) {
        this.clazzService = clazzService;
    }

    @Value("${upload.dir}")
    private String realpath;

    // 添加班级
    @PostMapping
    public Clazz create(String name, MultipartFile logo, Integer tagId) throws IOException {
        log.debug("班级名称: {}", name);
        log.debug("班级log名称: {}", logo.getOriginalFilename());
        log.debug("标签id: {}", tagId);

        // 处理文件上传：
        //  1. 修改文件名称（改用UUID），避免文件重名问题
        String newFileName = UUID.randomUUID().toString().replace("-", "") + "."
                + FilenameUtils.getExtension(logo.getOriginalFilename());
        //  2. 保存文件
        logo.transferTo(new File(realpath, newFileName));

        // 保存班级信息
        Clazz clazz = new Clazz();
        clazz.setName(name);
        clazz.setPath(newFileName);
        clazz.setTagId(tagId);
        return clazzService.insert(clazz);
    }

    // 班级列表接口
    @GetMapping
    public List<Clazz> clazzs() {
        return clazzService.queryAll();
    }
}
