package com.zzw.entity;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.io.Serializable;
import java.util.Date;

/**
 * (Tag)实体类
 *
 * @author makejava
 * @since 2021-09-12 14:51:09
 */
public class Tag implements Serializable {
    private static final long serialVersionUID = 894471832359949692L;

    private Integer id;

    private String name;

    private String type;

    @JsonProperty("created_at")
    private Date createdate;


    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public Date getCreatedate() {
        return createdate;
    }

    public void setCreatedate(Date createdate) {
        this.createdate = createdate;
    }

}

