package com.hkguide.controller;

import com.hkguide.dto.CharmView;
import com.hkguide.service.CharmService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/charms")
public class CharmController {

    private final CharmService charmService;

    public CharmController(CharmService charmService) {
        this.charmService = charmService;
    }

    @GetMapping
    public List<CharmView> list() {
        return charmService.listCharms();
    }
}
