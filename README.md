<h1 align="center" style="margin: 30px 0 30px; font-weight: bold;">Sz-Admin</h1>
<h4 align="center">基于SpringBoot3、Vue3的轻量级脚手架</h4>
<p align="center">
<a href="https://github.com/feiyuchuixue/sz-boot-parent/stargazers"><img src="https://img.shields.io/github/stars/feiyuchuixue/sz-boot-parent?style=flat-square&logo=GitHub"></a>
<a href="https://github.com/feiyuchuixue/sz-boot-parent/network/members"><img src="https://img.shields.io/github/forks/feiyuchuixue/sz-boot-parent?style=flat-square&logo=GitHub"></a>
<a href='https://gitee.com/feiyuchuixue/sz-boot-parent/stargazers'><img src='https://gitee.com/feiyuchuixue/sz-boot-parent/badge/star.svg?theme=dark' alt='star'></img></a>
<a href='https://gitee.com/feiyuchuixue/sz-boot-parent/members'><img src='https://gitee.com/feiyuchuixue/sz-boot-parent/badge/fork.svg?theme=dark' alt='fork'></img></a>
<a href="https://github.com/feiyuchuixue/sz-boot-parent/blob/main/LICENSE"><img src="https://img.shields.io/badge/license-Apache_2.0-blue.svg"></a>
<a href="https://deepwiki.com/feiyuchuixue/sz-boot-parent"><img src="https://deepwiki.com/badge.svg" alt="Ask DeepWiki"></a>
</p>


# 简介

> 接触了很多优秀的开源和闭源项目，在使用过程中也发现一些问题，不甘满足的我遂产生了想法：于是利用休息时间编写了一套后台管理系统，它**灵活、简洁、高效**，拥抱最新的技术，因此**Sz-Admin**便诞生了，也意为升职Admin，升职加薪节节高。

