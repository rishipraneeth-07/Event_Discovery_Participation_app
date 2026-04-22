package com.college.eventapp.controller;

import com.college.eventapp.dto.AppContentResponseDTO;
import com.college.eventapp.service.AppContentService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/app")
public class AppContentController {

    private final AppContentService appContentService;

    public AppContentController(AppContentService appContentService) {
        this.appContentService = appContentService;
    }

    @GetMapping("/content")
    public AppContentResponseDTO getContent() {
        return appContentService.getAppContent();
    }
}
