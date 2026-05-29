--liquibase formatted sql

--changeset 升职哦（sz）:20250508_2222
UPDATE `sys_menu` SET `name` = 'AccountManage' WHERE `id` = '140c9ed43ef54542bbcdde8a5d928400';
UPDATE `sys_menu` SET `name` = 'RoleManage' WHERE `id` = 'c6dd479d5b304731be403d7551c60d70';
UPDATE `sys_menu` SET `name` = 'MenuManage' WHERE `id` = '99c2ee7b882749e597bcd62385f368fb';
UPDATE `sys_menu` SET `name` = 'DictManage' WHERE `id` = 'dcb6aabcd910469ebf3efbc7e43282d4';
UPDATE `sys_menu` SET `name` = 'ConfigManage' WHERE `id` = '29d33eba6b73420287d8f7e64aea62b3';
UPDATE `sys_menu` SET `name` = 'ClientManageView' WHERE `id` = '9e731ff422184fc1be2022c5c985735e';
UPDATE `sys_menu` SET `name` = 'SysDeptView' WHERE `id` = '8354d626cc65487594a7c38e98de1bad';
UPDATE `sys_menu` SET `name` = 'SysDataRoleView' WHERE `id` = '0444cd2c01584f0687264b6205536691';
UPDATE `sys_menu` SET `name` = 'SysFileView' WHERE `id` = 'c4896e8735a745bda9b47ecaf50f46f2';
UPDATE `sys_menu` SET `name` = 'SysTempFileView' WHERE `id` = '8231a369712e4f8f8ac09fce232cd034';
UPDATE `sys_menu` SET `name` = 'Generator' WHERE `id` = '0e529e8a9dbf450898b695e051c36d48';