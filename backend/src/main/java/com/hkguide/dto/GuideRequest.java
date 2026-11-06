package com.hkguide.dto;

import com.fasterxml.jackson.databind.JsonNode;
import jakarta.validation.constraints.NotBlank;

import java.time.LocalDate;
import java.util.List;

public record GuideRequest(
        Long id,
        @NotBlank(message = "slug 不能为空") String slug,
        @NotBlank(message = "title 不能为空") String title,
        String subtitle,
        @NotBlank(message = "category 不能为空") String category,
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
        @NotBlank(message = "content 不能为空") String content,
        JsonNode theme) {
}
