<template>
<#if GeneratorInfo.windowShowType == "0">
  <el-dialog v-model="visible" :title="`<#noparse>${paramsProps.title}</#noparse>`" :destroy-on-close="true" :width="dialogWidth" draggable>
<#else>
  <el-drawer v-model="visible" :title="`<#noparse>${paramsProps.title}</#noparse>`" :destroy-on-close="true" :size="dialogWidth">
</#if>
    <el-form
      ref="ruleFormRef"
      label-width="140px"
      label-suffix=" :"
      :rules="rules"
      :model="paramsProps.row"
      @submit.enter.prevent="handleSubmit"
    >
    <#list columns as field>
        <#if field.isInsert == "1" || field.isEdit == "1" && field.isPk == "0">
      <el-form-item label="${field.columnComment}" prop="${field.javaField}">
          <#if field.htmlType == "input">
        <el-input v-model="paramsProps.row.${field.javaField}" placeholder="请填写${field.columnComment}" clearable></el-input>
          <#elseif field.htmlType == "textarea">
        <el-input v-model="paramsProps.row.${field.javaField}" placeholder="请填写${field.columnComment}" :rows="2" type="textarea" clearable></el-input>
          <#elseif field.htmlType == "input-number">
        <el-input-number v-model="paramsProps.row.${field.javaField}" :precision="0" :min="1" :max="999999" />
          <#elseif field.htmlType == "select">
        <el-select v-model="paramsProps.row.${field.javaField}" clearable placeholder="请选择${field.columnComment}">
          <el-option
            v-for="item in optionsStore.getDictOptions('${field.dictType}')"
            <#if field.dictShowWay == "0" >
            :key="item.id"
            <#else>
            :key="item.alias"
            </#if>
            :label="item.codeName"
            <#if field.javaType == "Integer">
              <#if field.dictShowWay == "0" >
            :value="Number(item.id)"
              <#else>
            :value="Number(item.alias)"
              </#if>
            <#else>
              <#if field.dictShowWay == "0" >
            :value="item.id"
              <#else>
            :value="item.alias"
              </#if>
            </#if>
          />
        </el-select>
          <#elseif field.htmlType == "radio" || field.htmlType == "radio-group">
        <el-radio-group v-model="paramsProps.row.${field.javaField}">
          <el-radio
            v-for="item in optionsStore.getDictOptions('${field.dictType}')"
            <#if field.dictShowWay == "0" >
            :key="item.id"
            <#else>
            :key="item.alias"
            </#if>
            <#if field.javaType == "Integer">
              <#if field.dictShowWay == "0" >
            :value="Number(item.id)"
              <#else>
            :value="Number(item.alias)"
              </#if>
            <#else>
              <#if field.dictShowWay == "0" >
            :value="item.id"
              <#else>
            :value="item.alias"
              </#if>
            </#if>
          >
            {{ item.codeName }}
          </el-radio>
        </el-radio-group>
          <#elseif field.htmlType == "checkbox">
        <el-checkbox-group
          v-model="${field.javaField}CheckedValues"
          @change="syncCheckboxValue('${field.javaField}', $event)"
        >
          <el-checkbox
            v-for="item in optionsStore.getDictOptions('${field.dictType}')"
            <#if field.dictShowWay == "0" >
            :key="item.id"
            <#else>
            :key="item.alias"
            </#if>
            <#if field.javaType == "Integer">
              <#if field.dictShowWay == "0" >
            :value="Number(item.id)"
              <#else>
            :value="Number(item.alias)"
              </#if>
            <#else>
              <#if field.dictShowWay == "0" >
            :value="item.id"
              <#else>
            :value="item.alias"
              </#if>
            </#if>
          >
            {{ item.codeName }}
          </el-checkbox>
        </el-checkbox-group>
          <#elseif field.htmlType == "datetime">
        <el-date-picker clearable
          v-model="paramsProps.row.${field.javaField}"
          type="datetime"
          value-format="YYYY-MM-DD HH:mm:ss"
          placeholder="请选择${field.columnComment}">
        </el-date-picker>
          <#elseif field.htmlType == "date">
        <el-date-picker clearable
          v-model="paramsProps.row.${field.javaField}"
          type="date"
          value-format="YYYY-MM-DD"
          placeholder="请选择${field.columnComment}">
        </el-date-picker>
          <#elseif field.htmlType == "time">
        <el-time-picker clearable v-model="paramsProps.row.${field.javaField}" value-format="HH:mm:ss" placeholder="请选择${field.columnComment}"></el-time-picker>
          <#elseif field.htmlType == "fileUpload" || field.htmlType == "imageUpload">
        <upload-files
          v-model:modelValue="${field.javaField}UploadResult"
          :limit="${field.options['upload-files.limit']!5}"
          :file-size="${field.options['upload-files.fileSize']!3}"
          scene-code="${field.options['upload-files.sceneCode']!'system.temp'}"
          path-segments="${field.options['upload-files.pathSegments']!'your_biz_path'}"
          :accept="'${field.options['upload-files.accept']!''}'"
          @change="syncUploadValue('${field.javaField}', $event)"
          @update:modelValue="syncUploadValue('${field.javaField}', $event)"
        />
          <#elseif field.htmlType == "jodit-editor">
        <jodit-editor
          v-model="paramsProps.row.${field.javaField}"
          scene-code="${field.options['upload.sceneCode']!'system.temp'}"
          path-segments="${field.options['upload.pathSegments']!'your_editor_biz_path'}"
          :height="'${field.options['height']!'400px'}'"
        />
          <#else>
        <el-input v-model="paramsProps.row.${field.javaField}" placeholder="请填写${field.columnComment}" clearable></el-input>
          </#if>
      </el-form-item>
        </#if>
    </#list>
    </el-form>
    <template #footer>
      <el-button @click="visible = false"> 取消</el-button>
      <el-button type="primary" @click="handleSubmit"> 确定</el-button>
    </template>
