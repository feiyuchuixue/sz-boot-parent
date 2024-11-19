<template>
  <div class="table-box">
    <ProTable
      ref="proTableRef"
      title="${functionName}"
      :indent="20"
      :columns="columns"
      :search-columns="searchColumns"
      :request-api="getTableList"
      row-key="${pkName}"
    >
      <!-- 表格 header 按钮 -->
      <template #tableHeader="scope">
        <el-button type="primary"
        <#if GeneratorInfo.btnPermissionType == "1">
          v-auth="'${createPermission}'"
        </#if>
          :icon="CirclePlus"
          @click="openAddEdit('新增${functionName}')"
        >
          新增
        </el-button>
        <el-button
          <#if GeneratorInfo.btnPermissionType == "1">
          v-auth="'${removePermission}'"
          </#if>
          type="danger"
          :icon="Delete"
          plain
          :disabled="!scope.isSelected"
          @click="batchDelete(scope.selectedListIds)"
        >
          批量删除
        </el-button>
          <#if GeneratorInfo.hasImport == "1">
        <el-button
          <#if GeneratorInfo.btnPermissionType == "1">
          v-auth="'${importPermission}'"
          </#if>
          type="primary"
          :icon="Upload"
          plain
          @click="importData"
        >
          导入
        </el-button>
          </#if>
          <#if GeneratorInfo.hasExport == "1">
        <el-button
          <#if GeneratorInfo.btnPermissionType == "1">
          v-auth="'${exportPermission}'"
          </#if>
          type="primary"
          :icon="Download"
          plain
          @click="downloadFile"
        >
          导出
        </el-button>
          </#if>
      </template>
      <template #operation="{ row }">
        <el-button
          <#if GeneratorInfo.btnPermissionType == "1">
          v-auth="'${updatePermission}'"
          </#if>
          type="primary"
          link
          :icon="EditPen"
          @click="openAddEdit('编辑${functionName}', row, false)"
        >
          编辑
        </el-button>
        <el-button
          <#if GeneratorInfo.btnPermissionType == "1">
            v-auth="'${removePermission}'"
          </#if>
          type="primary"
          link
          :icon="Delete"
          @click="deleteInfo(row)"
        >
          删除
        </el-button>
      </template>
    </ProTable>
    <${formClassName} ref="${businessName}Ref" />
    <#if GeneratorInfo.hasImport == "1">
    <ImportExcel ref="ImportExcelRef" />
    </#if>
  </div>
</template>

