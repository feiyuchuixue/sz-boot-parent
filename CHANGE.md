# 更新日志
## v1.2.4-beta （20250614）

- sz-boot-parent：

  - 修复: 菜单路径和组件路径拼写错误"menuManage"。（感谢[Kang-Yang](https://github.com/Kang-Yang)）
  - 修复：异常枚举类message方法并发问题。
  - 重构：将异常枚举类实现改为通用响应枚举模板。
  - 升级：sa-token v1.41.0 -> v1.44.0  && 同步改造。
- sz-admin：

  - 修复: 菜单路径和组件路径拼写错误"menuManage"。（感谢[Kang-Yang](https://github.com/Kang-Yang)）
  - 修复：账户管理批量设置部门后checkbox未重置的问题。
  - 优化：Grid组件，重构字段查找逻辑，提升性能和可读性。
  - 优化：ToolBarRight组件，修改展示项用户名为昵称。 

## v1.2.3-beta （20250603）

- sz-boot-parent：

  - 依赖升级：
    - **spring-boot-starter-parent：3.4.4  -> 3.5.0。**
    - software.amazon.awssdk.crt:aws-crt：0.33.7  -> 0.38.4。
    - software.amazon.awssdk:s3：2.29.50 -> 2.31.54。
    - hutool-jwt：5.8.34 -> 5.8.38。
    - fastexcel：1.1.0 -> 1.2.0。
    - mysql-mysql-connector-j: 9.2.0 -> 9.3.0
    - HikariCP：6.2.1 -> 6.3.0
    - commons-io-commons-io：2.18.0 -> 2.19.0
    - jackson：2.18.2 -> 2.19.0
    - springdoc-springdoc-openapi-starter-webmvc-ui：2.8.3 -> 2.8.8
    - org.aspectj-aspectjweaver：1.9.23 -> 1.9.24
    - modelmapper：3.2.2 -> 3.2.3
    - io.swagger.core.v3-swagger-annotations：2.2.27 -> 2.2.32
    - commons-collections4：4.4 -> 4.5.0
  - 优化：适配解决maven编译时无法识别Lombok和Mybatis-Flex APT注解器导致的”找不到符号“报错问题。 （感谢[JoeyFrancisTribbiani](https://github.com/JoeyFrancisTribbiani)）
  - 修复：FastExcel 依赖 Apache POI漏洞。
  - 优化：logback.xml中`converterClass`已废弃，更换为新语法`class`。
  - 更新：com.diffplug.spotless-spotless-maven-plugin-2.44.5。
  - 优化：cicd 脚本，将admin和websocket合并成一个脚本。
  - 修改：多文件上传不再支持单字符串，统一改为JSON数组形式。
- sz-admin：

  - 优化：多文件上传组件不再支持单字符串，统一改为数组形式。

## v1.2.2-beta （20250528）

> [!NOTE]
>
> [升级指南](https://szadmin.cn/md/Help/doc/other/upgrade.html#v1.2.2-beta)

- sz-boot-parent：

  - 优化：将**继承** `WebMvcConfigurationSupport`改为**实现** `WebMvcConfigurer`，提升框架兼容性。
  - 修复：[数据权限]-一些已知问题。
  - 优化：[代码生成器]
    - 导入数据表添加对createId/updateId/createTime/updateTime常用字段的支持。
    - 前端模版添加对useDict生成的支持
    - SQL Insert 支持IGNORE
    - Teacher演示案例的同步改造
    - 代码模版格式优化
  - 新增: [sz-common-wechat]-新增企业微信消息发送的支持。
- sz-admin：

  - 修复：版本号读取异常问题。
  - 优化：[演示案例] - 教师统计。
  - 优化：UploadFiles多文件上传组件, 简化使用方式并修复一些问题。

## v1.2.1-beta （20250509）

> [!NOTE]
>
> [升级指南](https://szadmin.cn/md/Help/doc/other/upgrade.html#v1.2.1-beta)

- sz-boot-parent：

  - 新增：Liquibase数据库管理，弃用Flyway配置 (**Flyway 将于v1.3.0-beta 版本弃用**)。
  - 修改：已支持Liquibase，默认配置关闭Flyway（将于v1.3.0-beta版本弃用）。
  - 修复：部分菜单路由名称与组件名称不一致时导致的菜单keep-alive缓存失效问题。
  - 修改：README.md 添加deepwiki。感谢[dongyu6/main](https://github.com/dongyu6)。

- sz-admin：

  - 优化：.env环境变量，无需指定`.env.development.local `文件即可使用。
  - 升级：sass 1.79.6 -> 1.87.0, vite 5.4.17 -> 6.3.4，以及其他依赖的同步升级。
  - 修复：部分菜单路由名称与组件名称不一致时导致的菜单keep-alive缓存失效问题。
  - 修改：README.md 添加deepwiki。（感谢[dongyu6/main](https://github.com/dongyu6)）。

## v1.2.0-beta （20250422）
> [!NOTE]
>
> [升级指南](https://szadmin.cn/md/Help/doc/other/upgrade.html#v1-2-0-beta)
- sz-boot-parent：
  - 优化：[代码生成器] - 为生成代码增加是否忽略表前缀的功能。PR[#140](https://github.com/feiyuchuixue/sz-boot-parent/pull/140)（感谢[crash](https://github.com/processcrash)）。
  - 优化：重构**数据权限**核心逻辑，修复部分问题。
  - 修复：[数据填充]-deptScope属性失败问题
  - 优化：Websocket相关：简化SocketMessage和TransferMessage类的泛型使用
  - 新增：系统消息功能
  - 新增：[演示] 消息发送接口
  - 新增：[CICD] Docker Sz-Socket CI Prod.yml
  - 优化：提高用户元数据变更性能
- sz-admin：
  - 修复：滑块验证码在某些浏览器无法滑动的问题
  - 新增：分类筛选器组件
  - 优化：滑块验证码的耗时计算逻辑
  - 新增：系统消息功能（搭配Websocket可体验完整功能）
  - 新增：功能演示
  - 新增：关于项目
  - 新增：全局菜单：[消息、功能演示菜、关于项目]

## v1.1.0-beta （20250406）

> [!NOTE]
>
> [升级指南](https://szadmin.cn/md/Help/doc/other/upgrade.html)

- sz-boot-parent：
  - 依赖升级：
    - spring-boot-starter-parent：3.4.2  -> 3.4.4。
    - mybatis-flex.version：1.10.8  -> 1.10.9。
    - sa-token：1.40.0 -> 1.41.0。
    - swagger-annotations：2.2.27 -> 2.2.29。
    - aspectjweaver：1.9.22.10 -> 1.9.23。
    - springdoc-openapi-starter-webmvc-ui: 2.8.3 -> 2.8.6
    - aws-crt： 0.33.7 -> 0.37.0
    - HikariCP：6.2.1 -> 6.3.0
    - aws.s3：2.29.50 -> 2.31.11
  - 优化：[代码生成器] - 添加/api/types/*.ts生成模板
- sz-admin：
  - 升级：Eslint8.x -> Eslint9
  - 新增：`useDictOptions` Hook
  - 优化：同步`useDictOptions` Hook写法
  - 修复：某些情况下部门树多选报错的问题
  - 优化：@import scss 官方已不推荐使用，修改为 @use
  - 更新：pnpm 依赖

## v1.0.2-beta （20250302）

- sz-boot-parent：
  - 依赖升级：
    - mybatis-flex.version：1.10.7  -> 1.10.8。
  - 优化：更新OSS配置，添加协议scheme支持并**弃用isHttps字段**。
    -  <font color="red">可能的破坏性更新： </font>请切换`isHttps=true/false"` 为` scheme="https/http"`
  - 优化：重构isNotNull方法，支持更广泛的集合类型。
  - 优化：重构BeanCopyUtils以使用单例ModelMapper实例。
  - 优化：【代码生成器】添加将bigint类型映射成long Java类型处理。
  - 优化：在EntityChangeListener onInsert事件中添加对updateTime和updateId的初始设置。
  - 修复：Excel导出时Long类型在某些情况下报错的问题。
  - 新增：系统字典查询-根据类型查询接口。
- sz-admin：
  - 新增：[Hook] useDict 方法。（可使用此方法更新指定typeCode的字典缓存）。
  - 新增：[Hook] useDict 的演示案例。
  - 修改：删除字典接口注释。**Issue**[#11](https://github.com/feiyuchuixue/sz-admin/issues/11)（感谢[Kang-Yang](https://github.com/Kang-Yang)）。
  - 修改：README中的地址更正。**Issue**[#12](https://github.com/feiyuchuixue/sz-admin/issues/12)（感谢[Kang-Yang](https://github.com/Kang-Yang)）。
## v1.0.1-beta （20250215）

- sz-boot-parent：
  - 依赖升级：
    - spring-boot-starter-parent：3.4.1  -> 3.4.2。
    - mybatis-flex.version：1.10.5  -> 1.10.7。
    - sa-token：1.39.0 -> 1.40.0。
    - excel-fastexcel：1.0.0 -> 1.1.0。
    - mysql-connector-j：9.1.0 -> 9.2.0。
  - 优化：字典类型删除时同步清除缓存。
  - 优化：统一异常code规范，追加prefix。
  - 优化：指定目标账户密码修改后，触发“踢下线”功能。
  - 修改：菜单树增加返回参数。
  - 优化: 日志格式。**Issue**[#10](https://github.com/feiyuchuixue/sz-admin/issues/10)。（感谢[129duckflew](https://github.com/129duckflew)）。
  - 新增：第三方开源库和许可证文件说明
- sz-admin：
  - 修复：无效Token响应码不一致的问题。
  - 修复：AES-GCM加密方法在某些场景（浏览器）不可用的问题（行为验证码）。
  - 优化：【代码生成器】- 生成信息中**上级菜单=目录**时与模块名的联动。
  - 新增：第三方开源库和许可证文件说明
## v1.0.0-beta （20250128）| 大型更新

- sz-boot-parent：
  - 优化：sonar 代码（质量）规范化
- sz-admin：
  - 优化：sonar 代码（质量）规范化
## v0.9.0 （20250119）

- sz-boot-parent：
  - 优化：无效文件清理
  - 优化：javadoc 注释
  - 优化：[sz-service-websocket] 同步调整config路径至项目根目录下
  - 优化：qodana 代码（质量）规范化
- sz-admin：
  - 无
## v0.8.8 （20250118）

- sz-boot-parent：
  - 修改：Jackson序列化添加对`MultipartFile`类型的支持。
  - 修改：`router.whitelist` 属性为Set结构。
  - 优化：系统用户更新时，同步更新缓存信息。
  - 优化：[行为验证码-滑块验证] 增加对double精度的支持。
  - 优化：接口白名单，删除非必要的放行接口。
  - 修复：aop日志打印的一些问题（ http-topic.log）。
  - 修复：[代码生成器] 预览时插入按钮SQL问题。
  - 修复：EntityChangeListener 在处理未登录用户数据初始化时的异常问题。
  - 修复：验证码参数`sys.captcha.requestLimit`未启用时redis中仍然记录了次数的问题。
  - 修复：[部门管理] 上级部门为`根部门`时编辑校验未通过的问题。
  - 依赖升级：
    - spotless-maven-plugin：2.43.0 -> 2.44.1。
    - mybatis-flex.version：1.10.2  -> 1.10.5。
    - aws.s3.version：2.29.23 -> 2.29.50。
    - springdoc-openapi-starter-webmvc-ui：2.7.0  ->  2.8.3。
    - modelmapper：3.2.1 -> 3.2.2。
    - swagger-annotations：2.2.26 -> 2.2.27。
- sz-admin：

  - 修复：个别浏览器Socket异常的问题。
  - 优化：[行为验证码-滑块验证] 添加对移动端浏览器的支持。
## v0.8.7 （20250109）

- sz-boot-parent：
  - 新增：`sz.cors.allowedOrigins`配置项，允许用户通过配置的方式指定限定域名
  - 修复：springboot启动时打印logback配置信息的问题 && 优化logback配置
  - 优化：接口防抖逻辑
    - 当全局设置忽略GET请求的参数为true时，如果GET请求的Controller未标注@Debounce注解，则跳过防抖处理；但若Controller标注了@Debounce注解，即使是GET请求，也会执行防抖逻辑。
  - 新增：行为验证码-滑块验证。感谢([阳纸伞](https://github.com/1327614618))
  - 新增：[演示案例] 远程搜索下拉选择组件
- sz-admin：

  - 新增：行为验证码-滑块验证
  - 新增：远程搜索下拉选择组件
  - 新增：[演示案例] 远程搜索下拉选择组件
## v0.8.6 （20250102）

- sz-boot-parent：
  - 修复: sys_config 缓存时间问题。
  - 修改: 移动配置文件至项目【根目录】下。|  <font color="red">可能的破坏性更新 </font>
  - 修改: 移除pom镜像源配置
  - 修改: Dockerfile 增加配置目录挂载的支持
  - 新增: GitHub Action workflow |
- sz-admin：

  - 修复：VITE自定义变量验证问题
  - 新增：gzip打包支持
  - 新增：Dockerfile
  - 修改：.env.production 配置
  - 新增：GitHub Action workflow
## v0.8.5 （20241229）

- sz-boot-parent：
  - 优化：commons-logging 引用冲突问题。
  - 修复：部门编辑时层级deep赋值不正确问题。
  - 修复：前端module模板文件导入excel缺少参数问题。
  - 修复：部门列表节点数量展示问题。
  - 优化：升级EasyExcel为FastExcel。 |  <font color="red">可能的破坏性更新 （easyExcel -> fastExcel 的Package包切换）</font>
  - 优化：增强 Excel 导入异常处理，新增表头校验功能。详见[Excel导入导出](https://szadmin.cn/md/Help/doc/excel.html)
  - 依赖升级：
    - spring-boot-starter-parent：3.4.0 -> 3.4.1。
- sz-admin：

  - 优化：文件上传模板组件样式。（感谢[Alex-1116](https://github.com/Alex-1116)）
  - 优化：Excel导入组件，增加上传进度的支持。
  - 优化：axios对全局response error的处理。
## v0.8.4 （20241216）

- sz-boot-parent：
  - 依赖升级：
    - spring-boot-starter-parent：3.3.5  -> 3.4.0。
    - mybatis-flex.version：1.9.7  -> 1.10.2。
    - aws-crt：0.33.0 -> 0.33.3。
    - hutool-jwt：5.8.32 -> 5.8.34。
    - aws.s3：2.29.0 -> 2.29.23。
    - HikariCP：6.0.0 -> 6.2.1。
    - common-io：2.17.0 -> 2.18.0。
    - lombok：1.18.34 -> 1.18.36。
    - jackson：2.17.2 -> 2.18.2。
    - swagger-annotations：2.2.25 ->  2.2.26。
    - mysql-connector-j：9.0.0 -> 9.1.0。
  - 修改：SpringBoot升级3.4.0后对knife4j的兼容性处理 |  <font color="red">兼容性更新，springboot升级3.4.0后knife4增强默认需禁用！！</font>
  - 删除：minio dependency。
  - 优化：代码生成器查询。 **PR**[#57]([https://github.com/feiyuchuixue/sz-boot-parent/pull/57)。（感谢**[AiMing317](https://github.com/AiMing317)** ）。
  - 优化：[代码生成器] 修复若干问题。
  - 新增：ossClient，新增oss文件流下载方法。
  - 优化：FileUtils 新增对response header的处理方法。
  - 优化：历史MapperXml/PO的结构映射。
  - 新增：模板文件管理模块。
- sz-admin：

  - 优化：头像框样式。
  - 优化：代码生成器查询。 **PR**[#57]([https://github.com/feiyuchuixue/sz-boot-parent/pull/57)。（感谢**[AiMing317](https://github.com/AiMing317)** ）。
  - 优化：Img、Imgs上传组件增加@change事件可获取完整的UploadResul。
  - 优化：[代码生成器] 修复一些问题，样式及便利性更新。
  - 优化：View.DefaultParams添加参数isAdd。可用于区分新增 OR 编辑。
  - 优化：useDownload组件！blob流式下载文件名从response中获取。
  - 新增：模板文件管理模块。
  - 修改：更新教师统计模板文件名。

## v0.8.3 （20241126）

- sz-boot-parent：

  - **删除：minio模块。** |  <font color="red">可能的破坏性更新</font>
  - 新增：oss模块，使用**AWS S3**协议，支持更多云存储厂商（阿里、七牛、腾讯、minio等）。
  - 修改：切换minio模块至oss模块，切换上传方法至ossClient。请将minio.yml文件切换为oss.yml。
  - 优化：移除冗余NotNull注解。
- sz-admin：

  - 新增：文件管理。

  - 修改：oss模块同步改动。

  - 新增：vite-plugin-vue-devtools插件。

  - 优化：完善UploadResult返回结构，优化文件代码格式。

  - 新增：图片上传、批量图片上传组件 （感谢 **Geeker-Admin** https://github.com/HalseySpicy/Geeker-Admin）。

  - 修改：切换用户头像上传为新的组件。
- 文档：

  - [oss存储](https://szadmin.cn/md/Help/doc/oss.html)

## v0.8.2 （20241119）

- sz-boot-parent：
  - 修复: 菜单详情页权限唯一值校验异常问题。
  - 修复: [代码生成] MySql5.7 导入表异常问题。
  - 新增: [代码生成] 对字典别名的支持。
  - 优化: 字典类型（sysDictType）增加缓存同步机制。
- sz-admin：
  - 修复：客户端管理列表，授权类型展示问题。
  - 新增：[代码生成] 对字典别名的支持。

## v0.8.1 （20241106）

- sz-boot-parent：
  - 依赖升级：
    - spring-boot-starter-data-redis：3.3.4  -> 3.3.5。
    - spring-boot-starter-parent：3.3.4  -> 3.3.5。
  - 优化：sz-service-socket添加启动Banner与版本号。
  - 优化：部门列表-未设置部门节点用户数量查询逻辑调整
  - 优化：账户登录的查询。
  - 修复：excel导出字典CodeName为null时的异常问题。
  - 优化：Excel导入导出及字典转换，提升性能。
- sz-admin：
  - 优化: vite.config.mts更新 SCSS 预处理器配。
  - 优化: 锁定sass版本为~1.79.x版本。
  - 优化: tsconfig.app.json 增加package.json的支持。
  - 优化: 使用TypeScript的模块扩展，优化package.json 的version。
  - 优化: 增强 env.d.ts 文件中的类型定义。

- 文档：

  - [Excel导入导出](https://szadmin.cn/md/Help/doc/excel.html)

## v0.8.0 （20241017）

- sz-boot-parent：
  - 依赖升级：
    - spring-boot-starter-data-redis：3.3.3  -> 3.3.4。
  - 修复：sql预览模版结尾多余空格问题。
  - 修复：无法正常解锁用户问题 **PR**[#26](https://github.com/feiyuchuixue/sz-boot-parent/issues/26#issue-2577509320)。（感谢[andyjia](https://github.com/andyjia)）
  - 优化：重构异常处理，引入错误前缀枚举，增强代码可读性和可维护。
  - 新增：spotless maven 格式化插件，全局格式化。
  - 新增：CHANGE.md 更新日志。
  - 修改：将数据表中的自增ID字段类型升级为**bigint**，以支持更大的数据范围和避免潜在的溢出问题。|  <font color="red">可能的破坏性更新</font>
  - 优化：自增ID字段升级为bigint：
    - 代码生成
    - 系统菜单
    - 系统字典
    - 角色
    - 用户
    - 教师统计（演示）
  - 新增：自增ID字段升级为bigint DDL脚本。
  - 修复：部分页面批量删除异常问题。
  - 优化：[代码生成] 前端模版添加对(string | number)[] 类型的支持。
  - 优化：验证权限标识唯一性接口。
  - 优化：接口防抖增加GET请求忽略的支持。
  - 优化：通用返回接口对额外参数param的处理。
  - 优化：[代码生成] 模版对菜单预览SQL的调整。
  - 优化：数据库迁移脚本 --业务脚本 示例修改。
- sz-admin：
  - 依赖升级：
    - **vite**：**4.5.3 -> 5.4.8**。
    - **axios**： 1.7.2 -> 1.7.7
    - **vue**：3.4.21 -> 3.5.12
    - pinia：2.1.7 -> 2.2.4
    - **vue-router**：**4.4.0 -> 4.4.5**
    - sortablejs：1.15.2 -> 1.15.3
    - pinia-plugin-persistedstate： 3.2.1 -> 3.2.3
    - @vueuse/core：10.11.0 -> 10.11.1
    - prettier：3.3.2 -> 3.3.3
    - @types/node：18.19.39 -> 18.19.55
    - **element-plus**：**2.7.6 -> 2.8.5**
    - **sass**：**1.77.7 -> 1.79.5**
    - @vue/tsconfig：0.4.0 -> 0.5.1
    - vue-tsc：1.8.27 -> 2.1.6
  - 修复：菜单管理、字典管理 预览SQL的格式问题。
  - 修复：代码预览组件行号展示问题。
  - 优化: 更新API状态码常量。
  - 优化：提取请求超时时间参数**VITE_APP_HTTP_TIMEOUT** 单位ms，（默认超时时间60s）。
  - 优化: useSelection组件添加对number数组的支持。
  - 优化: 修改API参数类型以支持数字和字符串。
  - 优化: 角色标识展示样式。
  - 优化: vite5.x 使用ESM语法 vite.config.ts 升级为vite.config.mts。
  - 优化: eslintrc.cjs 规则。
  - 优化: prettier format配置。
  - 优化：代码格式化，代码清理。

## v0.7.11 （20241009）

- sz-boot-parent：
  - 依赖升级：
    - spring-boot-starter-data-redis：3.3.3  -> 3.3.4。
  - 修复：sql预览模版结尾多余空格问题。
  - 修复：无法正常解锁用户问题 **PR**[#26](https://github.com/feiyuchuixue/sz-boot-parent/issues/26#issue-2577509320)。（感谢[andyjia](https://github.com/andyjia)）
  - 优化：重构异常处理，引入错误前缀枚举，增强代码可读性和可维护。
  - 新增：spotless maven 格式化插件，全局格式化。
  - 新增：CHANGE.md 更新日志。
  - 修改：将数据表中的自增ID字段类型升级为**bigint**，以支持更大的数据范围和避免潜在的溢出问题。|  <font color="red">可能的破坏性更新</font>
  - 优化：自增ID字段升级为bigint：
    - 代码生成
    - 系统菜单
    - 系统字典
    - 角色
    - 用户
    - 教师统计（演示）
  - 新增：自增ID字段升级为bigint DDL脚本。
  - 修复：部分页面批量删除异常问题。
  - 优化：[代码生成] 前端模版添加对(string | number)[] 类型的支持。
  - 优化：验证权限标识唯一性接口。
  - 优化：接口防抖增加GET请求忽略的支持。
  - 优化：通用返回接口对额外参数param的处理。
  - 优化：[代码生成] 模版对菜单预览SQL的调整。
  - 优化：数据库迁移脚本 --业务脚本 示例修改。

## v0.7.10 （20240919）

- sz-boot-parent：
  - 依赖升级：
    - com.alibaba:easyexcel：4.0.2  -> 4.0.3。
  - 修复：某些情况下账户列表查询的分页问题。
  - 修复：数据权限组合拼接条件时OR、AND的优先级问题。
  - 新增：接口防抖功能。
  - 优化：sz自定义配置格式统一化。
- sz-admin：
  - 优化：WebSocket验证逻辑。
  - 优化：env环境变量。
  - 修复：dict接口登录时请求两次的问题。

## v0.7.9 （20240913）

- sz-boot-parent：
  - 依赖升级：
    - org.apache.commons:commons-lang3：3.16.0  -> 3.17.0
    - swagger-annotations： 2.2.21  -> 2.2.23.
    - org.aspectj:aspectjweaver：1.9.22 -> 1.9.22.1
    - mysql-connector-j： 8.4.0 -> 9.0.0
    - hutool-jwt： 5.8.31 -> 5.8.32
  - 新增：BeanCopyUtils 增加copy, copyNotIgnoreNull方法。
  - 优化：**数据权限**映射表名的获取规则：优先使用@Table注解名称属性。
  - 修复：角色管理标识空值校验问题。
  - 优化：[代码生成] 前端搜索项独立enum Select的支持。
  - 修复: @SaIgnore注解失效问题。
  - 优化: @SaIgnore的使用场景，[详见文档](https://szadmin.cn/md/Help/doc/code-standard.html#_9-%E6%8E%A5%E5%8F%A3%E9%89%B4%E6%9D%83) 。
  - 优化: 账户detail详情查询接口，排除敏感信息。
- sz-admin：
  - 优化：改造SearchForm组件，添加对enum的独立支持。（感谢[Alex-1116](https://github.com/Alex-1116)）
  - 修改：[教师统计] searchColumns enum演示。

## v0.7.8 （20240902）

- sz-boot-parent：
  - 依赖升级：
    - spring-boot-starter-parent：3.2.5  -> 3.3.3
    - jackson：2.16.1 -> 2.17.2
    - hutool-jwt：5.8.27 -> 5.8.31
    - easyexcel： 3.3.4 -> 4.0.2
    - aspectjweaver： 1.9.22 -> 1.9.22.1
    - commons-lang3：3.14.0 -> 3.16.0
    - mybatis-flex-spring-boot3-starter：1.9.3 -> 1.9.7
    - modelmapper：3.2.0 -> 3.2.1
    - minio：8.5.10 -> 8.5.12
    - sa-token：1.38.0 -> 1.39.0
  - 修复：代码生成，zip生成时流未关闭的问题。
  - 修复：ftl sql模板int类型参数自动转换千分位的问题。
  - 修复：字典更新问题。
  - 优化：类名、包结构。
  - 修改：MybatisFlex**禁用全局null值自动忽略**。代码生成器模版、部分业务查询拼接同步改动。|增强逻辑清晰和确定性。
- sz-admin：
  - 修改：客户端管理Form项描述错误
  - 优化：[代码生成] 编辑Form，字典类型下拉展示。区分静态字典和动态字典。（感谢[Alex-1116](https://github.com/Alex-1116)）

## v0.7.7 （20240823）

- sz-boot-parent：
  - 优化：包结构及代码。
  - 修复：字典sql导出ID格式问题。
  - 修复：[代码生成] LocalDateTime范围查询Search区域展示异常问题。
  - 新增：字典改造，对静态字典、动态字典提供支持。
  - 优化：DictVO对象，完善字典查询sql增加对逻辑删除的支持。
  - 修改：SysUser 动态字典：用户信息的支持。
  - 优化：TeacherStatics演示示例，@DictFormat注解isSelected()的调整等。感谢[阳纸伞](https://github.com/1327614618)的贡献代码。
- sz-admin：
  - 优化：账户管理-切换部门列表时list接口请求两次额问题。

## v0.7.6 （20240814）

- sz-boot-parent：
  - 修复：[代码生成] excel导出条数与列表查询数不符的问题。
  - 修复：[代码生成] 关联字典的查询条件，生成类型为input而非select的问题。
  - 修复：[代码生成] 新增菜单类型【菜单】时，deep层级赋值不正确问题。
  - 优化：[代码生成] 代码生成逻辑，修复一些bug。
  - 优化：添加business业务Flyway README描述文件。
  - 优化：遵循jdk21建议，使用@Serial注解标识serialVersionUID。
  - 优化：角色增加is_lock, permissions属性。
  - 修复：字典菜单SQL查看时某些原因无法展示的问题。
- sz-admin：
  - 优化：代码生成字段类型添加描述。
  - 优化：角色管理增加标识属性（permissions）、锁定状态（is_lock）。

## v0.7.5 （20240812）

- sz-boot-parent：
  - 优化：账户新增时，如果选择了可用的部门，将账户创建到指定部门。
  - 新增：字典管理，SQL导出功能。
  - 优化：代码生成模板格式。
- sz-admin：
  - 修复：Header结构Avatar组件 默认头像展示问题。（感谢[Alex-1116](https://github.com/Alex-1116)）
  - 优化： 代码生成HighCode组件 增加行号展示。（感谢[Alex-1116](https://github.com/Alex-1116)）
  - 修复：账号管理 新增用户后 与部门列表的通讯问题。（感谢[Alex-1116](https://github.com/Alex-1116)）
  - 优化：代码生成编辑操作 根据导入导出checkbox 动态展示对应columns。（感谢[Alex-1116](https://github.com/Alex-1116)）
  - 优化：账户新增时，如果选择了可用的部门，将账户创建到指定部门下。
  - 新增：字典管理，SQL导出功能。
  - 修复：代码生成组件 行号 头部标题固定。（感谢[Alex-1116](https://github.com/Alex-1116)）

## v0.7.4 （20240730）

- sz-boot-parent：
  - 优化：业务与框架Flyway迁移脚本分离

    - 新增多 Flyway 实例配置，分别管理业务和框架迁移脚本。
    - 修改 `flyway.yml` 配置项，实现配置的清晰划分。
    - 利用 `@ConfigurationProperties` 自动绑定配置项，创建独立的 Flyway 实例
    - 各自使用独立的历史版本表（t_db_version 和 t_db_version_business）以避免相互干扰
    - 优化迁移管理，提高数据库版本管理的灵活性和扩展性
    - 更新了flyway的ddl脚本路径，在classpath:/db 路径下，新增framework、business路径，并**将原 classpath:/db 路径下 的DDL文件移动至 classpath:/db/framework下**。|  <font color="red">可能的破坏性更新</font>

    > [!IMPORTANT]
    >
    > - **重要通知：数据库迁移操作**
    >
    >   为了确保数据库迁移的顺利进行，我们特别提醒您注意以下步骤：
    >
    >   1. **迁移DDL脚本**：请将所有使用Flyway编写的自定义DDL（非框架提供的v1.1~v1.8版本）迁移脚本移动至`classpath:/db/business`目录下。
    >   2. **重新规划DDL版本**：在迁移脚本完成后，重新规划并更新DDL的版本号，确保版本控制的一致性和可追溯性。
    >   3. **数据备份**：**务必在执行迁移操作之前**，对现有数据库进行全面的数据备份。这是保障数据安全的关键步骤，避免在迁移过程中发生数据丢失或损坏的风险。
    >   4. **执行迁移操作**：在确认数据备份无误后，按照既定的迁移计划执行DDL迁移操作。

## v0.7.3 （20240728）

- sz-boot-parent：
  - 修复：部门逻辑删除后，account数据展示问题。
  - 优化：permission唯一性校验。
  - 新增：逻辑删除自动填充支持，delete_id,delete_time属性。
  - 优化：代码生成器，提取配置文件。
  - 优化：代码生成器，menu生成Sort逻辑。
  - 修复：预览时检查菜单重复的问题。
- sz-admin：
  - 优化：permission唯一性校验。
  - 优化：码生成器编辑Form表单的交互体验。
- 官网文档：
  - 新增：[代码生成器](https://szadmin.cn/md/Help/gen/generator-tools.html)文档。
## v0.7.2 （20240719）

> [!WARNING]
>
> **数据权限的session存储结构部分发生了改变，移除了customUserIds、customDeptIds。启用了userRuleMap、deptRuleMap来配合灵活的自定义规则。**

- sz-boot-parent：
  - 修改：数据权限，逻辑优化。减少用户操作，提升用户体验。
- sz-admin：

  - 修改：数据权限Form，优化交互逻辑。
  - 修复：数据权限，编辑后再新增操作项disable的问题。
- 官网文档：

  - 修改：[数据权限文档](https://szadmin.cn/md/Help/doc/data-scope.html) 对部分逻辑进行了简化。

## v0.7.1 （20240717）

- sz-boot-parent：

  - 修复：代码生成pojo类注释名称问题

  - 优化：用户管理功能，代码清理

  - 修复：TreeUtils constructTreeRecursiveExcludeNode方法忽略指定节点不生效的问题

  - 修复：AOP url参数解析异常问题

  - 优化：文件清理，命名规范化

  - 新增：StringUtils toSnakeCase方法

  - **修改：字典管理，添加业务类型，区分系统字典、业务字典**  |  <font color="red">可能的破坏性更新</font>

    > [!IMPORTANT] ！重要
    >
    > 1000号段为业务性字典，如有新增的字典请迁移至2000号段。受影响的字典类型值为“1006”、“1007”。

  - 新增：数据权限实现

- sz-admin：

  - 优化：账户添加Form tooltip
  - 修复：菜单缓存失效问题 [#IA8QI1](https://gitee.com/feiyuchuixue/sz-admin/issues/IA8QI1) | Gitee
  - 优化：角色权限Form禁止esc关闭及点击其他区域关闭
  - 修复：socket参数不存在时仍连接socket的问题
  - 新增：数据字典-业务类型
  - 修复：默认头像无法展示问题
  - 修复：home页切换“分栏”布局报错问题
  - 修复：查询条件项在某些条件下无法展示全的问题
  - 优化：代码清理，规范化命名
  - 新增：数据权限
- 官网文档：

  - 新增：[数据权限文档](https://szadmin.cn/md/Help/doc/data-scope.html)


## v0.6.5 （20240619）

- sz-boot-parent：
  - 修复：未登录状态接口自动数据填充异常问题
  - 修复：代码生成，批量删除异常问题
  - 升级：升级Mybatis-Flex版本v1.9.3

## v0.6.4 （20240612）

- sz-boot-parent：
  - 修复：权限问题orRole不生效的问题 [#IA4F9Z](https://gitee.com/feiyuchuixue/sz-boot-parent/issues/IA4F9Z)
  - 新增：查询用户角色接口
- sz-admin：
  - 新增：权限校验（v-auth指令） 对超管角色的处理。env 新增 VITE_ADMIN_BYPASS_PERMISSION属性。

## v0.6.3 （20240609）

- sz-boot-parent：
  - 修复：代码生成器拖拽排序丢失问题
  - 优化：代码生成器-字段信息编辑模板调整，增加数据库模板
  - 优化：代码生成菜单唯一性检查逻辑
  - 修改： 升级Mybatis-Flex版本v1.9.2
- sz-admin：
  - 优化：代码生成器-字段信息编辑、生成信息编辑的操作逻辑
  - 优化：代码生成器-字段信息编辑 选择模板功能区，增加数据库模板选项，完善提示信息
  - 修复：ElMessage提示信息展示不在顶层的问题
  - 优化：页脚增加版本号展示

## v0.6.2 （20240605）

- sz-boot-parent：
  - 优化：将密码错误次数、错误冻结时间等魔法值提取到参数管理中
- sz-admin：
  - 优化：优化nginx推荐配置，解决网络原因导致socket频繁断开的问题
- 官网文档：
  - 新增：代码规范，前端代码提交前检查
  - 修改：序言，商业用途免费。
  - 优化：技术栈增加技术依赖超链接
  - 新增：websocket Nginx配置建议
  - 修改：感谢，对前端贡献者s1990218yao增加描述
  - 修改：页脚，增加友链

## v0.6.1 （20240603）

- sz-boot-parent：
  - 修改：admin账户初始密码为**sz123456**
  - 优化：更新README文档
- sz-admin：
  - 修改：登陆页面placeholder
  - 优化：更新README文档

----

## v0.6.0 （20240602）

- sz-boot-parent：
  - init
- sz-admin：
  - init
- sz-deploy：
  - init
