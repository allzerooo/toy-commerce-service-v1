# Commerce Service

모노레포 형태로 구성된 Kotlin + Spring Boot 기반 커머스 마이크로서비스 세트이다. 각 서비스는 Gradle 멀티모듈로 묶여 있으며 공통 빌드 설정B을 공유한다.

## 모듈 구성
- `service-discovery`: 서비스 디스커버리를 담당하는 Spring Cloud Netflix Eureka 서버.
- `api-gateway`: 외부 유입 트래픽을 라우팅하고 cross-cutting concern(인증, 로깅 등)을 처리하는 Spring Cloud Gateway.
- `user-service`: 사용자 도메인과 관련된 REST API를 제공하는 핵심 비즈니스 서비스.

## 개발 환경
- Java 21
- Kotlin 1.9.25
- Spring Boot 3.5.x
- Gradle Wrapper(`./gradlew`) 사용 권장

## 기본 사용법
```bash
# 전체 모듈 빌드 및 테스트
./gradlew clean build

# 개별 서비스 실행 (예: user-service)
./gradlew :user-service:bootRun
```

## 프로젝트 구조
```
.
├── api-gateway
├── service-discovery
├── user-service
└── build.gradle (공통 설정)
```

## 추가
- 새로운 서비스 모듈을 추가할 경우 `settings.gradle`에 include 하고, 필요한 공통 의존성은 `build.gradle`의 `subprojects` 블록을 통해 상속받을 수 있다.
