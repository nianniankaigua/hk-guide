package com.hkguide.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record RegisterRequest(
        @NotBlank(message = "用户名不能为空")
        @Size(max = 32, message = "用户名长度不能超过32")
        String username,

        @NotBlank(message = "密码不能为空")
        @Size(min = 6, max = 64, message = "密码长度需在6-64之间")
        String password,

        @Size(max = 50, message = "昵称长度不能超过50")
        String nickname
) {
}
