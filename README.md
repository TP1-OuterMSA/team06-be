# 🔐 Team06 Auth Service - Backend

> **team6-auth-service**  
> 사용자 인증 및 학식 정보 관리 백엔드 서비스

Team06 Auth Service는 JWT 기반 사용자 인증/인가와 Kafka를 활용한 학식(급식) 데이터 실시간 수집·제공을 담당하는 Spring Boot 백엔드 서비스입니다.

---

## 📌 프로젝트 소개

본 서비스는 크게 두 가지 핵심 도메인으로 구성됩니다.

**인증(Auth)** — 회원가입, 로그인, JWT Access/Refresh Token 발급 및 갱신을 처리합니다. Refresh Token은 HttpOnly 쿠키로 관리하여 보안을 강화했습니다.

**학식(Meal)** — Kafka Consumer를 통해 웹 크롤러가 수집한 학식 데이터를 실시간으로 수신하고, 요일/식사 유형별로 저장·조회할 수 있는 API를 제공합니다.

---

## 🛠 기술 스택

| 분류 | 기술 |
|------|------|
| **Language** | Java 17 |
| **Framework** | Spring Boot, Spring Security |
| **Authentication** | JWT (Access Token + Refresh Token), BCrypt |
| **Database** | JPA / Hibernate |
| **Messaging** | Apache Kafka (Consumer) |
| **Build** | Gradle |
| **Container** | Docker (Multi-stage Build) |
| **Infra** | AWS EC2, Amazon ECR |
| **CI/CD** | GitHub Actions |

---

## 🏗 CI/CD 파이프라인

### CI — Build & Push to ECR

PR이 `main` 브랜치에 머지되면 GitHub Actions가 자동으로 Docker 이미지를 빌드하고 Amazon ECR에 푸시합니다.

### CD — Deploy to EC2

CI 워크플로우가 성공적으로 완료되면 CD가 트리거되어 EC2에 SSH로 접속한 뒤, ECR에서 이미지를 pull하고 `docker compose up`으로 자동 배포합니다.

```
Developer → GitHub (PR merge to main)
    → GitHub Actions CI (Docker build → ECR push)
    → GitHub Actions CD (SSH → EC2 → docker compose up)
```

### Dockerfile

Docker 멀티스테이지 빌드를 사용하여 이미지 크기를 최적화했습니다. Amazon Corretto 17 기반으로 빌드 및 실행 단계를 분리합니다.

---

## 📂 프로젝트 구조

```
src/main/java/com/example/teamproject/
├── domain/
│   ├── auth/                      # 인증/인가 도메인
│   │   ├── controller/
│   │   │   └── AuthController     # 회원가입, 로그인, 토큰 갱신 API
│   │   ├── dto/
│   │   │   ├── request/           # LoginRequest, SignupRequest, RefreshRequest
│   │   │   └── response/          # JwtResponseDto, TokenResponse, UserDto
│   │   ├── entity/                # User 엔티티
│   │   ├── repository/            # UserRepository
│   │   ├── security/
│   │   │   ├── SecurityConfig     # Spring Security 설정
│   │   │   ├── JwtTokenProvider   # JWT 생성/검증
│   │   │   └── CustomUserDetails  # UserDetails 구현체
│   │   └── service/
│   │       └── AuthService        # 인증 비즈니스 로직
│   ├── meal/                      # 학식 메뉴 도메인
│   │   ├── controller/
│   │   ├── dto/                   # MealResponse
│   │   ├── entity/                # Meal (id, name, category)
│   │   ├── repository/
│   │   └── service/               # MealService
│   ├── mealSchedule/              # 학식 스케줄 도메인
│   │   ├── controller/
│   │   │   └── MealScheduleController  # 주간/일간 학식 조회 API
│   │   ├── dto/                   # MealScheduleResponse
│   │   ├── entity/                # MealSchedule (day, mealType)
│   │   ├── repository/
│   │   └── service/               # MealScheduleService
│   └── mealScheduleItem/          # 학식 스케줄 ↔ 메뉴 매핑
│       ├── entity/                # MealScheduleItem (orderNum)
│       └── repository/
├── kafka/
│   └── MealKafkaListener          # Kafka Consumer (학식 데이터 수신)
└── TeamprojectApplication.java
```

---

## 🔗 주요 API

### 🔐 Auth API

| Method | Endpoint | 설명 |
|--------|----------|------|
| `POST` | `/api/team06-auth-service/auth/user/signup` | 회원가입 |
| `POST` | `/api/team06-auth-service/auth/user/login` | 로그인 (Access Token 반환 + Refresh Token 쿠키 설정) |
| `POST` | `/api/team06-auth-service/auth/user/refresh` | Refresh Token으로 Access Token 재발급 |
| `POST` | `/api/team06-auth-service/auth/user/refresh/validate` | Refresh Token 유효성 검증 |

### 🍽 Meal Schedule API

| Method | Endpoint | 설명 |
|--------|----------|------|
| `GET` | `/api/team6/meal/schedule/week` | 주간 학식 조회 (월~금) |
| `GET` | `/api/team6/meal/schedule/day?day={요일}` | 특정 요일 학식 조회 |
| `POST` | `/api/team6/meal/schedule/update` | 학식 스케줄 업데이트 |

---

## 📡 Kafka 이벤트

| Topic | Group ID | 설명 |
|-------|----------|------|
| `meal.web.crawler.updated` | `team06-service` | 웹 크롤러가 수집한 학식 데이터 수신 → MealSchedule 업데이트 |
| `meal.category.updated` | `team06-service` | 메뉴 카테고리 정보 수신 → Meal 엔티티 저장 |

---

## 🔒 인증 흐름

```
1. 회원가입 → POST /signup → 비밀번호 BCrypt 암호화 후 저장

2. 로그인 → POST /login
   → AuthenticationManager 인증
   → Access Token (응답 Body) + Refresh Token (HttpOnly Cookie) 발급

3. 토큰 갱신 → POST /refresh
   → 쿠키에서 Refresh Token 추출
   → 유효성 검증 후 새 Access Token 발급

4. API 요청 → Authorization: Bearer {accessToken}
   → Spring Security 필터에서 JWT 검증
```

---

## ⚙️ 실행 방법

### 사전 요구사항

- Java 17 (Amazon Corretto 권장)
- Gradle
- Docker & Docker Compose
- Kafka (외부 또는 Docker)

### 로컬 실행

```bash
# 1. 레포지토리 클론
git clone https://github.com/your-org/team06-auth-service.git
cd team06-auth-service

# 2. 환경 설정 (application.yml)
# jwt.secret, jwt.access-token-expiration-ms, jwt.refresh-token-expiration-ms 등 설정

# 3. 빌드 및 실행
./gradlew clean build -x test
java -jar build/libs/*.jar
```

### Docker 빌드 & 실행

```bash
# 이미지 빌드
docker build -t team06-auth-service .

# 컨테이너 실행
docker run -p 8080:8080 team06-auth-service
```

---
