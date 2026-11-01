# Service

Kotlin/Spring 서버와 React 화면을 한 서비스로 실행합니다. 기본 화면의 **연결 확인**은 `/api/health`를 호출하고, 서버는 JPA로 H2에 저장된 애플리케이션 이름을 읽어 응답합니다.

## 필요한 도구

- 로컬 개발: Java 21, Node.js 22.13+ 또는 24+, npm
- 컨테이너 빌드·실행: Docker 엔진과 CLI. 이미지를 빌드할 때 Node 24와 Java 21을 사용하므로 호스트의 Java·Node는 필요하지 않음

Gradle은 포함된 공식 Wrapper를 사용합니다. 최초 설치·빌드는 패키지 저장소에 접근할 수 있어야 합니다.

## 환경변수 준비

이 문서의 `app/`은 이 README가 있는 디렉터리입니다.

```bash
cd app
cp .env.example .env
```

`.env`의 `DB_PASSWORD`에 사용할 값을 설정합니다. `.env`는 Git에 포함하지 않습니다. Java와 Vite가 `.env`를 같은 방식으로 읽는다고 가정하지 말고, 로컬 서버 실행 전에 아래처럼 명시적으로 로드합니다. Docker 실행에서는 `--env-file .env`로 전달합니다.

| 변수 | 의미·기본값 |
|---|---|
| `DB_USERNAME` | H2 사용자명, 기본 `app` |
| `DB_PASSWORD` | H2 비밀번호. 실행 전에 설정 |
| `APP_DATA_DIR` | DB 디렉터리. 기본 `./data`, **서버의 작업 디렉터리 기준** |
| `SERVER_ADDRESS` | 통합 실행 `0.0.0.0`, 개발 서버 `127.0.0.1` |
| `SERVER_PORT` | 통합 실행 `3000`, 개발 서버 `8080` |
| `H2_ALLOW_REMOTE` | 개발 콘솔의 직접 원격 접근 허용. 기본 `false` |
| `BACKEND_TARGET` | Vite 프록시 대상. 기본 `http://127.0.0.1:8080` |

비밀번호를 변경하면 기존 DB의 인증 정보와도 맞아야 합니다. 경로를 바꾸면 다른 DB를 열게 되므로 초기 데이터가 사라진 것으로 오인하지 않도록 확인합니다.

## 개발 실행

터미널 1:

```bash
cd app
set -a
. ./.env
set +a
cd backend
./gradlew bootRun --args='--spring.profiles.active=dev'
```

터미널 2:

```bash
cd app/frontend
npm ci
npm run dev
```

```text
브라우저 -> Vite 0.0.0.0:3000
              |
              +-- /api        -> Spring 127.0.0.1:8080
              +-- /h2-console -> Spring 127.0.0.1:8080
```

- 로컬 접속: `http://localhost:3000/`
- 연결 확인 API: `http://localhost:3000/api/health`
- 개발용 DB 콘솔: `http://localhost:3000/h2-console/`
- `0.0.0.0`은 바인딩 주소입니다. 원격 접속은 실행 환경이 제공하는 호스트 주소를 사용합니다.
- 3000번이 사용 중이면 Vite가 다른 포트로 바꾸지 않고 종료합니다.
- 별도 프리뷰 호스트 이름이 필요하면 `__VITE_ADDITIONAL_SERVER_ALLOWED_HOSTS`에 해당 호스트만 지정합니다.
- 종료는 각 터미널에서 `Ctrl+C`입니다.

## 빌드와 통합 실행

통합 JAR을 실행 중이면 해당 서버 프로세스를 종료한 뒤 재빌드합니다. 실행 중인 JAR 파일을 덮어쓰지 않습니다.

```bash
cd app/frontend
npm ci
npm run build
cd ../backend
./gradlew test bootJar
```

프론트 산출물이 JAR의 정적 리소스로 포함됩니다. 프론트를 빌드하지 않으면 `bootJar`가 이유와 명령을 표시하고 실패합니다. 재빌드 시 이전 정적 파일은 정리됩니다.

3000번을 쓰는 개발 프론트를 종료한 뒤, 새 터미널에서:

```bash
cd app
set -a
. ./.env
set +a
cd backend
java -jar build/libs/app.jar
```

Spring이 `0.0.0.0:3000`에서 화면과 API를 함께 제공합니다. 기본 프로필에서는 H2 콘솔이 비활성화되어 있습니다. 통합 빌드에서도 개발 콘솔을 확인하려면 개발 환경에서만 아래처럼 실행합니다.

```bash
SERVER_ADDRESS=0.0.0.0 SERVER_PORT=3000 java -jar build/libs/app.jar --spring.profiles.active=dev
```

직접 원격 접속 시 콘솔이 로컬 접근만 허용한다면 개발 환경에서 `H2_ALLOW_REMOTE=true`를 함께 지정하고 DB 비밀번호를 설정합니다. 콘솔은 SQL 실행·수정 기능을 가진 개발 도구입니다.

`npm run preview`는 빌드된 화면의 로컬 확인용입니다. API를 포함한 최종 실행은 Spring JAR 또는 Docker를 사용합니다.

## Docker

```bash
cd app
docker build -t service-app:local .
docker run -d --name service-app \
  --env-file .env \
  -e APP_DATA_DIR=/app/data \
  -e SERVER_ADDRESS=0.0.0.0 -e SERVER_PORT=3000 \
  -p 3000:3000 \
  -v service-data:/app/data \
  service-app:local
```

