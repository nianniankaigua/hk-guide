package com.hkguide.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.hkguide.entity.Favorite;
import com.hkguide.entity.User;
import com.hkguide.exception.BizException;
import com.hkguide.mapper.FavoriteMapper;
import com.hkguide.mapper.UserMapper;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class FavoriteService {

    private final FavoriteMapper favoriteMapper;
    private final UserMapper userMapper;

    public FavoriteService(FavoriteMapper favoriteMapper, UserMapper userMapper) {
        this.favoriteMapper = favoriteMapper;
        this.userMapper = userMapper;
    }

    public List<String> listSlugs(String username) {
        Long userId = requireUserId(username);
        return favoriteMapper.selectList(new LambdaQueryWrapper<Favorite>()
                        .eq(Favorite::getUserId, userId)
                        .orderByDesc(Favorite::getCreatedAt))
                .stream()
                .map(Favorite::getGuideSlug)
                .toList();
    }

    public void add(String username, String slug) {
        Long userId = requireUserId(username);
        Long count = favoriteMapper.selectCount(new LambdaQueryWrapper<Favorite>()
                .eq(Favorite::getUserId, userId)
                .eq(Favorite::getGuideSlug, slug));
        if (count != null && count > 0) {
            return; // 幂等：已收藏则直接返回
        }
        Favorite favorite = new Favorite();
        favorite.setUserId(userId);
        favorite.setGuideSlug(slug);
        favorite.setCreatedAt(LocalDateTime.now());
        favoriteMapper.insert(favorite);
    }

    public void remove(String username, String slug) {
        Long userId = requireUserId(username);
        favoriteMapper.delete(new LambdaQueryWrapper<Favorite>()
                .eq(Favorite::getUserId, userId)
                .eq(Favorite::getGuideSlug, slug));
    }

    private Long requireUserId(String username) {
        User user = userMapper.selectOne(new LambdaQueryWrapper<User>()
                .eq(User::getUsername, username));
        if (user == null) {
            throw new BizException("用户不存在");
        }
        return user.getId();
    }
}
