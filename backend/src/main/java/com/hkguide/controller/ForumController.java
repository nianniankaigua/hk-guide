package com.hkguide.controller;

import com.hkguide.dto.CommentView;
import com.hkguide.dto.CreateCommentRequest;
import com.hkguide.dto.CreatePostRequest;
import com.hkguide.dto.PostDetail;
import com.hkguide.dto.PostView;
import com.hkguide.service.ForumService;
import jakarta.validation.Valid;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/api/forum/posts")
public class ForumController {

    private final ForumService forumService;

    public ForumController(ForumService forumService) {
        this.forumService = forumService;
    }

    @GetMapping
    public Map<String, Object> list() {
        return Map.of("posts", forumService.listPosts());
    }

    @GetMapping("/{id}")
    public PostDetail detail(@PathVariable Long id) {
        return forumService.getPost(id);
    }

    @PostMapping
    public PostView create(Authentication authentication, @Valid @RequestBody CreatePostRequest request) {
        return forumService.createPost(authentication.getName(), request);
    }

    @PostMapping("/{id}/comments")
    public CommentView comment(Authentication authentication, @PathVariable Long id,
                               @Valid @RequestBody CreateCommentRequest request) {
        return forumService.addComment(authentication.getName(), id, request);
    }
}
