# Zentrald

Spring Boot 기반 인증 시스템 프로젝트.
로그인·아이디 찾기·비밀번호 초기화 기능과 한/영 다국어, 설정 기반 비밀번호 인코딩을 제공합니다.

---

## 기술 스택

| 분류 | 기술 |
|---|---|
| Language | Java 17 |
| Framework | Spring Boot 3.3.0 |
| Security | Spring Security 6 |
| Template | Thymeleaf + thymeleaf-extras-springsecurity6 |
| ORM | Spring Data JPA (Hibernate) |
| Database | MySQL 8.x |
| Build | Gradle |

---

## 프로젝트 구조

```
zentrald/
├── SQL/
│   ├── schema.sql                  # 테이블 DDL (MySQL)
│   └── data.sql                    # 초기 테스트 데이터
│
├── src/main/
│   ├── java/com/zentrald/
│   │   ├── ZentraldApplication.java
│   │   ├── config/
│   │   │   ├── DataInitializer.java        # 기동 시 admin 계정 자동 생성
│   │   │   ├── PasswordEncoderConfig.java  # crypto-config.xml 읽어 PasswordEncoder 빈 생성
│   │   │   ├── SecurityConfig.java         # Spring Security 설정 + 보안 헤더
│   │   │   └── WebConfig.java              # 다국어(i18n) LocaleResolver 설정
│   │   ├── controller/
│   │   │   ├── AccountController.java      # 아이디 찾기 / 비밀번호 초기화 처리
│   │   │   ├── AuthController.java         # 로그인 / 로그아웃 처리
│   │   │   └── ViewController.java         # 화면 GET 라우팅
│   │   ├── crypto/
│   │   │   ├── CryptoConfigLoader.java     # XML 파싱 및 유효성 검사
│   │   │   ├── CryptoSettings.java         # 해시알고리즘 + 인코딩 설정값 보관
│   │   │   └── CustomPasswordEncoder.java  # PasswordEncoder 구현체
│   │   ├── dto/
│   │   │   └── LoginDto.java
│   │   ├── entity/
│   │   │   └── User.java                   # JPA 엔티티
│   │   ├── model/
│   │   │   └── UserInfo.java               # (deprecated, UserStore 대체용)
│   │   ├── repository/
│   │   │   ├── UserRepository.java         # Spring Data JPA 레포지토리
│   │   │   └── UserStore.java              # (deprecated, UserRepository로 대체됨)
│   │   └── service/
│   │       ├── AccountService.java         # 아이디 찾기 / 비밀번호 초기화 비즈니스 로직
│   │       └── CustomUserDetailsService.java  # Spring Security UserDetailsService 구현
│   │
│   └── resources/
│       ├── config/
│       │   └── crypto-config.xml           # 비밀번호 인코딩 설정 (핵심 외부 설정)
│       ├── templates/
│       │   ├── login.html                  # 로그인 화면 (진입점: /)
│       │   ├── home.html                   # 대시보드 (로그인 후)
│       │   ├── find-username.html          # 아이디 찾기 화면
│       │   └── reset-password.html         # 비밀번호 초기화 화면
│       ├── application.properties
│       ├── messages.properties             # 한국어 메시지 (기본)
│       └── messages_en.properties          # 영어 메시지
│
├── build.gradle
└── settings.gradle
```

---

## 실행 방법

### 사전 요구사항
- Java 17+
- MySQL 8.x 실행 중

### 1. 데이터베이스 준비

```sql
-- schema.sql 실행 (최초 1회)
source SQL/schema.sql;
```

> **개발 환경**: `DataInitializer`가 기동 시 `admin` 계정을 자동 생성하므로
> `data.sql`을 별도로 실행할 필요가 없습니다.

### 2. DB 접속 정보 설정

`src/main/resources/application.properties`

```properties
spring.datasource.url=jdbc:mysql://localhost:3306/zentrald?useSSL=false&serverTimezone=Asia/Seoul&characterEncoding=UTF-8
spring.datasource.username=your_username
spring.datasource.password=your_password
```

### 3. 빌드 및 실행

```bash
./gradlew bootRun
```

접속: `http://localhost:9091`

---

