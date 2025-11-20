# Commerce Platform

Spring Boot + Kotlin 기반 마이크로서비스 아키텍처 학습 프로젝트입니다.

## 📋 프로젝트 개요

Gradle 멀티모듈로 구성된 커머스 플랫폼으로, 서비스 디스커버리, API Gateway, 그리고 여러 비즈니스 서비스들이 포함되어 있습니다.

## 🏗️ 아키텍처

```
┌─────────────┐
│   Client    │
└──────┬──────┘
       │
       v
┌─────────────────┐
│  API Gateway    │ :8000
└────────┬────────┘
         │
    ┌────┴─────┬──────────┐
    v          v          v
┌─────────┐ ┌─────────┐ ┌─────────┐
│  User   │ │  Order  │ │Customer │
│ Service │ │ Service │ │ Service │
│  :0     │ │  :8081  │ │  :8082  │
└────┬────┘ └────┬────┘ └────┬────┘
     │           │           │
     └───────────┴───────────┘
                 │
          ┌──────v──────┐
          │   Eureka    │ :8761
          │   Server    │
          └─────────────┘
```

## 📦 모듈 구성

| 모듈 | 포트 | 설명 |
|------|------|------|
| `service-discovery` | 8761 | Eureka 서비스 레지스트리 |
| `api-gateway` | 8000 | Spring Cloud Gateway (MVC) |
| `api-gateway-reactive` | - | WebFlux 기반 게이트웨이 (개발 중) |
| `user-service` | 동적 | 사용자 관리 서비스 |
| `order-service` | 8081 | 주문 관리 서비스 |
| `customer-service` | 8082 | 고객 관리 서비스 |

## 🛠️ 기술 스택

- **Language**: Kotlin 1.9.25
- **Framework**: Spring Boot 3.5.7
- **Build Tool**: Gradle 8.x
- **Java Version**: 21
- **Service Discovery**: Netflix Eureka
- **API Gateway**: Spring Cloud Gateway

## 🚀 시작하기

### 필수 요구사항

- JDK 21 이상
- Gradle (또는 내장된 Gradle Wrapper 사용)

### 전체 빌드

```bash
./gradlew clean build
```

### 서비스 실행 순서

1. **Eureka Server 시작**
```bash
./gradlew :service-discovery:bootRun
```
→ http://localhost:8761 에서 대시보드 확인

2. **비즈니스 서비스 시작**
```bash
# 별도 터미널에서 각각 실행
./gradlew :user-service:bootRun
./gradlew :order-service:bootRun
./gradlew :customer-service:bootRun
```

3. **API Gateway 시작**
```bash
./gradlew :api-gateway:bootRun
```

### 동작 확인

```bash
# Gateway를 통한 Order Service 호출
curl http://localhost:8000/orders/welcome

# Gateway를 통한 Customer Service 호출
curl http://localhost:8000/customers/welcome
```

## 📁 프로젝트 구조

```
commerce-platform/
├── build.gradle                 # Root 빌드 설정
├── settings.gradle             # 모듈 구성
├── service-discovery/          # Eureka Server
│   ├── build.gradle
│   └── src/
├── api-gateway/                # API Gateway (MVC)
│   ├── build.gradle
│   └── src/
├── api-gateway-reactive/       # API Gateway (WebFlux)
│   ├── build.gradle
│   └── src/
├── user-service/               # User 도메인
│   ├── build.gradle
│   └── src/
├── order-service/              # Order 도메인
│   ├── build.gradle
│   └── src/
└── customer-service/           # Customer 도메인
    ├── build.gradle
    └── src/
```

## 🔧 개발 가이드

### 새 모듈 추가하기

1. **디렉토리 생성**
```bash
mkdir new-service
```

2. **settings.gradle에 등록**
```gradle
include 'new-service'
```

3. **build.gradle 작성**
```gradle
version = '1.0.0'
description = 'New Service'

dependencies {
    implementation 'org.springframework.boot:spring-boot-starter-web'
    implementation 'org.springframework.cloud:spring-cloud-starter-netflix-eureka-client'
}
```

### 공통 의존성

Root의 `subprojects` 블록에서 모든 모듈이 공유하는 의존성을 관리합니다:
- Kotlin reflect
- Jackson Kotlin Module
- kotlin-logging
- Spring Boot Test

## 📚 참고 자료

- [Spring Cloud Documentation](https://spring.io/projects/spring-cloud)
- [Kotlin with Spring Boot](https://spring.io/guides/tutorials/spring-boot-kotlin)
- [Gradle Multi-Project Builds](https://docs.gradle.org/current/userguide/multi_project_builds.html)

## 📄 라이선스

MIT License
