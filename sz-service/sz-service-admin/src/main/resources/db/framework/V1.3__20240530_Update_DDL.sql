-- sys_client表添加 is_lock 字段
ALTER TABLE `sys_client` ADD COLUMN `is_lock` ENUM('T', 'F') CHARACTER SET utf8mb4 COLLATE utf8mb4_bin DEFAULT 'F' COMMENT '是否锁定';
-- 更新记录
UPDATE `sys_client` SET `is_lock` = 'T',`remark` ='演示client，禁止删除' WHERE `client_id` = '195da9fcce574852b850068771cde034';