<#if GeneratorInfo.windowShowType == "0">
  </el-dialog>
<#else>
  </el-drawer>
</#if>
</template>

<script setup lang="ts">
import { ref, reactive } from 'vue';
import { type ElForm, ElMessage } from 'element-plus';
<#if hasSelect == true>
import { useOptionsStore } from '@/stores/modules/options';
</#if>
<#list columns as field>
<#if field.htmlType == "fileUpload" || field.htmlType == "imageUpload">
<#assign hasFileUpload = true>
</#if>
<#if field.htmlType == "checkbox">
<#assign hasCheckbox = true>
</#if>
<#if field.htmlType == "jodit-editor">
<#assign hasJoditEditor = true>
</#if>
</#list>
<#if hasFileUpload?? && hasFileUpload>
import type { IResourceUploadResult } from '@/api/types/system/upload';
import UploadFiles from '@/components/Upload/UploadFiles.vue';
</#if>
<#if hasJoditEditor?? && hasJoditEditor>
import JoditEditor from '@/components/JoditEditor/index.vue';
</#if>
import { useDialogWidth } from '@/hooks/useDialogWidth';
defineOptions({
  name: '${formClassName}',
});
const dialogWidth = useDialogWidth('');
<#if hasSelect == true>
const optionsStore = useOptionsStore();
</#if>
const visible = ref(false);
const paramsProps = ref<View.DefaultParams>({
  title: '',
  row: {},
  api: undefined,
  getTableList: undefined
});

<#list columns as field>
<#if field.htmlType == "checkbox">
const ${field.javaField}CheckedValues = ref<Array<string | number | boolean>>([]);
</#if>
<#if field.htmlType == "fileUpload" || field.htmlType == "imageUpload">
const ${field.javaField}UploadResult = ref<IResourceUploadResult[] | string[]>([]);
</#if>
</#list>

<#if hasCheckbox?? && hasCheckbox>
const normalizeCheckboxValue = (value: unknown, numeric = false): Array<string | number | boolean> => {
  if (Array.isArray(value)) {
    return numeric ? value.map(item => Number(item)) : (value as Array<string | number | boolean>);
  }
  if (value === undefined || value === null || value === '') {
    return [];
  }
  const values = String(value).split(',').map(item => item.trim()).filter(Boolean);
  return numeric ? values.map(item => Number(item)) : values;
};

const formatCheckboxValue = (value: Array<string | number | boolean>) => value.join(',');

const syncCheckboxValue = (fieldName: string, value: unknown, validate = true) => {
  paramsProps.value.row[fieldName] = formatCheckboxValue(normalizeCheckboxValue(value));
  if (validate) {
    ruleFormRef.value?.validateField(fieldName);
  }
};

