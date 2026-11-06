package com.hkguide.dto;

import com.fasterxml.jackson.annotation.JsonInclude;

import java.util.List;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record CharmView(
        Long id,
        String slug,
        String name,
        String en,
        Integer notches,
        String effect,
        String lore,
        String location,
        String notes,
        String icon,
        List<String> synergy,
        String synergyNote) {
}
