# 🏗️ 계층별 역할 및 책임 가이드 (Architecture Guidelines)

이 가이드는 **Controller-Service-Repository 패턴**을 기반으로 하되, **DDD(도메인 주도 설계)**의 계층 구조를 적용하여 **관심사의 분리**와 **도메인 중심 설계**를 지향합니다.

### 📋 한눈에 보는 패키지 구조도

| 계층 (Layer) | 패키지 경로 (Package) | 주요 구성 요소 (Components)                      |
| :--- | :--- |:-------------------------------------------|
| **Presentation** | `presentation/` | `controller/`, `dto/`                      |
| **Application** | `application/` | `service/`, `usecase/`                     |
| **Domain** | `domain/` | `model/entity/`, `repository/`, `service/` |
| **Infrastructure** | `infrastructure/` | `repository/`, `client/`                   |

---

## 1. 🌐 Presentation Layer (표현 계층)
> **패키지 위치:** `presentation/`
> **역할:** 사용자의 요청을 받아 Application 계층으로 전달하고, 결과를 반환합니다.

### 📂 구성 요소
- **`controller/`**: REST API 엔드포인트 정의. 요청을 매핑하고 `Application Service`를 호출합니다.
- **`dto/`**: `Request`, `Response` 객체. 클라이언트와 데이터를 주고받는 껍데기 역할입니다.

### ✅ 책임 (Responsibility)
- **데이터 변환:** HTTP 요청을 시스템이 이해할 수 있는 형태(`DTO`)로 변환합니다.
- **입력값 검증:** `@Valid` 등을 사용하여 요청 데이터의 형식을 검증합니다.
- **단순 위임:** 비즈니스 로직을 직접 수행하지 않고 하위 계층에 위임합니다.

### 🚫 금지 (Restriction)
- **비즈니스 로직 포함 금지:** 순수한 전달자 역할만 수행해야 합니다.
- **`Entity` 노출 금지:** 클라이언트에게 도메인 `Entity`를 직접 반환하면 안 됩니다.
- **DB 직접 접근 금지:** `Repository`나 `Infrastructure`를 직접 호출하지 않습니다.

---

## 2. ⚙️ Application Layer (응용 계층)
> **패키지 위치:** `application/`
> **역할:** 도메인 객체와 인프라 자원을 조율하여 애플리케이션의 유스케이스(기능)를 실행합니다.

### 📂 구성 요소
- **`service/`**: 일반적인 비즈니스 로직의 흐름을 제어합니다. (우리가 흔히 아는 Service 클래스)
- **`usecase/`**: (선택사항) 복잡한 비즈니스 로직의 경우, 단일 기능을 별도 클래스로 분리할 때 사용합니다.

### ✅ 책임 (Responsibility)
- **흐름 제어:** 도메인 객체를 가져오고(`Repository`), 로직을 실행하고(`Domain`), 저장하는 흐름 관리
- **트랜잭션 관리:** `@Transactional`을 사용하여 데이터 일관성을 보장합니다.
- **도메인 보호:** `DTO`를 `Entity`로 변환하거나, `Entity`를 `DTO`로 변환하여 내보냅니다. (`Presentation` 계층이 `Entity`를 모르게 함)

### 🚫 금지 (Restriction)
- **비즈니스 판단 로직 금지:** "상태 변경 규칙" 등 핵심 로직은 `Domain`에 위임하고, 여기서는 **순서만 제어**합니다.
- **기술 종속성 배제:** `HttpServletRequest` 등 웹 관련 기술 코드가 섞이면 안 됩니다.
- **`SQL/Infra` 코드 금지:** 직접 쿼리를 작성하거나 외부 API를 호출하는 코드를 작성하지 않습니다.

---

## 3. 🧠 Domain Layer (도메인 계층)
> **패키지 위치:** `domain/`
> **역할:** 소프트웨어의 심장부로, 비즈니스 규칙과 핵심 로직을 담당합니다. **기술 의존성이 없어야 합니다.**

### 📂 구성 요소
- **`model/entity/`**: 핵심 비즈니스 객체 (`Entity`, `VO`). 비즈니스 로직을 메서드로 가집니다.
- **`repository/`**: **(중요)** 데이터를 저장하고 불러오는 **인터페이스(Interface)**만 정의합니다.
- **`service/`**: 엔티티만으로 처리하기 힘든 도메인 로직을 정의합니다. (Domain Service)

### ✅ 책임 (Responsibility)
- **핵심 규칙 구현:** "주문 취소 시 상태 변경", "비밀번호 암호화 규칙" 등 핵심 로직을 수행합니다.
- **순수 자바 코드:** Spring이나 JPA 같은 프레임워크 기술에 의존하지 않는 POJO 상태를 유지합니다.

### 🚫 금지 (Restriction)
- **기술 의존성 제로(0):** Spring, JPA, Hibernate 등 프레임워크나 인프라 기술에 의존하지 않는 POJO 상태 유지.
- **구현체 포함 금지:** `Repository`의 실제 구현 코드(JPA 등)가 포함되면 안 됩니다. (오직 인터페이스만 존재)

---

## 4. 🔌 Infrastructure Layer (인프라 계층)
> **패키지 위치:** `infrastructure/`
> **역할:** 도메인 계층에서 정의한 인터페이스를 실제 기술로 구현합니다.

### 📂 구성 요소
- **`repository/`**: Domain 계층의 Repository 인터페이스를 **구현(Implements)**하는 클래스 (`JPA`, `QueryDSL` 등).
- **`client/`**: 외부 API 호출, 메시지 큐 등 외부 시스템과의 통신 구현체.

### ✅ 책임 (Responsibility)
- **기술적 구현:** 실제 DB에 쿼리를 날리거나, 외부 시스템과 통신하는 코드가 위치합니다.
- **DIP(의존성 역전) 수행:** 도메인 계층은 인프라를 모르고, 인프라 계층이 도메인을 알고 의존합니다.

### 🚫 금지 (Restriction)
- **비즈니스 규칙 포함 금지:** 기술적인 동작만 담당해야 하며, 비즈니스 판단을 내려서는 안 됩니다.
- **도메인 오염 주의:** 구현체가 도메인 모델을 침범하거나 변경해서는 안 됩니다.
- **순환 참조 주의:** `Infrastructure`가 `Presentation`을 역으로 참조하면 안 됩니다.

---

### 💡 핵심 원칙: 의존성 규칙 (Dependency Rule)
모든 소스 코드의 의존성은 반드시 **외부(`Infra`, `Presentation`)에서 내부(`Domain`)** 로 향해야 합니다.

1. `Presentation` ➡️ `Application`
2. `Application` ➡️ `Domain`
3. `Infrastructure` ➡️ `Domain` (인터페이스 구현을 통해)

**절대로 `Domain` 계층이 `Infrastructure`나 `Presentation`을 의존(import)해서는 안 됩니다.**