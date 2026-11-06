package com.hkguide.dto;

import java.time.LocalDateTime;

public record PostView(Long id, String title, String content, String author, LocalDateTime createdAt) {
}
