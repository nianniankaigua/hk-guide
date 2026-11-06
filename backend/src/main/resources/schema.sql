-- HK Guide 数据库表结构（幂等，可重复执行）

CREATE TABLE IF NOT EXISTS hk_user (
    id         BIGINT       NOT NULL AUTO_INCREMENT,
    username   VARCHAR(32)  NOT NULL,
    password   VARCHAR(100) NOT NULL,
    nickname   VARCHAR(50)  DEFAULT NULL,
    created_at DATETIME     DEFAULT NULL,
    PRIMARY KEY (id),
    UNIQUE KEY uk_hk_user_username (username)
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4 COLLATE = utf8mb4_unicode_ci;

CREATE TABLE IF NOT EXISTS hk_favorite (
    id         BIGINT      NOT NULL AUTO_INCREMENT,
    user_id    BIGINT      NOT NULL,
    guide_slug VARCHAR(64) NOT NULL,
    created_at DATETIME    DEFAULT NULL,
    PRIMARY KEY (id),
    UNIQUE KEY uk_hk_favorite_user_slug (user_id, guide_slug)
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4 COLLATE = utf8mb4_unicode_ci;

CREATE TABLE IF NOT EXISTS hk_post (
    id         BIGINT       NOT NULL AUTO_INCREMENT,
    user_id    BIGINT       NOT NULL,
    title      VARCHAR(120) NOT NULL,
    content    TEXT         NOT NULL,
    created_at DATETIME     DEFAULT NULL,
    PRIMARY KEY (id)
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4 COLLATE = utf8mb4_unicode_ci;

CREATE TABLE IF NOT EXISTS hk_comment (
    id         BIGINT        NOT NULL AUTO_INCREMENT,
    post_id    BIGINT        NOT NULL,
    user_id    BIGINT        NOT NULL,
    content    VARCHAR(1000) NOT NULL,
    created_at DATETIME      DEFAULT NULL,
    PRIMARY KEY (id),
    KEY idx_hk_comment_post_id (post_id)
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4 COLLATE = utf8mb4_unicode_ci;

CREATE TABLE IF NOT EXISTS hk_guide (
    id           BIGINT       NOT NULL,
    slug         VARCHAR(64)  NOT NULL,
    title        VARCHAR(120) NOT NULL,
    subtitle     VARCHAR(200) DEFAULT NULL,
    category     VARCHAR(32)  NOT NULL,
    region       VARCHAR(64)  DEFAULT NULL,
    summary      VARCHAR(500) DEFAULT NULL,
    tags         TEXT         DEFAULT NULL,
    watermark    VARCHAR(8)   DEFAULT NULL,
    cover        VARCHAR(200) DEFAULT NULL,
    badge        VARCHAR(500) DEFAULT NULL,
    difficulty   INT          DEFAULT 0,
    views        BIGINT       DEFAULT 0,
    likes        BIGINT       DEFAULT 0,
    read_minutes INT          DEFAULT 0,
    updated_at   DATE         DEFAULT NULL,
    featured     TINYINT(1)   DEFAULT 0,
    content      LONGTEXT     DEFAULT NULL,
    theme        VARCHAR(500) DEFAULT NULL,
    PRIMARY KEY (id),
    UNIQUE KEY uk_hk_guide_slug (slug)
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4 COLLATE = utf8mb4_unicode_ci;

CREATE TABLE IF NOT EXISTS hk_category (
    id          VARCHAR(32)  NOT NULL,
    name        VARCHAR(64)  NOT NULL,
    en          VARCHAR(64)  DEFAULT NULL,
    description VARCHAR(200) DEFAULT NULL,
    sort_order  INT          DEFAULT 0,
    PRIMARY KEY (id)
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4 COLLATE = utf8mb4_unicode_ci;

CREATE TABLE IF NOT EXISTS hk_charm (
    id           BIGINT       NOT NULL,
    slug         VARCHAR(64)  NOT NULL,
    name         VARCHAR(64)  NOT NULL,
    en           VARCHAR(120) DEFAULT NULL,
    notches      INT          DEFAULT 0,
    effect       TEXT         DEFAULT NULL,
    lore         TEXT         DEFAULT NULL,
    location     VARCHAR(500) DEFAULT NULL,
    notes        TEXT         DEFAULT NULL,
    icon         VARCHAR(200) DEFAULT NULL,
    synergy      TEXT         DEFAULT NULL,
    synergy_note VARCHAR(500) DEFAULT NULL,
    PRIMARY KEY (id),
    UNIQUE KEY uk_hk_charm_slug (slug)
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4 COLLATE = utf8mb4_unicode_ci;
