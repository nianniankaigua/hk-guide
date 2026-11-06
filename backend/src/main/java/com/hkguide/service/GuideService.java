package com.hkguide.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.type.CollectionType;
import com.hkguide.dto.GuideView;
import com.hkguide.entity.Guide;
import com.hkguide.exception.NotFoundException;
import com.hkguide.mapper.GuideMapper;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class GuideService {

    private final GuideMapper guideMapper;
    private final ObjectMapper objectMapper;

    public GuideService(GuideMapper guideMapper, ObjectMapper objectMapper) {
        this.guideMapper = guideMapper;
        this.objectMapper = objectMapper;
    }

    /** 列表：不含 content，按 updatedAt 倒序；category 可空表示全部 */
    public List<GuideView> listGuides(String category) {
        LambdaQueryWrapper<Guide> wrapper = new LambdaQueryWrapper<Guide>()
                .select(Guide.class, field -> !"content".equals(field.getColumn()))
                .eq(category != null && !category.isBlank(), Guide::getCategory, category)
                .orderByDesc(Guide::getUpdatedAt);
        return guideMapper.selectList(wrapper).stream()
                .map(this::toView)
                .toList();
    }

    /** 详情：含 content，不存在抛 404 */
    public GuideView getGuide(String slug) {
        Guide guide = guideMapper.selectOne(new LambdaQueryWrapper<Guide>()
                .eq(Guide::getSlug, slug));
        if (guide == null) {
            throw new NotFoundException("攻略不存在");
        }
        return toView(guide);
    }

    /** 搜索：title/subtitle/summary/tags/content 五列 LIKE，返回列表视图，最多 50 条 */
    public List<GuideView> searchGuides(String q) {
        if (q == null || q.isBlank()) {
            return List.of();
        }
        LambdaQueryWrapper<Guide> wrapper = new LambdaQueryWrapper<Guide>()
                .select(Guide.class, field -> !"content".equals(field.getColumn()))
                .and(w -> w.like(Guide::getTitle, q)
                        .or().like(Guide::getSubtitle, q)
                        .or().like(Guide::getSummary, q)
                        .or().like(Guide::getTags, q)
                        .or().like(Guide::getContent, q))
                .orderByDesc(Guide::getUpdatedAt)
                .last("LIMIT 50");
        return guideMapper.selectList(wrapper).stream()
                .map(this::toView)
                .toList();
    }

    private GuideView toView(Guide g) {
        return new GuideView(
                g.getId(),
                g.getSlug(),
                g.getTitle(),
                g.getSubtitle(),
                g.getCategory(),
                g.getRegion(),
                g.getSummary(),
                parseTags(g.getTags()),
                g.getWatermark(),
                g.getCover(),
                parseNode(g.getBadge()),
                g.getDifficulty(),
                g.getViews(),
                g.getLikes(),
                g.getReadMinutes(),
                g.getUpdatedAt(),
                g.getFeatured(),
                g.getContent(),
                parseNode(g.getTheme()));
    }

    private List<String> parseTags(String json) {
        if (json == null || json.isBlank()) {
            return null;
        }
        try {
            CollectionType type = objectMapper.getTypeFactory()
                    .constructCollectionType(List.class, String.class);
            return objectMapper.readValue(json, type);
        } catch (Exception e) {
            throw new IllegalStateException("tags JSON 解析失败: " + json, e);
        }
    }

    private JsonNode parseNode(String json) {
        if (json == null || json.isBlank()) {
            return null;
        }
        try {
            return objectMapper.readTree(json);
        } catch (Exception e) {
            throw new IllegalStateException("JSON 解析失败: " + json, e);
        }
    }
}
