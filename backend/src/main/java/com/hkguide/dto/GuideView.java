package com.hkguide.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.JsonNode;

import java.time.LocalDate;
import java.util.List;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record GuideView(
        Long id,
        String slug,
        String title,
        String subtitle,
        String category,
        String region,
        String summary,
        List<String> tags,
        String watermark,
        String cover,
        JsonNode badge,
        Integer difficulty,
        Long views,
        Long likes,
        Integer readMinutes,
        LocalDate updatedAt,
        Boolean featured,
        String content,
        JsonNode theme) {
}
