# Service Starter

Kotlin/Spring 서버와 React 화면을 함께 개발하는 스타터입니다. 요구사항별로 작은 사용자 흐름을 구현하고, 인수 기준과 검증·평가 근거를 연결합니다.

## 시작

```bash
git clone https://github.com/jeony7/service-starter.git
cd service-starter
```

1. [AGENTS.md](AGENTS.md)와 요구사항 정리 스킬을 읽도록 요청하고, 실행 환경·산출물 제약을 먼저 확인합니다.
2. 기능과 공통 제약을 정리하면서 [앱 실행 안내](app/README.md)에 따라 화면·API·DB 연결을 확인합니다. 서로 독립적인 정리와 환경 준비는 병행할 수 있습니다.
3. Docker를 사용하는 환경이면 포함된 Dockerfile을 `docker build`·`docker run`으로 빌드·기동하고 데이터 볼륨을 초기에 확인합니다. 지정 실행 방식이 있으면 우선하고, 도구가 없어 실행하지 못한 범위는 기록합니다.
4. [요구사항 문서](docs/REQUIREMENTS.md)에서 다음 작은 구현 범위를 정하고 업무 기능을 추가합니다.

```text
AGENTS.md와 .agents/skills/define-requirements/SKILL.md를 읽고
제공한 자료에서 기능 요구사항과 공통 제약을 정리해줘.
실행 환경과 산출물 제약을 먼저 확인하고,
app/README.md를 따라 기본 템플릿의 실행 환경도 함께 준비해줘.
화면·API·H2 연결을 확인하고, Docker를 사용할 수 있으면
포함된 Dockerfile로 docker build·docker run과 볼륨 데이터 보존을 확인해줘.
실행하지 못한 항목은 구분해서 기록해줘.
사용자 흐름을 ASCII로 보여주고, 다음 구현 범위를 제안해줘.
업무 기능은 구현 범위를 정한 뒤 진행해줘.
```

```text
AGENTS.md와 .agents/skills/implement-usecase/SKILL.md를 읽고
합의한 기능의 첫 UC를 구현해줘.
인수 기준별 검증 수단과 실제 실행 근거·결과를 갱신해줘.
```

```text
AGENTS.md와 .agents/skills/verify-delivery/SKILL.md를 읽고
필수 요구사항의 현재 결과와 통합 실행을 확인해줘.
지표가 필요한 기능은 실제 측정 근거까지 확인해줘.
```

## 구조

```text
AGENTS.md             작업 기준과 필요한 지침으로의 연결
.agents/skills/       요구사항 / 구현 / 전체 검증
docs/
  REQUIREMENTS.md     기능·제약·UC·인수 기준·검증·평가
  DECISIONS.md        중요한 선택과 근거
  STACK.md            코드 구조와 기술 규칙
  COLLABORATION.md    필요할 때만 읽는 위임 지침
app/
  backend/           Kotlin / Spring / JPA / H2
  frontend/          React / TypeScript / Vite / Tailwind
  README.md          설치·빌드·실행·사용·DB 조회
  Dockerfile
```

`app/`은 독립적으로 빌드·실행할 수 있습니다. 업무 요구사항과 데이터는 프로젝트에서 추가합니다. 현재 스택과 실제 환경이 충돌할 때만 필요한 부분을 조정합니다. 검사 도구는 설정되어 있지만 lint·포맷은 명시적으로 요청할 때만 실행합니다.
