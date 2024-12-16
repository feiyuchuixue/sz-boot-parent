<template>
  <el-dialog
    v-model="visible"
    :title="`<#noparse>${paramsProps.title}</#noparse>`"
    :destroy-on-close="true"
    width="580px"
    draggable
  >
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
        <el-input
          v-model="paramsProps.row.${field.javaField}"
          placeholder="请填写${field.columnComment}"
          clearable
        ></el-input>
          <#elseif field.htmlType == "textarea">
        <el-input
          v-model="paramsProps.row.${field.javaField}"
          placeholder="请填写${field.columnComment}"
          :rows="2"
          type="textarea"
          clearable
        ></el-input>
          <#elseif field.htmlType == "input-number">
        <el-input-number
          v-model="paramsProps.row.${field.javaField}" :precision="0" :min="1" :max="999999" />
          <#elseif field.htmlType == "select" || field.htmlType == "radio">
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
          value-format="YYYY-MM-DD HH:mm:ss"
          placeholder="请选择${field.columnComment}">
        </el-date-picker>
          <#elseif field.htmlType == "time">
        <el-time-picker clearable
          v-model="paramsProps.row.${field.javaField}"
          placeholder="请选择${field.columnComment}">
        </el-time-picker>
          <#else>
        <el-input
          v-model="paramsProps.row.${field.javaField}"
          placeholder="请填写${field.columnComment}"
          clearable
        ></el-input>
          </#if>
      </el-form-item>
        </#if>
    </#list>
    </el-form>
    <template #footer>
      <el-button @click="visible = false"> 取消</el-button>
      <el-button type="primary" @click="handleSubmit"> 确定</el-button>
    </template>
  </el-dialog>
</template>

<script setup lang="ts">
import { ref, reactive } from 'vue'
import { type ElForm, ElMessage } from 'element-plus'
<#if hasSelect == true>
import { useOptionsStore } from '@/stores/modules/options'
</#if>

defineOptions({
    name: '${formClassName}'
})

<#if hasSelect == true>
const optionsStore = useOptionsStore()
</#if>
const rules = reactive({
  <#list columns as field>
  <#if field.isRequired == "1">
  ${field.javaField}: [{ required: true, message: '请填写${field.columnComment}' }],
  </#if>
  </#list>
})

const visible = ref(false)
const paramsProps = ref<View.DefaultParams>({
  title: '',
  row: {},
  api: undefined,
  getTableList: undefined
})

// 接收父组件传过来的参数
const acceptParams = (params: View.DefaultParams) => {
  paramsProps.value = params
  visible.value = true
}

// 提交数据（新增/编辑）
const ruleFormRef = ref<InstanceType<typeof ElForm>>()
const handleSubmit = () => {
  ruleFormRef.value!.validate(async (valid) => {
    if (!valid) return
    try {
      await paramsProps.value.api!(paramsProps.value.row)
      ElMessage.success({ message: `<#noparse>${paramsProps.value.title}</#noparse>成功！` })
      paramsProps.value.getTableList!()
      visible.value = false
    } catch (error) {
      console.log(error)
    }
  })
}

defineExpose({
  acceptParams
})
</script>

<style scoped lang="scss"></style>