</#if>
<#if hasFileUpload?? && hasFileUpload>
const normalizeUploadValue = (value: unknown): IResourceUploadResult[] | string[] => {
  if (Array.isArray(value)) {
    return value.filter(item => item !== undefined && item !== null && item !== '') as IResourceUploadResult[] | string[];
  }
  if (value === undefined || value === null || value === '') {
    return [];
  }
  return [value] as IResourceUploadResult[] | string[];
};

const hasUploadValue = (value: unknown) => normalizeUploadValue(value).length > 0;

const syncUploadValue = (fieldName: string, value: unknown, validate = true) => {
  paramsProps.value.row[fieldName] = normalizeUploadValue(value);
  if (validate) {
    ruleFormRef.value?.validateField(fieldName);
  }
};

</#if>
<#list columns as field>
<#if field.isRequired == "1" && field.htmlType == "checkbox">
const validate${field.upCamelField}Checked = (_rule: unknown, _value: unknown, callback: (error?: Error) => void) => {
  if (normalizeCheckboxValue(paramsProps.value.row.${field.javaField}<#if field.javaType == "Integer" || field.javaType == "Long">, true</#if>).length === 0) {
    callback(new Error('请选择${field.columnComment}'));
    return;
  }
  callback();
};

</#if>
<#if field.isRequired == "1" && (field.htmlType == "fileUpload" || field.htmlType == "imageUpload")>
const validate${field.upCamelField}Upload = (_rule: unknown, _value: unknown, callback: (error?: Error) => void) => {
  if (!hasUploadValue(paramsProps.value.row.${field.javaField})) {
    callback(new Error('请上传${field.columnComment}'));
    return;
  }
  callback();
};

</#if>
</#list>
const rules = reactive({
  <#list columns as field>
  <#if field.isRequired == "1">
    <#if field.htmlType == "checkbox">
  ${field.javaField}: [{ validator: validate${field.upCamelField}Checked, trigger: 'change' }],
    <#elseif field.htmlType == "fileUpload" || field.htmlType == "imageUpload">
  ${field.javaField}: [{ validator: validate${field.upCamelField}Upload, trigger: 'change' }],
    <#elseif field.htmlType == "select" || field.htmlType == "radio" || field.htmlType == "radio-group" || field.htmlType == "datetime" || field.htmlType == "date" || field.htmlType == "time">
  ${field.javaField}: [{ required: true, message: '请选择${field.columnComment}', trigger: 'change' }],
    <#else>
  ${field.javaField}: [{ required: true, message: '请填写${field.columnComment}', trigger: 'blur' }],
    </#if>
  </#if>
  </#list>
});

// 接收父组件传过来的参数
const acceptParams = (params: View.DefaultParams) => {
  paramsProps.value = params;
<#list columns as field>
<#if field.htmlType == "checkbox">
  ${field.javaField}CheckedValues.value = normalizeCheckboxValue(params.row.${field.javaField}<#if field.javaType == "Integer" || field.javaType == "Long">, true</#if>);
  syncCheckboxValue('${field.javaField}', ${field.javaField}CheckedValues.value, false);
</#if>
<#if field.htmlType == "fileUpload" || field.htmlType == "imageUpload">
  ${field.javaField}UploadResult.value = params.row.${field.javaField} || [];
  syncUploadValue('${field.javaField}', ${field.javaField}UploadResult.value, false);
</#if>
</#list>
  visible.value = true;
};

// 提交数据（新增/编辑）
const ruleFormRef = ref<InstanceType<typeof ElForm>>();
const handleSubmit = () => {
  ruleFormRef.value!.validate(async (valid) => {
    if (!valid) return;
    try {
<#list columns as field>
  <#if field.htmlType == "checkbox">
      paramsProps.value.row.${field.javaField} = formatCheckboxValue(${field.javaField}CheckedValues.value);
  </#if>
  <#if field.htmlType == "fileUpload" || field.htmlType == "imageUpload">
      paramsProps.value.row.${field.javaField} = ${field.javaField}UploadResult.value; // 附件数据添加--从上传组件获取
  </#if>
</#list>
      await paramsProps.value.api!(paramsProps.value.row);
      ElMessage.success({ message: `<#noparse>${paramsProps.value.title}</#noparse>成功！` });
      paramsProps.value.getTableList!();
      visible.value = false;
    } catch (error) {
      console.log(error);
    }
  });
};

defineExpose({
  acceptParams,
});
</script>

<style scoped lang="scss"></style>