**[Sz Admin](https://szadmin.cn/)** ，一个基于 Spring Boot 3、Vue 3 和 Element-Plus 的开源中后台管理框架，致力于为您提供一个流畅、直观且功能强大的开发框架。它不仅融合了最新的技术趋势，而且通过精心设计，确保了系统的简洁性和高效，让使用者可以专注业务。



## 在线体验

- 官网地址：https://szadmin.cn
- 文档地址：https://szadmin.cn/md/Help/doc/info/start.html
- 预览地址：https://preview.szadmin.cn
- 代码仓库：
    - 前端：
      - **Github**：**[sz-admin](https://github.com/feiyuchuixue/sz-admin.git)**
      - **Gitee**：**[sz-admin](https://gitee.com/feiyuchuixue/sz-admin.git)**
    - 后端：
      - **Github**：**[sz-boot-parent](https://github.com/feiyuchuixue/sz-boot-parent.git)**
      - **Gitee**：**[sz-boot-parent](https://gitee.com/feiyuchuixue/sz-boot-parent.git)**
    - 部署：
      - **Github**：**[sz-deploy](https://github.com/feiyuchuixue/sz-deploy.git)**
      - **Gitee**：**[sz-deploy](https://gitee.com/feiyuchuixue/sz-deploy.git)**

## 系统要求

- JDK >= 21
- MySQL >= 8.0.34
- Maven >= 3.8
- Node >= 18.x

## 核心技术

- **SpringBoot 3.x：** 最新的Spring Boot版本，提供更优的性能和更丰富的特性。
- **Sa-Token**：一个轻量级 Java 权限认证框架，简化权限认证，保障应用的安全性。
- **Mybatis Flex**：一个优雅的 `MyBatis` 增强框架，它非常轻量、同时拥有极高的性能与灵活性。
- **Flyway**：`数据库版本控制`工具，确保数据库迁移的可靠性。
- **Knife4j**：一个为 `Swagger` 接口文档增强的工具，提供了更直观的 API 文档展示和更便捷的接口测试体验。
- ~~**Minio**：一个开源的对象存储服务，提供高性能、分布式存储解决方案，兼容 S3 API。~~
- **AWS S3：** 一个广泛兼容的存储解决方案。通过采用 AWS S3 协议，我们的服务现在能够无缝集成并兼容多种对象存储服务，包括但不限于 MinIO、阿里云OSS和腾讯云OSS等。
- **HikariCP**：选择 `HikariCP` 作为 JDBC 连接池，提供快速且高效的数据库连接管理。

- **Vue 3.x**：采用 `Vue 3.x`，Vue.js 的最新稳定版本，提供更强的性能和更丰富的功能，构建响应式用户界面。
- **Vite 5.x**：使用 `Vite 5`.x 作为前端开发和构建工具，它利用现代浏览器的原生 ES 模块导入特性，提供了快速的冷启动和即时模块热更新。
- **TypeScript**：通过 `TypeScript` 的集成，引入静态类型检查，增强了代码的可维护性和可读性，提前避免潜在的错误。
- **Pinia**：状态管理采用 `Pinia`，这是 Vue 3 的解构式状态管理库，它简单、灵活且易于使用，优化了应用的状态管理。
- **Element-Plus**：一个基于 Vue 3 的组件库，提供了一系列高质量的 UI 组件，帮助开发者快速构建美观、功能完备的用户界面。

## 功能列表

- **账户管理**：负责管理系统用户的创建、配置及权限分配，确保用户身份的合法性和操作的合规性。
- **角色管理**：实现角色与权限的精细绑定，通过角色分配简化用户权限管理，提高系统安全性和灵活性。
- **菜单管理**：定制化系统导航结构，通过权限细分确保用户仅访问授权的操作界面，增强操作的直观性和可控性。
- **字典管理**：维护系统内静态数据字典，如配置项、枚举值等，以统一管理和优化数据的一致性。
- **参数管理**：动态调整系统运行参数，无需重启即可实时生效，提升系统响应速度和运维效率。
- **客户端管理**：监管客户端接入，确保客户端的合法性和安全性，维护系统的整体稳定性。
- **部门管理**：构建组织架构，通过树状结构展示，支持数据权限的层级化管理，加强信息的有序性和安全性。
- **代码生成器**：自动化生成前后端代码模板，支持CRUD操作，加速开发周期，提升开发效率。
- **WebSocket**：提供WebSocket支持。
- **数据权限支持**：通过精细控制和灵活配置，确保用户仅访问授权的数据，强化数据安全性和系统响应性。
- **接口防抖**：通过限制短时间内的重复请求，防止脏数据产生，确保数据的准确性和系统稳定性。

## 系统美照

<table>
    <tr>
        <td><img alt="登录页" src="https://minioapi.szadmin.cn/public/img/login.webp"/></td>
        <td><img alt="home页" src="https://minioapi.szadmin.cn/public/img/home.webp"/></td>
    </tr>
    <tr>
        <td><img alt="账户管理" src="https://minioapi.szadmin.cn/public/img/account.webp"/></td>
        <td><img alt="角色管理" src="https://minioapi.szadmin.cn/public/img/role.webp"/></td>
    </tr>
    <tr>
        <td><img alt="菜单管理" src="https://minioapi.szadmin.cn/public/img/menu.webp"/></td>
        <td><img alt="字典管理" src="https://minioapi.szadmin.cn/public/img/dict.webp"/></td>
    </tr>
    <tr>
        <td><img alt="配置管理" src="https://minioapi.szadmin.cn/public/img/config.webp"/></td>
        <td><img alt="客户端管理" src="https://minioapi.szadmin.cn/public/img/client.webp"/></td>
    </tr>    
    <tr>
        <td><img alt="部门管理" src="https://minioapi.szadmin.cn/public/img/dept.webp"/></td>
        <td><img alt="代码预览" src="https://minioapi.szadmin.cn/public/img/gen-preview.webp"/></td>
    </tr>
    <tr>
        <td><img alt="代码生成配置1" src="https://minioapi.szadmin.cn/public/img/gen-editor.webp"/></td>
        <td><img alt="代码生成配置2" src="https://minioapi.szadmin.cn/public/img/gen-editor2.webp"/></td>
    </tr>
</table>

----

## 参与讨论

<img alt="加入群聊" src="https://minioapi.szadmin.cn/public/img/wechat.webp"/>

------

**邮箱：feiyuchuixue@163.com**
