import type { IPageQuery } from '@/api/interface'

export namespace ${interfaceNamespace} {

  // 查询条件
  export interface Query extends IPageQuery {
<#list columns as field>
 <#if field.isQuery == "1" >
   <#if field.queryType == "BETWEEN" >
    ${field.javaField}Start?: ${field.tsType}
    ${field.javaField}End?: ${field.tsType}
   <#else>
    ${field.javaField}?: ${field.tsType}
   </#if>
 </#if>
</#list>
  }

  // 编辑form表单
  export interface Form {
<#list columns as field>
    <#if field.isInsert == "1" || field.isEdit == "1" >
    ${field.javaField}?: ${field.tsType}
    </#if>
</#list>
 }

  // list或detail返回结构
  export interface Row {
<#list columns as field>
    <#if field.isList == "1">
    ${field.javaField}?: ${field.tsType}
    </#if>
</#list>
  }

}