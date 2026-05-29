<#setting number_format="0">
<#compress>
<#if dictTypeList?has_content>
<#list dictTypeList as dictType>
<#if scriptDialect == "postgresql">
INSERT INTO sys_dict_type (id, type_name, type_code, source_code, is_lock, is_show, del_flag, remark, create_time, update_time, delete_time, create_id, update_id, delete_id)
VALUES (${sql.number(dictType.id)}, ${sql.value(dictType.typeName)}, ${sql.value(dictType.typeCode)}, ${sql.value((dictType.sourceCode)!"")}, ${sql.value((dictType.isLock)!"F")}, ${sql.value((dictType.isShow)!"T")}, ${sql.value((dictType.delFlag)!"F")}, ${sql.value((dictType.remark)!"")}, ${sql.value(dictType.createTime)}, ${sql.value(dictType.updateTime)}, ${sql.value(dictType.deleteTime)}, ${sql.number(dictType.createId)}, ${sql.number(dictType.updateId)}, ${sql.number(dictType.deleteId)})
ON CONFLICT (id) DO NOTHING;
<#else>
INSERT IGNORE INTO sys_dict_type (id, type_name, type_code, source_code, is_lock, is_show, del_flag, remark, create_time, update_time, delete_time, create_id, update_id, delete_id)
VALUES (${sql.number(dictType.id)}, ${sql.value(dictType.typeName)}, ${sql.value(dictType.typeCode)}, ${sql.value((dictType.sourceCode)!"")}, ${sql.value((dictType.isLock)!"F")}, ${sql.value((dictType.isShow)!"T")}, ${sql.value((dictType.delFlag)!"F")}, ${sql.value((dictType.remark)!"")}, ${sql.value(dictType.createTime)}, ${sql.value(dictType.updateTime)}, ${sql.value(dictType.deleteTime)}, ${sql.number(dictType.createId)}, ${sql.number(dictType.updateId)}, ${sql.number(dictType.deleteId)});
</#if>
</#list>
</#if>
</#compress>

<#compress>
<#if dictList?has_content>
<#list dictList as dict>
<#if scriptDialect == "postgresql">
INSERT INTO sys_dict (id, sys_dict_type_id, code_name, alias, sort, callback_show_style, remark, is_lock, is_show, del_flag, create_time, update_time, delete_time, create_id, update_id, delete_id)
VALUES (${sql.number(dict.id)}, ${sql.number(dict.sysDictTypeId)}, ${sql.value(dict.codeName)}, ${sql.value((dict.alias)!"")}, ${sql.number((dict.sort)!0)}, ${sql.value((dict.callbackShowStyle)!"")}, ${sql.value((dict.remark)!"")}, ${sql.value((dict.isLock)!"F")}, ${sql.value((dict.isShow)!"T")}, ${sql.value((dict.delFlag)!"F")}, ${sql.value(dict.createTime)}, ${sql.value(dict.updateTime)}, ${sql.value(dict.deleteTime)}, ${sql.number(dict.createId)}, ${sql.number(dict.updateId)}, ${sql.number(dict.deleteId)})
ON CONFLICT (id) DO NOTHING;
<#else>
INSERT IGNORE INTO sys_dict (id, sys_dict_type_id, code_name, alias, sort, callback_show_style, remark, is_lock, is_show, del_flag, create_time, update_time, delete_time, create_id, update_id, delete_id)
VALUES (${sql.number(dict.id)}, ${sql.number(dict.sysDictTypeId)}, ${sql.value(dict.codeName)}, ${sql.value((dict.alias)!"")}, ${sql.number((dict.sort)!0)}, ${sql.value((dict.callbackShowStyle)!"")}, ${sql.value((dict.remark)!"")}, ${sql.value((dict.isLock)!"F")}, ${sql.value((dict.isShow)!"T")}, ${sql.value((dict.delFlag)!"F")}, ${sql.value(dict.createTime)}, ${sql.value(dict.updateTime)}, ${sql.value(dict.deleteTime)}, ${sql.number(dict.createId)}, ${sql.number(dict.updateId)}, ${sql.number(dict.deleteId)});
</#if>
</#list>
</#if>
</#compress>
