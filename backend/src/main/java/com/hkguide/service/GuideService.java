package com.hkguide.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.type.CollectionType;
import com.hkguide.dto.GuideRequest;
import com.hkguide.dto.GuideView;
import com.hkguide.entity.Favorite;
import com.hkguide.entity.Guide;
import com.hkguide.exception.BizException;
import com.hkguide.exception.NotFoundException;
import com.hkguide.mapper.FavoriteMapper;
import com.hkguide.mapper.GuideMapper;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class GuideService {

    private final GuideMapper guideMapper;
    private final FavoriteMapper favoriteMapper;
    private final ObjectMapper objectMapper;

    public GuideService(GuideMapper guideMapper, FavoriteMapper favoriteMapper, ObjectMapper objectMapper) {
        this.guideMapper = guideMapper;
        this.favoriteMapper = favoriteMapper;
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

    /** 新建：id 不传则 max(id)+1，slug 重复抛 400 */
    public GuideView createGuide(GuideRequest req) {
        Long slugCount = guideMapper.selectCount(new LambdaQueryWrapper<Guide>()
                .eq(Guide::getSlug, req.slug()));
        if (slugCount != null && slugCount > 0) {
            throw new BizException("slug 已存在");
        }
        Guide g = new Guide();
        applyRequest(g, req);
        g.setId(req.id() != null ? req.id() : nextId());
        guideMapper.insert(g);
        return toView(g);
    }

    /** 更新：按 slug 更新全部可变字段（slug/id 不变），不存在抛 404 */
    public GuideView updateGuide(String slug, GuideRequest req) {
        Guide g = requireBySlug(slug);
        applyRequest(g, req);
        g.setSlug(slug);
        guideMapper.updateById(g);
        return toView(g);
    }

    /** 删除：同时清理 hk_favorite 里的收藏记录，不存在抛 404 */
    public void deleteGuide(String slug) {
        Guide g = requireBySlug(slug);
        guideMapper.deleteById(g.getId());
        favoriteMapper.delete(new LambdaQueryWrapper<Favorite>()
                .eq(Favorite::getGuideSlug, slug));
    }

    private Guide requireBySlug(String slug) {
        Guide guide = guideMapper.selectOne(new LambdaQueryWrapper<Guide>()
                .eq(Guide::getSlug, slug));
        if (guide == null) {
            throw new NotFoundException("攻略不存在");
        }
        return guide;
    }

    private Long nextId() {
        List<Object> result = guideMapper.selectObjs(new QueryWrapper<Guide>()
                .select("IFNULL(MAX(id), 0)"));
        long max = result.isEmpty() || result.get(0) == null ? 0L : ((Number) result.get(0)).longValue();
        return max + 1;
    }

    private void applyRequest(Guide g, GuideRequest req) {
        g.setSlug(req.slug());
        g.setTitle(req.title());
        g.setSubtitle(req.subtitle());
        g.setCategory(req.category());
        g.setRegion(req.region());
        g.setSummary(req.summary());
        g.setTags(writeJson(req.tags()));
        g.setWatermark(req.watermark());
        g.setCover(req.cover());
        g.setBadge(req.badge() == null || req.badge().isNull() ? null : req.badge().toString());
        g.setDifficulty(req.difficulty());
        g.setViews(req.views());
        g.setLikes(req.likes());
        g.setReadMinutes(req.readMinutes());
        g.setUpdatedAt(req.updatedAt());
        g.setFeatured(req.featured());
        g.setContent(req.content());
        g.setTheme(req.theme() == null || req.theme().isNull() ? null : req.theme().toString());
    }

    private String writeJson(Object value) {
        if (value == null) {
            return null;
        }
        try {
            return objectMapper.writeValueAsString(value);
        } catch (Exception e) {
            throw new IllegalStateException("JSON 序列化失败", e);
        }
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
