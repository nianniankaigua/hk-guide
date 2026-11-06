package com.hkguide.controller;

import com.hkguide.service.FavoriteService;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/api/favorites")
public class FavoriteController {

    private final FavoriteService favoriteService;

    public FavoriteController(FavoriteService favoriteService) {
        this.favoriteService = favoriteService;
    }

    @GetMapping
    public Map<String, Object> list(Authentication authentication) {
        return Map.of("slugs", favoriteService.listSlugs(authentication.getName()));
    }

    @PostMapping("/{slug}")
    public Map<String, Boolean> add(Authentication authentication, @PathVariable String slug) {
        favoriteService.add(authentication.getName(), slug);
        return Map.of("favorited", true);
    }

    @DeleteMapping("/{slug}")
    public Map<String, Boolean> remove(Authentication authentication, @PathVariable String slug) {
        favoriteService.remove(authentication.getName(), slug);
        return Map.of("favorited", false);
    }
}
