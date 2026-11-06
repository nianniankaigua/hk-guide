package com.hkguide.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.type.CollectionType;
import com.hkguide.dto.CharmView;
import com.hkguide.entity.Charm;
import com.hkguide.mapper.CharmMapper;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CharmService {

    private final CharmMapper charmMapper;
    private final ObjectMapper objectMapper;

    public CharmService(CharmMapper charmMapper, ObjectMapper objectMapper) {
        this.charmMapper = charmMapper;
        this.objectMapper = objectMapper;
    }

    /** 全部护符，按 id 升序 */
    public List<CharmView> listCharms() {
        return charmMapper.selectList(new LambdaQueryWrapper<Charm>()
                        .orderByAsc(Charm::getId))
                .stream()
                .map(this::toView)
                .toList();
    }

    private CharmView toView(Charm c) {
        return new CharmView(
                c.getId(),
                c.getSlug(),
                c.getName(),
                c.getEn(),
                c.getNotches(),
                c.getEffect(),
                c.getLore(),
                c.getLocation(),
                c.getNotes(),
                c.getIcon(),
                parseSynergy(c.getSynergy()),
                c.getSynergyNote());
    }

    private List<String> parseSynergy(String json) {
        if (json == null || json.isBlank()) {
            return null;
        }
        try {
            CollectionType type = objectMapper.getTypeFactory()
                    .constructCollectionType(List.class, String.class);
            return objectMapper.readValue(json, type);
        } catch (Exception e) {
            throw new IllegalStateException("synergy JSON 解析失败: " + json, e);
        }
    }
}
