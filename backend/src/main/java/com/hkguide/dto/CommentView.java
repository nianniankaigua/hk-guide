package com.hkguide.dto;

import java.time.LocalDateTime;

public record CommentView(Long id, String author, String content, LocalDateTime createdAt) {
}