접속 주소는 `http://localhost:3000/`입니다. 화면과 API를 한 컨테이너에서 제공합니다. H2 파일은 `service-data` 볼륨에 저장합니다. `.env`의 로컬 경로·포트가 컨테이너 설정을 바꾸지 않도록 위 명령에서 명시적으로 덮어씁니다.

로그·종료·재시작:

```bash
docker logs -f service-app
docker stop service-app
docker start service-app
```

새 이미지로 교체할 때는 기존 컨테이너를 종료·삭제한 뒤 다시 빌드하고 위의 `docker run` 명령을 실행합니다. 같은 `service-data` 볼륨을 연결하면 커밋된 DB 데이터가 유지됩니다. 컨테이너 삭제와 볼륨 삭제는 별개이며, `docker volume rm service-data`는 이 앱의 저장 데이터를 삭제합니다.

Docker 빌드는 컴파일·타입 검사를 수행하지만 테스트는 별도로 `./gradlew test`로 실행합니다. 컨테이너는 기본 프로필로 실행하므로 DB 콘솔을 노출하지 않습니다.

macOS에서 Colima로 Docker 엔진을 사용하는 경우 `colima start`로 시작하고 `colima stop`으로 종료합니다. Linux나 다른 Docker 실행 환경에서는 해당 엔진의 기동 방법을 사용합니다.

## DB 조회와 초기 데이터

- DB: H2 파일 DB, 별도 DB 서버 기동 불필요
- 기본 JDBC URL: `jdbc:h2:file:./data/app`
- 로컬 파일: `app/backend/data/app.mv.db` (위 실행 명령 기준)
- 컨테이너 파일: `/app/data/app.mv.db` (볼륨 연결)
- 초기 데이터: `config/InitialData.kt`가 `app_settings`에 `application.name=Service` 한 행을 없을 때만 저장합니다. 재실행 시 기존 값을 덮어쓰지 않습니다.
- 기본 스키마 설정: `ddl-auto=update`. 자동 변경으로 다루기 어려운 스키마 변경은 별도 마이그레이션으로 관리해야 합니다.

개발용 DB 콘솔을 열고 Driver Class는 `org.h2.Driver`, JDBC URL은 위 URL, 사용자명·비밀번호는 실행 시 설정한 값으로 입력합니다. URL의 작업 디렉터리는 서버 기준입니다. 콘솔이 기본으로 제안하는 다른 DB에 접속하지 않도록 URL을 확인합니다.

```sql
SELECT setting_key, setting_value
FROM app_settings
ORDER BY setting_key;

SELECT COUNT(*) AS row_count FROM app_settings;
```

처음에는 한 행이 보이고, 초기화 코드를 다시 실행해도 한 행이어야 합니다. 재시작 후에도 같은 파일을 사용하면 커밋된 값이 유지됩니다. 콘솔에서 데이터를 변경해 확인할 때는 Auto commit 상태를 확인하고, 꺼져 있으면 `COMMIT;`을 실행합니다. 파일 DB의 기본 JVM 종료 동작은 유지하며, 정상 종료와 강제 종료를 같은 검증으로 취급하지 않습니다. 기능 데이터를 추가하면 주요 테이블·조회 SQL·예상 초기 행 수를 이 절에 갱신합니다.

데이터를 초기화하려면 서버를 완전히 종료한 뒤 **이 앱의 실제 DB 경로**에서 `app.mv.db`와 해당 DB의 추적 파일을 삭제합니다. 사용자 데이터도 함께 삭제됩니다. 다른 앱이나 다른 경로의 DB 파일은 삭제하지 않습니다. 재시작하면 스키마와 기본 한 행이 다시 생성됩니다.

조회 결과가 예상과 다르면 실행 프로필 → 실제 JDBC URL·작업 디렉터리 → 커밋 여부 → 초기 데이터 삽입 여부를 확인합니다. Spring Security를 추가할 때는 콘솔 경로의 접근 권한·CSRF·프레임 정책을 개발 프로필에 한정해 설정합니다.

## 확인 명령

프론트 타입 검사·빌드:

```bash
cd app/frontend
npm run typecheck
npm run build
```

서버 테스트:

```bash
cd app/backend
./gradlew test
```

테스트는 격리된 메모리 DB와 임시 파일 DB를 사용합니다. JPA 조회·초기 데이터 재실행·롤백·파일 DB 재시작 보존을 확인하며 실제 개발 DB를 수정하지 않습니다. 보고서는 `backend/build/reports/tests/test/index.html`에 생성됩니다. 현재 포함된 테스트는 업무 기능이나 실제 브라우저 검증을 대신하지 않습니다.

아래는 **요청할 때만 실행하는** 스타일 명령입니다. 저장·커밋·빌드·테스트에는 자동 연결되어 있지 않습니다.

```bash
cd app/backend
./gradlew ktlintCheck
./gradlew ktlintFormat
```

```bash
cd app/frontend
npm run lint
npm run format:check
npm run format
```

## 기능 추가 후 갱신

주요 기능의 사용 순서, 역할별 접속 방법, 입력 파일 형식, 데이터 적재·조회, 구현/미구현/미검증과 알려진 한계를 실제 동작에 맞춰 기록합니다. 현재는 연결 확인용 기본 화면과 API만 포함되어 있습니다.
