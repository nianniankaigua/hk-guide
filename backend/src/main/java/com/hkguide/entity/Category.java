package com.hkguide.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

@Data
@TableName("hk_category")
public class Category {

    @TableId(type = IdType.INPUT)
    private String id;

    private String name;

    private String en;

    private String description;

    private Integer sortOrder;
}
