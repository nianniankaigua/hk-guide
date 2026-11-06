package com.hkguide.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record CreatePostRequest(
        @NotBlank(message = "标题不能为空")
        @Size(max = 120, message = "标题长度不能超过120")
        String title,

        @NotBlank(message = "内容不能为空")
        String content
) {
}
