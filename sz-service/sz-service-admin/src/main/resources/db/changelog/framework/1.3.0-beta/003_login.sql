--liquibase formatted sql

--changeset 升职哦（sz）:20251016_1914

INSERT IGNORE INTO `sys_config` (`config_name`, `config_key`, `config_value`, `is_lock`, `create_id`, `create_time`, `update_id`, `update_time`, `remark`) VALUES ('登录请求次数限制', 'sys.login.requestLimit', '100', 'F', 1, '2025-10-14 16:35:28', 1, '2025-10-15 16:40:34', '一段时间内的登录请求次数限制，根据requestId（Ip + UserAgent）判断；若为 0 则不限制');
INSERT IGNORE INTO `sys_config` (`config_name`, `config_key`, `config_value`, `is_lock`, `create_id`, `create_time`, `update_id`, `update_time`, `remark`) VALUES ('登录次数计数周期（分）', 'sys.login.requestCycle', '10', 'F', 1, '2025-10-14 16:36:14', 1, '2025-10-16 16:51:44', '默认10分钟');

UPDATE `sys_config` SET `config_name` = '验证码请求次数限制', `config_key` = 'sys.captcha.requestLimit', `config_value` = '100', `is_lock` = 'T', `create_id` = 1, `create_time` = '2025-01-08 22:09:28', `update_id` = 1, `update_time` = '2025-10-16 17:30:09', `remark` = '一段时间内的验证码请求次数上限（0为不限制），默认100次' WHERE `config_key` = 'sys.captcha.requestLimit';
UPDATE `sys_config` SET `config_name` = '验证码计数周期（分）', `config_key` = 'sys.captcha.requestCycle', `config_value` = '10', `is_lock` = 'T', `create_id` = 1, `create_time` = '2025-01-08 22:13:09', `update_id` = 1, `update_time` = '2025-10-16 17:30:40', `remark` = '默认10（分）' WHERE `config_key` = 'sys.captcha.requestCycle';