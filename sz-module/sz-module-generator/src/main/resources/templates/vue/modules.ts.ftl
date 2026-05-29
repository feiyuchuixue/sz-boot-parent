import { adminHttp } from '@/api/client';
import type { IPage } from '@/api/types';
import type {
  ${interfaceNamespace}Query,
  ${interfaceNamespace}Row,
  ${interfaceNamespace}Form
} from '@${typePkg}/${interfaceClassName}';
<#if GeneratorInfo.hasImport == "1">
import type { UploadRawFile } from "element-plus/es/components/upload/src/upload";
import type { AxiosRequestConfig } from 'axios';
</#if>

/**
* 查询列表
* @param params
* @returns {*}
*/
export const ${funGetList} = (params: ${interfaceNamespace}Query) => {
  return adminHttp.get<IPage<${interfaceNamespace}Row>>(`/${router}`, params);
};

/**
* 添加
* @param params
* @returns {*}
*/
export const ${funCreate} = (params: ${interfaceNamespace}Form) => {
  return adminHttp.post(`/${router}`, params);
};

/**
* 修改
* @param params
* @returns {*}
*/
export const ${funUpdate} = (params: ${interfaceNamespace}Form) => {
  return adminHttp.put(`/${router}`, params);
};

/**
* 删除
* @param params
* @returns {*}
*/
export const ${funRemove} = (params: { ids: (string | number)[] }) => {
 return adminHttp.delete(`/${router}`, params);
};

/**
* 获取详情
* @param params
* @returns {*}
*/
export const ${funDetail} = (params: { id: ${idType} }) => {
  const { id } = params;
  return adminHttp.get<${interfaceNamespace}Row>(`/${router}/<#noparse>${id}</#noparse>`);
};
<#if GeneratorInfo.hasImport == "1">

/**
* 导入excel
* @param params
*/
export const ${funImport} = (params : UploadRawFile, config?: AxiosRequestConfig<any> | undefined) => {
  return adminHttp.upload(`/${router}/import`, params, config);
};
</#if>

<#if GeneratorInfo.hasExport == "1">
/**
* 导出excel
* @param params
* @returns {*}
*/
export const ${funExport}  = (params: ${interfaceNamespace}Query) => {
  return adminHttp.download(`/${router}/export`, params);
<#compress>
};
</#compress>
</#if>
