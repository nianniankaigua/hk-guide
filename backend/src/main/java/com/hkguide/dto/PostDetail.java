package com.hkguide.dto;

import java.time.LocalDateTime;
import java.util.List;

public record PostDetail(Long id, String title, String content, String author,
                         LocalDateTime createdAt, List<CommentView> comments) {
}
