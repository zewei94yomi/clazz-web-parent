package com.zzw.feign;

import com.zzw.entity.Tag;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient("TAGS")
public interface TagClient {

    // 根据标签id，查询标签信息
    @GetMapping("/tags/{id}")
    Tag tag(@PathVariable("id") Integer id);

}