## 기본 테스트 계정

| 항목 | 값 |
|---|---|
| 아이디 | `admin` |
| 비밀번호 | `1234` |
| 이름 | `관리자` |
| 이메일 | `admin@example.com` |

> `DataInitializer`가 기동마다 현재 `crypto-config.xml` 설정으로 비밀번호를 재인코딩합니다.

---

## 비밀번호 인코딩 설정

`src/main/resources/config/crypto-config.xml`

```xml
<crypto-config>
    <password-encoding>
        <hash-algorithm>SHA-512</hash-algorithm>  <!-- SHA-256 | SHA-512 -->
        <encoding>HEX</encoding>                   <!-- HEX    | BASE64  -->
    </password-encoding>
</crypto-config>
```

### 지원 조합

| hash-algorithm | encoding | 저장 길이 |
|---|---|---|
| SHA-512 | HEX | 128자 |
| SHA-512 | BASE64 | 88자 |
| SHA-256 | HEX | 64자 |
| SHA-256 | BASE64 | 44자 |

### 외부 파일로 분리하려면

```properties
# application.properties
crypto.config.path=file:/etc/zentrald/crypto-config.xml
```

> **보안 주의**: SHA-256/512는 빠른 단방향 해시로 솔트가 없습니다.
> 운영 환경에서는 BCrypt / Argon2 사용을 권장합니다.

---

## 다국어 (i18n)

- 기본 언어: **한국어**
- 지원 언어: 한국어 / English
- 전환 방법: 각 화면 우상단 언어 버튼 클릭 또는 URL에 `?lang=ko` / `?lang=en` 추가
- 설정 파일: `messages.properties` (한국어), `messages_en.properties` (영어)
- 저장 방식: 쿠키(`lang`) — 페이지 이동 후에도 유지

---

## 화면 구성

| URL | 화면 | 인증 필요 |
|---|---|---|
| `/` | 로그인 (미인증 시) / 대시보드 리다이렉트 (인증 시) | X |
| `/login` | 로그인 (에러·로그아웃 메시지 지원) | X |
| `/home` | 대시보드 (세션 정보 표시) | O |
| `/account/find-username` | 아이디 찾기 (이름 + 이메일 입력) | X |
| `/account/reset-password` | 비밀번호 초기화 (아이디 + 새 비밀번호 입력) | X |
| `/auth/login` | 로그인 POST 처리 | X |
| `/auth/logout` | 로그아웃 POST 처리 | X |

---

## 보안 구성

### HTTP 보안 헤더 (SecurityConfig)

| 헤더 | 설정 | 방지 효과 |
|---|---|---|
| `X-Frame-Options` | `DENY` | 클릭재킹 |
| `X-Content-Type-Options` | `nosniff` | MIME 스니핑 |
| `X-XSS-Protection` | `1; mode=block` | 반사형 XSS |
| `Content-Security-Policy` | `default-src 'self'` 외 | XSS, 외부 리소스 주입 |
| `Referrer-Policy` | `strict-origin-when-cross-origin` | URL 정보 유출 |

### 화면 위변조 방지 (각 템플릿 공통)

- **CSRF**: `th:action` 이 폼에 `_csrf` hidden 필드를 자동 삽입, 불일치 시 403 반환
- **CSRF 메타태그**: JavaScript AJAX 요청용 토큰 노출 (`<meta name="_csrf">`)
- **IE 호환성 차단**: `X-UA-Compatible: IE=edge`
- **Referrer 메타**: `strict-origin-when-cross-origin`
- **autocomplete 속성**: 브라우저 자동완성을 의미에 맞게 제어

---

## 향후 개선 사항

- [ ] 이메일 인증 기반 아이디 찾기 / 비밀번호 초기화
- [ ] 회원가입 기능
- [ ] 역할(Role) 관리 — ROLE_ADMIN / ROLE_USER 분리
- [ ] HTTPS 적용 후 HSTS 헤더 활성화 (`SecurityConfig` 주석 해제)
- [ ] 비밀번호 인코딩을 BCrypt / Argon2로 교체
- [ ] 외부 CSS 파일 분리 → CSP `unsafe-inline` 제거 가능
