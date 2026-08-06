# cxf-rt-bytebuddy

[![Java](https://img.shields.io/badge/Java-8-orange)] [![License](https://img.shields.io/badge/license-Apache%202.0-green)](LICENSE)

> 使用 [Byte Buddy](https://bytebuddy.net) 生成基于 Apache CXF 的 JAX-WS / JAX-RS
> 端点实现——注解驱动的端点模型与字节码级绑定辅助类。

## 目录

- [1. 项目概述](#1-项目概述)
- [2. 功能与状态](#2-功能与状态)
- [3. 环境要求与兼容性](#3-环境要求与兼容性)
- [4. 架构与模块](#4-架构与模块)
- [5. 安装](#5-安装)
- [6. 快速开始](#6-快速开始)
- [7. 配置](#7-配置)
- [8. 核心用法 / API](#8-核心用法--api)
- [9. 测试与构建](#9-测试与构建)
- [10. 版本与分支](#10-版本与分支)
- [11. 贡献与许可](#11-贡献与许可)

[English](./README.md) | [简体中文](./README.zh-CN.md)

## 1. 项目概述

`cxf-rt-bytebuddy` 提供使用 Byte Buddy 在运行时生成 Apache CXF JAX-WS / JAX-RS
服务实现的共享端点模型：

- **端点基类** — `EndpointApi`（可插拔 `java.lang.reflect.InvocationHandler`），
  生成的端点继承该类。
- **端点注解** — 类型与方法上的 `@WebEndpoint`（地址、拦截器、feature、handler）
  与 `@WebBound`（uid / json 数据绑定）。
- **REST 定义模型** — `RestBound`、`RestMethod`（方法名、HTTP 方法、路径、
  produces）、`RestParam`、`RestProduce`、`HttpMethodEnum`、`HttpParamEnum`，
  用于描述生成的 REST 资源。

本模块中的定义类与姊妹项目 `cxf-rt-javassist` 共享，后者提供具体的运行时类构建器
（`JaxwsEndpointApiCtClassBuilder` / `JaxrsEndpointApiCtClassBuilder`）。

它不是：

- CXF 运行时本身——提供服务生成的端点仍需要 Apache CXF frontend 构件。
- Spring Boot starter——不提供自动装配。

典型场景：

| 场景 | 使用内容 |
| :--- | :--- |
| 描述生成的 JAX-WS 端点 | `@WebEndpoint(addr = ...)` + `EndpointApi` 子类 |
| 描述生成的 JAX-RS 方法 | `RestMethod` + `RestParam` + `RestBound` |
| 给端点方法绑定数据（uid / json） | `@WebBound(uid = ..., json = ...)` / `RestBound` |
| 把生成的方法调用分发给处理器 | `EndpointApi(InvocationHandler)` |

## 2. 功能与状态

| 能力 | 状态 | 说明 |
| :--- | :--- | :--- |
| `EndpointApi` 基类 | 活跃开发 | 持有可选的 `InvocationHandler` 用于方法分发 |
| `@WebEndpoint` 注解 | 活跃开发 | `addr`、`inInterceptors`、`outInterceptors`、`inFaults`、`outFaults`、`features`、`handlers` |
| `@WebBound` 注解 | 活跃开发 | 类型与方法上的 `uid` / `json` 数据绑定 |
| REST 定义模型 | 活跃开发 | `RestMethod`、`RestParam`、`RestProduce`、`RestBound`、`HttpMethodEnum`、`HttpParamEnum` |

> **假设**：以上能力状态反映 1.0.x 分支当前情况；该模块处于活跃开发中
> （未提交测试）。

## 3. 环境要求与兼容性

| 要求 | 版本 / 说明 |
| :--- | :--- |
| JDK | 8+ |
| Maven | 3.0+（enforcer 强制；项目内置 Maven Wrapper `./mvnw`） |
| Apache CXF | `cxf-rt-frontend-jaxws` / `cxf-rt-frontend-jaxrs`（由本 pom 管理） |
| Byte Buddy | `byte-buddy`（由本 pom 管理） |

版本线：

| 分支 | JDK | 版本 |
| :--- | :--- | :--- |
| `feature/1.0.x` | 8 | `1.0.x.*` |
| `feature/2.0.x` | 17 | `2.0.x.*` |
| `feature/3.0.x` | 21 | `3.0.x.*` |

## 4. 架构与模块

```text
+------------------+   +------------------------------------------+
| Developer        |   | cxf-rt-bytebuddy                         |
|                  |-->|  annotations: @WebEndpoint, @WebBound    |
| endpoint spec    |   |  base class : EndpointApi                |
| (annotations /   |   |              (+InvocationHandler)        |
|  definition      |   |  jaxrs defs: RestMethod, RestParam,     |
|  objects)        |   |              RestProduce, RestBound,    |
+------------------+   |              HttpMethodEnum, HttpParam  |
                       +-------------------+----------------------+
                                           |
                                           v
                     +-------------------------------------------+
                     | Byte Buddy generates CXF JAX-WS/JAX-RS   |
                     | implementation classes at runtime        |
                     +-------------------------------------------+
```

单模块 Maven 工程（`packaging: jar`），无子模块。

| 构件 | 职责 |
| :--- | :--- |
| `io.github.easy4j:cxf-rt-bytebuddy` | 端点基类、端点注解、JAX-RS 定义模型 |

关键包：

| 包 | 内容 |
| :--- | :--- |
| `org.apache.cxf.endpoint` | `EndpointApi` |
| `org.apache.cxf.endpoint.annotation` | `WebEndpoint`、`WebBound` |
| `org.apache.cxf.endpoint.jaxrs.definition` | `RestBound`、`RestMethod`、`RestParam`、`RestProduce`、`HttpMethodEnum`、`HttpParamEnum` |

## 5. 安装

项目**尚未发布到 Maven Central**。快照 / 发布版本通过阿里云 Maven 仓库与 GitHub
Releases 分发。

Maven：

```xml
<dependency>
    <groupId>io.github.easy4j</groupId>
    <artifactId>cxf-rt-bytebuddy</artifactId>
    <version>1.0.x.20260630-SNAPSHOT</version>
</dependency>
```

Gradle：

```groovy
implementation 'io.github.easy4j:cxf-rt-bytebuddy:1.0.x.20260630-SNAPSHOT'
```

## 6. 快速开始

用注解建模一个生成的端点：

```java
import org.apache.cxf.endpoint.EndpointApi;
import org.apache.cxf.endpoint.annotation.WebEndpoint;
import org.apache.cxf.endpoint.annotation.WebBound;

@WebEndpoint(addr = "http://localhost:8080/services/sample")
@WebBound(uid = "sample-001")
public class SampleEndpoint extends EndpointApi {

    public String sayHello(String name) {
        return "Hello, " + name;
    }
}
```

预期结果：`SampleEndpoint` 是携带端点地址与绑定元数据的合法 `EndpointApi` 子类，
基于 Byte Buddy 的类生成器可以把它转换为 CXF 服务实现。

## 7. 配置

本库没有配置文件或属性前缀。所有行为由注解与定义对象驱动：

| 元素 | 属性 / 字段 | 说明 |
| :--- | :--- | :--- |
| `@WebEndpoint` | `addr`、`inInterceptors`、`outInterceptors`、`inFaults`、`outFaults`、`features`、`handlers` | 声明端点地址与 CXF 装配 |
| `@WebBound` | `uid`、`json` | 为类型或方法声明绑定数据（主键 / JSON 载荷） |
| `RestMethod` | `name`、`method`（`HttpMethodEnum`）、`path`、produces | 描述一个 REST 方法 |
| `RestParam` | 类型 + `HttpParamEnum`（path / query 等） | 描述一个 REST 参数 |
| `RestProduce` | 媒体类型 | 描述产出的媒体类型 |

## 8. 核心用法 / API

### 8.1 字节码级端点绑定

`RestBound` 是 `@WebBound` 的运行时对应物——携带生成时用于方法数据绑定的 uid 与
JSON 载荷：

```java
RestBound bound = new RestBound("user-123", "{\"name\":\"Alice\"}");
bound.getUid();   // "user-123"
bound.getJson();  // JSON 载荷字符串
```

### 8.2 REST 方法定义

```java
RestMethod method = new RestMethod(HttpMethodEnum.GET, "sayHello", "{id}/info");
// HTTP 动词、方法名与 URI 路径描述生成的 JAX-RS 资源方法
```

## 9. 测试与构建

```bash
./mvnw clean verify
```

- 构建配置了 JaCoCo Maven 插件（报告 + 绑定在 `verify` 阶段的 `check` 目标，
  行覆盖率规则为 90%；`haltOnFailure=false`）。
- **假设**：1.0.x 分支当前 `src/test` 下未提交测试源码；覆盖率门禁仅在存在测试时生效。
- 本 worktree 的 `.github/` 下无 CI 工作流文件。

## 10. 版本与分支

| 分支 | JDK | 版本 | 说明 |
| :--- | :--- | :--- | :--- |
| `feature/1.0.x` | 8 | `1.0.x.*` | 当前分支，JDK 8 基线，活跃开发 |
| `feature/2.0.x` | 17 | `2.0.x.*` | JDK 17 版本线 |
| `feature/3.0.x` | 21 | `3.0.x.*` | JDK 21 版本线 |

维护策略：`1.0.x` 版本线接收针对 JDK 8 基线的缺陷修复与兼容性更新；面向新 JDK 的
新特性在 `2.0.x` / `3.0.x` 版本线开发。发布物通过阿里云 Maven 仓库与 GitHub
Releases 分发；项目尚未发布到 Maven Central。

## 11. 贡献与许可

欢迎通过 GitHub Issue 或 Pull Request 参与贡献。

本项目基于 [Apache License, Version 2.0](LICENSE) 许可。
