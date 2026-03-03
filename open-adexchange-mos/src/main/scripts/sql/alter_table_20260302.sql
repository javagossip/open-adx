ALTER TABLE `site_ad_placement`
  ADD `debug` TINYINT(1)  NULL  DEFAULT 0  COMMENT '是否启用debug 模式'  AFTER `updated_at`;

CREATE TABLE IF NOT EXISTS `log_sampling_config`
(
  `id`            int unsigned NOT NULL AUTO_INCREMENT,
  `log_type`      varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL DEFAULT 'GLOBAL' COMMENT '采用日志类型： GLOBAL,BID_REQ,BID_RSP,DSP_REQ,DSP_RSP',
  `media_id`      int                                                          NOT NULL DEFAULT '0' COMMENT '媒体id',
  `dsp_id`        int                                                          NOT NULL DEFAULT '0' COMMENT 'dsp 平台 id',
  `ad_slot_id`    int                                                          NOT NULL DEFAULT '0' COMMENT '媒体广告位 id',
  `sampling_rate` int                                                          DEFAULT '0' COMMENT '采样率-万分位',
  `create_time`   datetime                                                     DEFAULT CURRENT_TIMESTAMP COMMENT '创建日期',
  `update_time`   datetime                                                     DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新日期',
  `status`        int                                                          DEFAULT '1' COMMENT '启用/禁用,0-禁用,1-启用',
  PRIMARY KEY (`id`),
  KEY             `uniq_idx_lmda` (`log_type`,`media_id`,`dsp_id`,`ad_slot_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;