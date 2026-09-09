import type { IPageQuery } from '@/api/types';
<#if hasResourceRef == true>
import type { ResourceRef } from '@/api/types/system/upload';
</#if>

// 查询条件
export type ${interfaceNamespace}Query = IPageQuery & {
<#list columns as field>
<#if field.isQuery == "1" >
  <#if field.queryType == "BETWEEN" >
  ${field.javaField}Start?: <#if field.javaType == "Long">string<#else>${field.tsType}</#if>;
  ${field.javaField}End?: <#if field.javaType == "Long">string<#else>${field.tsType}</#if>;
  <#else>
  ${field.javaField}?: <#if field.javaType == "Long">string<#elseif field.javaType == "List<ResourceRef>">ResourceRef[]<#else>${field.tsType}</#if>;
  </#if>
</#if>
</#list>
};

// 编辑form表单
export type ${interfaceNamespace}Form = {
<#list columns as field>
<#if field.isInsert == "1" || field.isEdit == "1" >
  ${field.javaField}?: <#if field.javaType == "Long">string<#elseif field.javaType == "List<ResourceRef>">ResourceRef[]<#else>${field.tsType}</#if>;
</#if>
</#list>
};

// list或detail返回结构
export type ${interfaceNamespace}Row = {
<#list columns as field>
<#if field.isList == "1">
  ${field.javaField}?: <#if field.javaType == "Long">string<#elseif field.javaType == "List<ResourceRef>">ResourceRef[]<#else>${field.tsType}</#if>;
</#if>
</#list>
};
