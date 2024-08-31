<#-- 菜单SQL  -->
<#setting number_format="0">
<#compress>
<#list sysMenuList as menu>
INSERT INTO `sys_menu` (`id`, `pid`, `path`, `name`, `title`, `icon`, `component`, `sort`, `deep`, `menu_type_cd`, `permissions`, `has_children`) VALUES ('${menu.id}', '${menu.pid}', '${menu.path}', '${menu.name}', '${menu.title}', '${menu.icon}', '${menu.component}', ${menu.sort}, ${menu.deep}, '${menu.menuTypeCd}', '${menu.permissions}', '${menu.hasChildren}');
</#list>
</#compress>