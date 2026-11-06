package com.hkguide.config;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.hkguide.entity.Charm;
import com.hkguide.mapper.CharmMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Component;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

/**
 * 启动时种子导入：hk_charm 表为空时从 classpath:charms.json 批量插入。
 */
@Component
public class CharmDataSeeder implements ApplicationRunner {

    private static final Logger log = LoggerFactory.getLogger(CharmDataSeeder.class);

    private final CharmMapper charmMapper;
    private final ObjectMapper objectMapper;

    public CharmDataSeeder(CharmMapper charmMapper, ObjectMapper objectMapper) {
        this.charmMapper = charmMapper;
        this.objectMapper = objectMapper;
    }

    @Override
    public void run(ApplicationArguments args) throws Exception {
        Long count = charmMapper.selectCount(null);
        if (count != null && count > 0) {
            log.info("hk_charm 表已有 {} 条数据，跳过种子导入", count);
            return;
        }
        ClassPathResource resource = new ClassPathResource("charms.json");
        try (InputStream in = resource.getInputStream()) {
            JsonNode root = objectMapper.readTree(in);
            List<Charm> charms = new ArrayList<>();
            for (JsonNode node : root) {
                charms.add(toEntity(node));
            }
            for (Charm charm : charms) {
                charmMapper.insert(charm);
            }
            log.info("hk_charm 种子导入完成，共插入 {} 条护符", charms.size());
        }
    }

    private Charm toEntity(JsonNode node) {
        Charm c = new Charm();
        c.setId(node.get("id").asLong());
        c.setSlug(node.get("slug").asText());
        c.setName(node.get("name").asText());
        c.setEn(textOrNull(node.get("en")));
        c.setNotches(node.hasNonNull("notches") ? node.get("notches").asInt() : 0);
        c.setEffect(textOrNull(node.get("effect")));
        c.setLore(textOrNull(node.get("lore")));
        c.setLocation(textOrNull(node.get("location")));
        c.setNotes(textOrNull(node.get("notes")));
        c.setIcon(textOrNull(node.get("icon")));
        JsonNode synergy = node.get("synergy");
        c.setSynergy(synergy == null || synergy.isNull() ? null : synergy.toString());
        c.setSynergyNote(textOrNull(node.get("synergyNote")));
        return c;
    }

    private String textOrNull(JsonNode node) {
        return node == null || node.isNull() ? null : node.asText();
    }
}
