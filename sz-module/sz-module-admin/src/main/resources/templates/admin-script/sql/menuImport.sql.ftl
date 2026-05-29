<#setting number_format="0">
<#compress>
<#if sysMenuList?has_content>
<#list sysMenuList as menu>
<#if scriptDialect == "postgresql">
INSERT INTO sys_menu (id, pid, path, name, title, icon, component, redirect, sort, deep, menu_type_cd, permissions, is_hidden, has_children, is_link, is_full, is_affix, is_keep_alive, del_flag, use_data_scope)
VALUES (${sql.number(menu.id)}, ${sql.number((menu.pid)!0)}, ${sql.value((menu.path)!"")}, ${sql.value((menu.name)!"")}, ${sql.value((menu.title)!"")}, ${sql.value((menu.icon)!"")}, ${sql.value((menu.component)!"")}, ${sql.value((menu.redirect)!"")}, ${sql.number((menu.sort)!0)}, ${sql.number((menu.deep)!1)}, ${sql.value((menu.menuTypeCd)!"")}, ${sql.value((menu.permissions)!"")}, ${sql.value((menu.isHidden)!"F")}, ${sql.value((menu.hasChildren)!"F")}, ${sql.value((menu.isLink)!"F")}, ${sql.value((menu.isFull)!"F")}, ${sql.value((menu.isAffix)!"F")}, ${sql.value((menu.isKeepAlive)!"F")}, ${sql.value((menu.delFlag)!"F")}, ${sql.value((menu.useDataScope)!"F")})
ON CONFLICT (id) DO NOTHING;
<#else>
INSERT IGNORE INTO sys_menu (id, pid, path, name, title, icon, component, redirect, sort, deep, menu_type_cd, permissions, is_hidden, has_children, is_link, is_full, is_affix, is_keep_alive, del_flag, use_data_scope)
VALUES (${sql.number(menu.id)}, ${sql.number((menu.pid)!0)}, ${sql.value((menu.path)!"")}, ${sql.value((menu.name)!"")}, ${sql.value((menu.title)!"")}, ${sql.value((menu.icon)!"")}, ${sql.value((menu.component)!"")}, ${sql.value((menu.redirect)!"")}, ${sql.number((menu.sort)!0)}, ${sql.number((menu.deep)!1)}, ${sql.value((menu.menuTypeCd)!"")}, ${sql.value((menu.permissions)!"")}, ${sql.value((menu.isHidden)!"F")}, ${sql.value((menu.hasChildren)!"F")}, ${sql.value((menu.isLink)!"F")}, ${sql.value((menu.isFull)!"F")}, ${sql.value((menu.isAffix)!"F")}, ${sql.value((menu.isKeepAlive)!"F")}, ${sql.value((menu.delFlag)!"F")}, ${sql.value((menu.useDataScope)!"F")});
</#if>
</#list>
</#if>
</#compress>
