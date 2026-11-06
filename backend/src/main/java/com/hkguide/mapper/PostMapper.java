package com.hkguide.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.hkguide.entity.Post;
import org.apache.ibatis.annotations.Select;

import java.util.List;
import java.util.Map;

public interface PostMapper extends BaseMapper<Post> {

    /** 每个帖子的评论数：LEFT JOIN hk_comment 按 post_id 分组统计 */
    @Select("SELECT p.id AS post_id, COUNT(c.id) AS cnt FROM hk_post p "
            + "LEFT JOIN hk_comment c ON c.post_id = p.id GROUP BY p.id")
    List<Map<String, Object>> countCommentsByPost();
}
