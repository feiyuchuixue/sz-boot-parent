<?xml version="1.0" encoding="UTF-8"?>
<!DOCTYPE mapper PUBLIC "-//mybatis.org//DTD Mapper 3.0//EN" "http://mybatis.org/dtd/mybatis-3-mapper.dtd">
<mapper namespace="${mapperPkg}.${mapperClassName}">
    <!-- 通用映射 -->
    <resultMap id="BaseResultMap" type="${poPkg}.${poClassName}">
    <#list columns as field>
        <#-- 主键 -->
        <#if field.isPk == "1" >
        <id column="${field.columnName}" property="${field.javaField}"/>
        <#-- 普通字段 -->
        <#else>
        <result column="${field.columnName}" property="${field.javaField}"/>
        </#if>
    </#list>
    </resultMap>

    <!-- 通用查询结果列 -->
    <sql id="Base_Column_List">
        <#list columns as field>${field.columnName}<#if !field?is_last>, </#if></#list>
    </sql>

</mapper>
