--liquibase formatted sql

--changeset 升职哦（sz）:20250604_1621
--comment: 修正菜单路径拼写错误
UPDATE `sys_menu` SET `path` = '/system/menuManage' WHERE `path` = '/system/menuMange';
UPDATE `sys_menu` SET `component` = '/system/menuManage/index' WHERE `component` = '/system/menuMange/index';