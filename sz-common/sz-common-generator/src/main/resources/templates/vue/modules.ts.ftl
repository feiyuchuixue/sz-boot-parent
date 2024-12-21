import http from '@/api'
import { ADMIN_MODULE } from '@/api/helper/prefix'
import type { IPage } from '@/api/interface'
import type { ${interfaceNamespace} } from '@${interfacePkg}/${interfaceClassName}'
<#if GeneratorInfo.hasImport == "1">
import type { UploadRawFile } from "element-plus/es/components/upload/src/upload";
import type { AxiosRequestConfig } from 'axios';
</#if>

/**
* 查询列表
* @param params
* @returns {*}
*/
export const ${funGetList} = (params: ${interfaceNamespace}.Query) => {
  return http.get<IPage<${interfaceNamespace}.Row>>(ADMIN_MODULE + `/${router}`, params)
}

/**
* 添加
* @param params
* @returns {*}
*/
export const ${funCreate} = (params: ${interfaceNamespace}.Form) => {
  return http.post(ADMIN_MODULE + `/${router}`, params)
}

/**
* 修改
* @param params
* @returns {*}
*/
export const ${funUpdate} = (params: ${interfaceNamespace}.Form) => {
  return http.put(ADMIN_MODULE + `/${router}`, params)
}

/**
* 删除
* @param params
* @returns {*}
*/
export const ${funRemove} = (params: { ids: (string | number)[] }) => {
 return http.delete(ADMIN_MODULE + `/${router}`, params)
}

/**
* 获取详情
* @param params
* @returns {*}
*/
export const ${funDetail} = (params: { id: ${idType} }) => {
  const { id } = params
  return http.get<${interfaceNamespace}.Row>(ADMIN_MODULE + `/${router}/<#noparse>${id}</#noparse>`)
}
<#if GeneratorInfo.hasImport == "1">

/**
* 导入excel
* @param params
*/
export const ${funImport} = (params : UploadRawFile, config?: AxiosRequestConfig<{}> | undefined) => {
  return http.upload(ADMIN_MODULE + `/${router}/import`, params, config)
}
</#if>

<#if GeneratorInfo.hasExport == "1">
/**
* 导出excel
* @param params
* @returns {*}
*/
export const ${funExport}  = (params: ${interfaceNamespace}.Query) => {
  return http.download(ADMIN_MODULE + `/${router}/export`, params)
<#compress>
}
</#compress>
</#if>
