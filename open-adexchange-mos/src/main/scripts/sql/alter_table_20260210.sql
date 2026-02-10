ALTER TABLE `ad_slot_stat` ADD `ad_slot_name` VARCHAR(100)  NULL  DEFAULT NULL  AFTER `ad_slot_id`;
ALTER TABLE `ad_slot_stat` ADD `publisher_name` VARCHAR(150)  NULL  DEFAULT NULL  AFTER `publisher_id`;
ALTER TABLE `ad_slot_stat` ADD `site_name` VARCHAR(100)  NULL  DEFAULT NULL  COMMENT '站点/app 名称'  AFTER `site_id`;
