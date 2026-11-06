package com.hkguide.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDate;

@Data
@TableName("hk_guide")
public class Guide {

    @TableId(type = IdType.INPUT)
    private Long id;

    private String slug;

    private String title;

    private String subtitle;

    private String category;

    private String region;

    private String summary;

    /** JSON 数组字符串，如 ["真菌荒地","三领主"] */
    private String tags;

    private String watermark;

    private String cover;

    /** JSON 字符串，可能是字符串或字符串数组 */
    private String badge;

    private Integer difficulty;

    private Long views;

    private Long likes;

    private Integer readMinutes;

    private LocalDate updatedAt;

    private Boolean featured;

    private String content;

    /** JSON 对象字符串，如 {"accent":"#a9e6da"} */
    private String theme;
}
