package com.hkguide.controller;

import com.hkguide.dto.GuideRequest;
import com.hkguide.dto.GuideView;
import com.hkguide.service.GuideService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/api/admin/guides")
public class AdminController {

    private final GuideService guideService;

    public AdminController(GuideService guideService) {
        this.guideService = guideService;
    }

    @PostMapping
    public GuideView create(@Valid @RequestBody GuideRequest request) {
        return guideService.createGuide(request);
    }

    @PutMapping("/{slug}")
    public GuideView update(@PathVariable String slug, @Valid @RequestBody GuideRequest request) {
        return guideService.updateGuide(slug, request);
    }

    @DeleteMapping("/{slug}")
    public Map<String, String> delete(@PathVariable String slug) {
        guideService.deleteGuide(slug);
        return Map.of("message", "删除成功");
    }
}
