# cxf-rt-bytebuddy

[![Java](https://img.shields.io/badge/Java-17-orange)] [![License](https://img.shields.io/badge/license-Apache%202.0-green)](LICENSE)

> Generate Apache CXF JAX-WS / JAX-RS endpoint implementations with
> [Byte Buddy](https://bytebuddy.net) — annotation-driven endpoint model and
> bytecode-level binding helpers.

## Table of Contents

- [1. Project Overview](#1-project-overview)
- [2. Features & Status](#2-features--status)
- [3. Requirements & Compatibility](#3-requirements--compatibility)
- [4. Architecture & Modules](#4-architecture--modules)
- [5. Installation](#5-installation)
- [6. Quick Start](#6-quick-start)
- [7. Configuration](#7-configuration)
- [8. Core Usage / API](#8-core-usage--api)
- [9. Testing & Build](#9-testing--build)
- [10. Versioning & Branches](#10-versioning--branches)
- [11. Contributing & License](#11-contributing--license)

## 1. Project Overview

`cxf-rt-bytebuddy` provides the shared endpoint model for generating Apache CXF
JAX-WS / JAX-RS service implementations at runtime with Byte Buddy:

- **Endpoint base class** — `EndpointApi` (with a pluggable
  `java.lang.reflect.InvocationHandler`) that generated endpoints extend.
- **Endpoint annotations** — `@WebEndpoint` (address, interceptors, features,
  handlers) and `@WebBound` (uid / json data binding) on types and methods.
- **REST definition model** — `RestBound`, `RestMethod` (name, HTTP method, path,
  produces), `RestParam`, `RestProduce`, `HttpMethodEnum`, `HttpParamEnum`, the
  building blocks used to describe generated REST resources.

The definition classes in this module are shared with the companion project
`cxf-rt-javassist`, which provides the concrete runtime class builders
(`JaxwsEndpointApiCtClassBuilder` / `JaxrsEndpointApiCtClassBuilder`).

What it is **not**:

- Not the CXF runtime itself — Apache CXF frontend artifacts are required at
  runtime to serve the generated endpoints.
- Not a Spring Boot starter — no auto-configuration is provided.

Typical scenarios:

| Scenario | What you use |
| :--- | :--- |
| Describe a generated JAX-WS endpoint | `@WebEndpoint(addr = ...)` + `EndpointApi` subclass |
| Describe a generated JAX-RS method | `RestMethod` + `RestParam` + `RestBound` |
| Bind data (uid / json) to an endpoint method | `@WebBound(uid = ..., json = ...)` / `RestBound` |
| Dispatch generated method calls to a handler | `EndpointApi(InvocationHandler)` |

## 2. Features & Status

| Capability | Status | Notes |
| :--- | :--- | :--- |
| `EndpointApi` base class | Active development | Holds an optional `InvocationHandler` for method dispatch |
| `@WebEndpoint` annotation | Active development | `addr`, `inInterceptors`, `outInterceptors`, `inFaults`, `outFaults`, `features`, `handlers` |
| `@WebBound` annotation | Active development | `uid` / `json` data binding on types and methods |
| REST definition model | Active development | `RestMethod`, `RestParam`, `RestProduce`, `RestBound`, `HttpMethodEnum`, `HttpParamEnum` |

> **Assumption**: the capability statuses above reflect the current state of the
> 1.0.x branch; the module is under active development (no tests are checked in).

## 3. Requirements & Compatibility

| Requirement | Version / Notes |
| :--- | :--- |
| JDK | 17+ |
| Maven | 3.0+ (enforced; Maven Wrapper `./mvnw` included) |
| Apache CXF | `cxf-rt-frontend-jaxws` / `cxf-rt-frontend-jaxrs` (managed by this pom) |
| Byte Buddy | `byte-buddy` (managed by this pom) |

Version lines:

| Branch | JDK | Version |
| :--- | :--- | :--- |
| `feature/1.0.x` | 8 | `1.0.x.*` |
| `feature/2.0.x` | 17 | `2.0.x.*` |
| `feature/3.0.x` | 21 | `3.0.x.*` |

## 4. Architecture & Modules

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

Single-module Maven project (`packaging: jar`). No child modules.

| Artifact | Responsibility |
| :--- | :--- |
| `io.github.easy4j:cxf-rt-bytebuddy` | Endpoint base class, endpoint annotations, JAX-RS definition model |

Key packages:

| Package | Content |
| :--- | :--- |
| `org.apache.cxf.endpoint` | `EndpointApi` |
| `org.apache.cxf.endpoint.annotation` | `WebEndpoint`, `WebBound` |
| `org.apache.cxf.endpoint.jaxrs.definition` | `RestBound`, `RestMethod`, `RestParam`, `RestProduce`, `HttpMethodEnum`, `HttpParamEnum` |

## 5. Installation

The project is **not yet published to Maven Central**. Snapshots/releases are
distributed through the Aliyun Maven repository and GitHub Releases.

Maven:

```xml
<dependency>
    <groupId>io.github.easy4j</groupId>
    <artifactId>cxf-rt-bytebuddy</artifactId>
    <version>2.0.x.x.20260630-SNAPSHOT</version>
</dependency>
```

Gradle:

```groovy
implementation 'io.github.easy4j:cxf-rt-bytebuddy:2.0.x.x.20260630-SNAPSHOT'
```

## 6. Quick Start

Model a generated endpoint with the annotations:

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

Expected result: `SampleEndpoint` is a valid `EndpointApi` subclass carrying the
endpoint address and binding metadata that a Byte Buddy based class generator can
turn into a CXF service implementation.

## 7. Configuration

The library has no configuration file or property prefix. All behaviour is driven
by annotations and definition objects:

| Element | Attributes / fields | Description |
| :--- | :--- | :--- |
| `@WebEndpoint` | `addr`, `inInterceptors`, `outInterceptors`, `inFaults`, `outFaults`, `features`, `handlers` | Declares the endpoint address and CXF wiring |
| `@WebBound` | `uid`, `json` | Declares bound data (primary key / JSON payload) for a type or method |
| `RestMethod` | `name`, `method` (`HttpMethodEnum`), `path`, produces | Describes a REST method |
| `RestParam` | type + `HttpParamEnum` (path / query / ...) | Describes a REST parameter |
| `RestProduce` | media types | Describes the produced media type |

## 8. Core Usage / API

### 8.1 Endpoint binding at the bytecode level

`RestBound` is the runtime counterpart of `@WebBound` — it carries the uid and the
JSON payload used to bind method data during generation:

```java
RestBound bound = new RestBound("user-123", "{\"name\":\"Alice\"}");
bound.getUid();   // "user-123"
bound.getJson();  // JSON payload as string
```

### 8.2 REST method definition

```java
RestMethod method = new RestMethod(HttpMethodEnum.GET, "sayHello", "{id}/info");
// HTTP verb, method name and URI path describe the generated JAX-RS resource method
```

## 9. Testing & Build

```bash
./mvnw clean verify
```

- The build is configured with the JaCoCo Maven plugin (report + `check` goal with a
  90% line-coverage rule bound to the `verify` phase; `haltOnFailure=false`).
- **Assumption**: the 1.0.x branch currently checks in no test sources under
  `src/test`; coverage thresholds are therefore enforced only when tests exist.
- No CI workflow files are present under `.github/` in this worktree.

## 10. Versioning & Branches

| Branch | JDK | Version | Notes |
| :--- | :--- | :--- | :--- |
| `feature/1.0.x` | 8 | `1.0.x.*` | Current branch, JDK 8 baseline, active development |
| `feature/2.0.x` | 17 | `2.0.x.*` | JDK 17 line |
| `feature/3.0.x` | 21 | `3.0.x.*` | JDK 21 line |

Maintenance policy: the `1.0.x` line receives bug fixes and compatibility updates
for the JDK 8 baseline. New features targeting newer JDKs land on the `2.0.x` /
`3.0.x` lines. Releases are published to the Aliyun Maven repository and as
GitHub Releases; the project is not yet published to Maven Central.

## 11. Contributing & License

Contributions are welcome — please open issues or pull requests on GitHub.

Licensed under the [Apache License, Version 2.0](LICENSE).