<script setup lang="ts">
import { ref } from 'vue'
import {
  CirclePlus,
  Delete,
  EditPen,
  <#if GeneratorInfo.hasImport == "1">
  Upload,
  </#if>
  <#if GeneratorInfo.hasExport == "1">
  Download,
  </#if>
} from '@element-plus/icons-vue'
import ProTable from '@/components/ProTable/index.vue'
import {
  ${funCreate},
  ${funRemove},
  ${funUpdate},
  ${funGetList},
  ${funDetail},
  <#if GeneratorInfo.hasImport == "1">
  ${funImport},
  </#if>
  <#if GeneratorInfo.hasExport == "1">
  ${funExport},
  </#if>
} from '@${modulesPkg}/${modulesClassName}';
import { useHandleData } from '@/hooks/useHandleData';
import ${ formClassName } from '@${formPkg}/${formClassName}.vue';
<#if hasDict == true>
import { useOptionsStore } from '@/stores/modules/options';
</#if>
import type { ColumnProps, ProTableInstance, SearchProps } from '@/components/ProTable/interface';
import type { ${interfaceNamespace} } from '@${interfacePkg}/${interfaceClassName}';
<#if GeneratorInfo.hasImport == "1">
import ImportExcel from '@/components/ImportExcel/index.vue';
import { downloadTemplate } from '@/api/modules/system/common';
</#if>
<#if GeneratorInfo.hasExport == "1">
import { ElMessageBox } from "element-plus";
import { useDownload } from "@/hooks/useDownload";
</#if>
defineOptions({
  name: '${indexDefineOptionsName}'
})
<#if hasDict == true>
const optionsStore = useOptionsStore();
</#if>
const proTableRef = ref<ProTableInstance>();
// 表格配置项
const columns: ColumnProps<${interfaceNamespace}.Row>[] = [
  { type: 'selection', width: 80 },
  <#list columns as field>
  <#if field.isList == "1" && field.isPk == "0">
  <#if field.dictType != "">
  { prop: '${field.javaField}',
    label: '${field.columnComment}',
    tag: true,
    enum: optionsStore.getDictOptions('${field.dictType}'),
    fieldNames: {
      label: "codeName",
       <#if field.dictShowWay == "0" >
      value: "id",
        <#else>
      value: "alias",
        </#if>
      tagType: "callbackShowStyle"
    },
  },
  <#else>
  { prop: '${field.javaField}', label: '${field.columnComment}' },
  </#if>
  </#if>
  </#list>
  { prop: 'operation', label: '操作', width: 250, fixed: 'right' }
]
// 搜索条件项
const searchColumns: SearchProps[] = [
  <#list columns as field>
  <#if field.isQuery == "1">
  <#if field.searchType == "date-picker">
  { prop: '${field.javaField}',
    label: '${field.columnComment}',
    el: '${field.searchType}',
    span: 2,
    props: {
      type: "datetimerange",
      valueFormat: "YYYY-MM-DD HH:mm:ss"
    },
  },
  <#elseif field.dictType != "">
  { prop: '${field.javaField}',
    label: '${field.columnComment}',
    el: '${field.searchType}',
    enum: optionsStore.getDictOptions('${field.dictType}'),
    fieldNames: {
      label: "codeName",
      value: "id",
      tagType: "callbackShowStyle"
    },
  },
  <#else>
  { prop: '${field.javaField}', label: '${field.columnComment}', el: '${field.searchType}' },
  </#if>
  </#if>
  </#list>
]
// 获取table列表
const getTableList = (params: ${interfaceNamespace}.Query) => {
  let newParams = formatParams(params);
  return ${funGetList}(newParams);
};
const formatParams = (params: ${interfaceNamespace}.Query) =>{
  let newParams = JSON.parse(JSON.stringify(params));
  <#list columns as field>
  <#if field.queryType == "BETWEEN">
  newParams.${field.javaField} && (newParams.${field.javaField}Start = newParams.${field.javaField}[0]);
  newParams.${field.javaField} && (newParams.${field.javaField}End = newParams.${field.javaField}[1]);
  delete newParams.${field.javaField};
  </#if>
  </#list>
  return newParams;
}
// 打开 drawer(新增、查看、编辑)
const ${businessName}Ref = ref<InstanceType<typeof ${formClassName}>>()
const openAddEdit = async(title: string, row: any = {}, isAdd = true) => {
  if (!isAdd) {
    const record = await ${funDetail}({ id: row?.${pkName} })
    row = record?.data
  }
  const params = {
    title,
    row: { ...row },
    api: isAdd ? ${funCreate} : ${funUpdate},
    getTableList: proTableRef.value?.getTableList
  }
  ${businessName}Ref.value?.acceptParams(params)
}
// 删除信息
const deleteInfo = async (params: ${interfaceNamespace}.Row) => {
  await useHandleData(
    ${funRemove},
    { ids: [params.${pkName}] },
    `删除【<#noparse>${params.</#noparse>${pkName}}】${functionName}`
  )
  proTableRef.value?.getTableList()
}
// 批量删除信息
const batchDelete = async (ids: (string | number)[]) => {
  await useHandleData(${funRemove}, { ids }, '删除所选${functionName}')
  proTableRef.value?.clearSelection()
  proTableRef.value?.getTableList()
}
<#if GeneratorInfo.hasImport == "1">
// 导入
const ImportExcelRef = ref<InstanceType<typeof ImportExcel>>()
const importData = () => {
  const params = {
    title: '${functionName}',
    templateName: '${functionName}',
    tempApi: downloadTemplate,
    importApi: ${funImport},
    getTableList: proTableRef.value?.getTableList
  }
  ImportExcelRef.value?.acceptParams(params)
}
</#if>
<#if GeneratorInfo.hasExport == "1">
// 导出
const downloadFile = async () => {
  let newParams = formatParams(proTableRef.value?.searchParam as ${interfaceNamespace}.Query);
  useDownload(${funExport}, "${functionName}", newParams);
};
</#if>
</script>