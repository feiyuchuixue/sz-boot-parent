--liquibase formatted sql

--changeset 升职哦（sz）:20250530_1125
--comment: 修正菜单路径拼写错误
UPDATE `sys_menu` SET `path` = '/system/menuManage' WHERE `path` = '/system/menuMange';