<#-- 菜单SQL  -->
<#compress>
<#setting number_format="0">
<#list sysMenuList as menu>
INSERT INTO `sys_menu` (`id`, `pid`, `path`, `name`, `title`, `icon`, `component`, `redirect`, `sort`, `deep`, `menu_type_cd`, `permissions`, `is_hidden`, `has_children`, `is_link`, `is_full`, `is_affix`, `is_keep_alive`, `create_time`, `update_time`, `create_id`, `update_id`, `del_flag`, `delete_id`, `delete_time`)VALUES ('${menu.id}', '${menu.pid}', '${menu.path}', '${menu.name}', '${menu.title}', '${menu.icon}', '${menu.component}', '${menu.redirect}', ${menu.sort}, ${menu.deep}, '${menu.menuTypeCd}', '${menu.permissions}', '${menu.isHidden}', '${menu.hasChildren}', '${menu.isLink}', '${menu.isFull}', '${menu.isAffix}', '${menu.isKeepAlive}', '${menu.createTime}', '${menu.updateTime}', '${menu.createId}', '${menu.updateId}', '${menu.delFlag}', NULL, NULL);
</#list>
</#compress>