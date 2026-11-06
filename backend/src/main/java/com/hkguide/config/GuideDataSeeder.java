package com.hkguide.config;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.hkguide.entity.Guide;
import com.hkguide.mapper.GuideMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Component;

import java.io.InputStream;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * 启动时种子导入：hk_guide 表为空时从 classpath:guides.json 批量插入。
 */
@Component
public class GuideDataSeeder implements ApplicationRunner {

    private static final Logger log = LoggerFactory.getLogger(GuideDataSeeder.class);

    private final GuideMapper guideMapper;
    private final ObjectMapper objectMapper;

    public GuideDataSeeder(GuideMapper guideMapper, ObjectMapper objectMapper) {
        this.guideMapper = guideMapper;
        this.objectMapper = objectMapper;
    }

    @Override
    public void run(ApplicationArguments args) throws Exception {
        Long count = guideMapper.selectCount(null);
        if (count != null && count > 0) {
            log.info("hk_guide 表已有 {} 条数据，跳过种子导入", count);
            return;
        }
        ClassPathResource resource = new ClassPathResource("guides.json");
        try (InputStream in = resource.getInputStream()) {
            JsonNode root = objectMapper.readTree(in);
            List<Guide> guides = new ArrayList<>();
            for (JsonNode node : root) {
                guides.add(toEntity(node));
            }
            for (Guide guide : guides) {
                guideMapper.insert(guide);
            }
            log.info("hk_guide 种子导入完成，共插入 {} 条攻略", guides.size());
        }
    }

    private Guide toEntity(JsonNode node) {
        Guide g = new Guide();
        g.setId(node.get("id").asLong());
        g.setSlug(node.get("slug").asText());
        g.setTitle(node.get("title").asText());
        g.setSubtitle(textOrNull(node.get("subtitle")));
        g.setCategory(node.get("category").asText());
        g.setRegion(textOrNull(node.get("region")));
        g.setSummary(textOrNull(node.get("summary")));
        g.setTags(jsonOrNull(node.get("tags")));
        g.setWatermark(textOrNull(node.get("watermark")));
        g.setCover(textOrNull(node.get("cover")));
        g.setBadge(jsonOrNull(node.get("badge")));
        g.setDifficulty(node.hasNonNull("difficulty") ? node.get("difficulty").asInt() : 0);
        g.setViews(node.hasNonNull("views") ? node.get("views").asLong() : 0L);
        g.setLikes(node.hasNonNull("likes") ? node.get("likes").asLong() : 0L);
        g.setReadMinutes(node.hasNonNull("readMinutes") ? node.get("readMinutes").asInt() : 0);
        g.setUpdatedAt(node.hasNonNull("updatedAt") ? LocalDate.parse(node.get("updatedAt").asText()) : null);
        g.setFeatured(node.hasNonNull("featured") ? node.get("featured").asBoolean() : null);
        g.setContent(textOrNull(node.get("content")));
        g.setTheme(jsonOrNull(node.get("theme")));
        return g;
    }

    private String textOrNull(JsonNode node) {
        return node == null || node.isNull() ? null : node.asText();
    }

    /** 保留原始 JSON 结构（数组/对象/字符串）存入数据库 */
    private String jsonOrNull(JsonNode node) {
        return node == null || node.isNull() ? null : node.toString();
    }
}
