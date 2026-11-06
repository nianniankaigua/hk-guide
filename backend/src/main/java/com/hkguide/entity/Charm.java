package com.hkguide.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

@Data
@TableName("hk_charm")
public class Charm {

    @TableId(type = IdType.INPUT)
    private Long id;

    private String slug;

    private String name;

    private String en;

    private Integer notches;

    private String effect;

    private String lore;

    private String location;

    private String notes;

    private String icon;

    /** JSON 数组字符串，存护符 slug 数组 */
    private String synergy;

    private String synergyNote;
}
