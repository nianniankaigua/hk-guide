package com.hkguide.config;

import com.hkguide.entity.Category;
import com.hkguide.mapper.CategoryMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * 启动时种子导入：hk_category 表为空时插入 7 个攻略分类。
 */
@Component
public class CategoryDataSeeder implements ApplicationRunner {

    private static final Logger log = LoggerFactory.getLogger(CategoryDataSeeder.class);

    private final CategoryMapper categoryMapper;

    public CategoryDataSeeder(CategoryMapper categoryMapper) {
        this.categoryMapper = categoryMapper;
    }

    @Override
    public void run(ApplicationArguments args) {
        Long count = categoryMapper.selectCount(null);
        if (count != null && count > 0) {
            log.info("hk_category 表已有 {} 条数据，跳过种子导入", count);
            return;
        }
        List<Category> categories = List.of(
                of("boss", "BOSS 攻略", "Boss", "招式拆解与安全站位，按节奏打赢每一场。", 1),
                of("dream", "战士之梦", "Warrior Dreams", "七位亡者的梦境战场，在墓碑上用梦之钉敲开。", 2),
                of("enemy", "小怪图鉴", "Bestiary", "按地图环境划分的圣巢生物，先认环境，再认敌人。", 3),
                of("charm", "护符图鉴", "Charms", "槽位、协同与三套常用搭配。", 4),
                of("route", "流程路线", "Route", "从遗忘十字路到圣巢深处的推进顺序。", 5),
                of("ending", "结局收集", "Endings", "结局条件、完成度与易漏项。", 6),
                of("advanced", "进阶挑战", "Advanced", "神居、钢魂、苦痛之路与速通。", 7));
        for (Category category : categories) {
            categoryMapper.insert(category);
        }
        log.info("hk_category 种子导入完成，共插入 {} 条分类", categories.size());
    }

    private Category of(String id, String name, String en, String description, int sortOrder) {
        Category c = new Category();
        c.setId(id);
        c.setName(name);
        c.setEn(en);
        c.setDescription(description);
        c.setSortOrder(sortOrder);
        return c;
    }
}
