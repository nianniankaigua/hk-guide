package com.hkguide.dto;

import java.time.LocalDateTime;

public record PostSummary(Long id, String title, String excerpt, String author,
                          LocalDateTime createdAt, long commentCount) {
}
