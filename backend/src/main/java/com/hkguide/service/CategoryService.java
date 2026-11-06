package com.hkguide.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.hkguide.dto.CategoryView;
import com.hkguide.entity.Category;
import com.hkguide.mapper.CategoryMapper;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CategoryService {

    private final CategoryMapper categoryMapper;

    public CategoryService(CategoryMapper categoryMapper) {
        this.categoryMapper = categoryMapper;
    }

    public List<CategoryView> listCategories() {
        return categoryMapper.selectList(new LambdaQueryWrapper<Category>()
                        .orderByAsc(Category::getSortOrder))
                .stream()
                .map(c -> new CategoryView(c.getId(), c.getName(), c.getEn(), c.getDescription()))
                .toList();
    }
}
