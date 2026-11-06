package com.hkguide.controller;

import com.hkguide.dto.GuideView;
import com.hkguide.service.GuideService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/guides")
public class GuideController {

    private final GuideService guideService;

    public GuideController(GuideService guideService) {
        this.guideService = guideService;
    }

    @GetMapping
    public List<GuideView> list(@RequestParam(required = false) String category) {
        return guideService.listGuides(category);
    }

    @GetMapping("/search")
    public List<GuideView> search(@RequestParam(required = false) String q) {
        return guideService.searchGuides(q);
    }

    @GetMapping("/{slug}")
    public GuideView detail(@PathVariable String slug) {
        return guideService.getGuide(slug);
    }
}
