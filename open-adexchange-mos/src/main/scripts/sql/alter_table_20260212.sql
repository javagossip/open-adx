-- 更新publisher表和site表 id字段类型为int
ALTER TABLE `publisher` CHANGE `id` `id` INT  NOT NULL  AUTO_INCREMENT;
ALTER TABLE `site` CHANGE `publisher_id` `publisher_id` INT  NOT NULL;
ALTER TABLE `site` CHANGE `id` `id` INT  NOT NULL  AUTO_INCREMENT;
ALTER TABLE `site_ad_placement` CHANGE `site_id` `site_id` INT  NULL  DEFAULT NULL  COMMENT '站点/app id';

