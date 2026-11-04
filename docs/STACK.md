# 코드 구조

## 백엔드

Java 21, Kotlin 2.1.21, Spring Boot 3.5.7, Gradle 8.14.3, JPA, H2 파일 DB를 사용한다. 버전 변경은 호환성·실행 환경상 필요한 경우에만 한다.

```text
app/backend/src/main/kotlin/com/example/app/
  Application.kt
  config/                         환경·프레임워크 설정
  api/<feature>/                  Controller·Request·Response
  application/
    service/<feature>/            유즈케이스·트랜잭션·조회/저장 순서
    domain/<feature>/             업무 규칙·계산·상태 전이
    infra/<feature>/              JPA 엔티티·저장소·외부 연결
```

- Controller는 HTTP 입력·기본 형식 검증·응답을 담당한다. Service가 API Request/Response에 의존하지 않게 한다.
- Service는 데이터를 확보하고 권한·처리 순서·트랜잭션 경계를 조율한다. 의미 있는 업무 규칙은 DB 없이 확인할 수 있는 Domain 객체·함수로 분리한다.
- Domain은 직접 DB·HTTP를 호출하지 않는다. 객체 자신의 상태 전이, 값 객체의 규칙, 여러 객체에 걸친 정책과 계산을 필요에 따라 둔다.
- 복잡한 계산은 입력과 결과·실패 의미가 드러나는 함수나 객체로 두고 작은 사례로 검증한다. Service는 같은 코드를 실제 UC에서 호출한다. 알고리즘 교체를 예상한다는 이유만으로 인터페이스·엔진 계층을 미리 만들지 않는다.
- 단순 CRUD에 Domain 모델·Repository 인터페이스·DTO 변환 계층을 의무적으로 추가하지 않는다. 실제 규칙이 생길 때 파일을 만든다.
- 저장 시점의 경합은 Service의 처리 경계와 Infra/DB의 제약·잠금으로 다룬다. 트랜잭션만으로 업무상 중복이 방지된다고 가정하지 않는다.
- 테스트는 `src/test/kotlin/`에서 관련 패키지에 둔다. 업무 판단은 단위 테스트, 실제 저장·롤백·동시성은 적합한 DB 통합 검증으로 구분한다.

## 프론트엔드

React 19, TypeScript strict, Vite 7, Tailwind CSS 4를 사용한다. npm lockfile을 유지한다.

```text
app/frontend/src/
  main.tsx / App.tsx
  pages/                          페이지 구성
  features/<feature>/             해당 기능의 API·타입·컴포넌트
  shared/api/                     공통 HTTP 처리
  shared/ui/                      실제로 재사용되는 UI
  styles/                         공통 스타일
```

상태는 먼저 관련 화면 가까이에 둔다. 라우터·전역 상태·쿼리 라이브러리는 필요할 때 추가한다. 페이지는 기능을 조합하고, 공통 코드가 개별 기능을 참조하지 않게 한다. 테스트를 추가하면 관련 코드 가까이에 둔다.

## 도구와 실행

ktlint 1.5.0, ESLint, Prettier는 수동 실행 명령만 제공한다. 저장·커밋·빌드·테스트에 lint/포맷을 자동 연결하지 않는다. 실제 명령·DB 경로·초기 데이터·H2 콘솔은 [앱 README](../app/README.md)를 기준으로 한다.

## 공식 참고

- [Spring Boot: SQL DB와 H2 콘솔](https://docs.spring.io/spring-boot/3.5/reference/data/sql.html)
- [Spring Boot: DB 초기화](https://docs.spring.io/spring-boot/3.5/how-to/data-initialization.html)
- [H2 연결과 파일 DB](https://www.h2database.com/html/features.html)
- [Vite 개발 서버](https://vite.dev/config/server-options)
- [Kotlin JPA 플러그인](https://kotlinlang.org/docs/no-arg-plugin.html#jpa-support)
