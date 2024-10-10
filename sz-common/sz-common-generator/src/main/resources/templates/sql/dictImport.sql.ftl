<#-- 字典SQL  -->
<#setting number_format="0">
<#compress>
<#if dictTypeList?has_content>
<#list dictTypeList as dictType>
    INSERT INTO `sys_dict_type` (`id`, `type_name`, `type_code`, `is_lock`, `is_show`, `del_flag`, `remark`, `create_time`, `update_time`, `delete_time`, `create_id`, `update_id`, `delete_id`, `type`) VALUES (${dictType.id}, '${dictType.typeName}', '${dictType.typeCode}', '${dictType.isLock}', '${dictType.isShow}', '${dictType.delFlag}', '${dictType.remark}', '${dictType.createTime}', NULL, NULL, NULL, NULL, NULL, '${dictType.type}');
</#list>
</#if>
</#compress>


<#compress>
<#if dictList?has_content>
<#list dictList as dict>
    INSERT INTO `sys_dict` (`id`, `sys_dict_type_id`, `code_name`, `alias`, `sort`, `callback_show_style`, `remark`, `is_lock`, `is_show`, `del_flag`, `create_time`, `update_time`, `delete_time`, `create_id`, `update_id`, `delete_id`) VALUES (${dict.id}, '${dict.sysDictTypeId}', '${dict.codeName}', '${dict.alias}', ${dict.sort}, '${dict.callbackShowStyle}', '${dict.remark}', '${dict.isLock}', '${dict.isShow}', '${dict.delFlag}', '${dict.createTime}', NULL, NULL, NULL, NULL, NULL);
</#list>
</#if>
</#compress>