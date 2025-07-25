# 환경 변수 설정 가이드

이 문서는 `application.yml` 파일에 정의된 환경 변수들과 IntelliJ IDEA에서 이 변수들을 설정하는 방법을 설명합니다.

## 1. 환경 변수 목록

`src/main/resources/application.yml` 파일에는 다음과 같은 환경 변수들이 사용됩니다.

- `DB_URL`: 데이터베이스 연결 URL
- `DB_USERNAME`: 데이터베이스 사용자 이름
- `DB_PASSWORD`: 데이터베이스 비밀번호
- `SECRET_KEY`: JWT(JSON Web Token) 서명에 사용되는 비밀 키

## 2. IntelliJ IDEA에서 환경 변수 설정 방법

IntelliJ IDEA에서 애플리케이션을 실행할 때 환경 변수를 설정하는 방법은 여러 가지가 있습니다. 여기서는 가장 일반적인 두 가지 방법을 설명합니다.

### 방법 1: Run/Debug Configurations를 통한 설정 (권장)

이 방법은 특정 실행 구성에만 환경 변수를 적용하므로, 개발 환경과 프로덕션 환경을 분리하여 관리하기에 용용이합니다.

1.  **Run/Debug Configurations 열기**: IntelliJ IDEA 상단 메뉴에서 `Run` > `Edit Configurations...`를 선택합니다.
2.  **애플리케이션 선택**: 왼쪽 패널에서 현재 프로젝트의 Spring Boot 애플리케이션 실행 구성을 선택합니다. (일반적으로 프로젝트 이름으로 되어 있습니다.)
3.  **환경 변수 추가**: `Environment variables` 필드 옆의 `...` 버튼을 클릭합니다.
4.  **변수 입력**: 새 창에서 `+` 버튼을 클릭하여 각 환경 변수(`DB_URL`, `DB_USERNAME`, `DB_PASSWORD`, `SECRET_KEY`)와 해당 값을 입력합니다. 예를 들어:
    ```
    DB_URL=jdbc:mysql://localhost:3306/your_database_name?serverTimezone=UTC
    DB_USERNAME=your_db_username
    DB_PASSWORD=your_db_password
    SECRET_KEY=your_secret_key_for_jwt_signing
    ```
    **주의**: `SECRET_KEY`는 보안상 중요한 값이므로, 실제 배포 환경에서는 더욱 안전하게 관리해야 합니다.
5.  **적용 및 닫기**: `OK`를 클릭하여 환경 변수 창을 닫고, 다시 `OK`를 클릭하여 Run/Debug Configurations 창을 닫습니다.

이제 이 실행 구성으로 애플리케이션을 실행하면 설정된 환경 변수들이 적용됩니다.

### 방법 2: 시스템 환경 변수 설정 (운영체제 레벨)

이 방법은 운영체제 전체에 환경 변수를 설정하므로, 해당 변수가 필요한 모든 애플리케이션에서 접근할 수 있습니다. 하지만 특정 프로젝트에만 국한되지 않으므로 주의해야 합니다.

**macOS/Linux:**

터미널에서 `~/.bash_profile`, `~/.zshrc`, 또는 `~/.bashrc` 파일 중 하나를 열어 다음 라인을 추가합니다.

```bash
export DB_URL="jdbc:mysql://localhost:3306/your_database_name?serverTimezone=UTC"
export DB_USERNAME="your_db_username"
export DB_PASSWORD="your_db_password"
export SECRET_KEY="your_secret_key_for_jwt_signing"
```

파일을 저장한 후, 터미널을 다시 시작하거나 `source ~/.bash_profile` (또는 해당 쉘 설정 파일) 명령어를 실행하여 변경 사항을 적용합니다.

**Windows:**

1.  `제어판` > `시스템 및 보안` > `시스템` > `고급 시스템 설정`으로 이동합니다.
2.  `환경 변수(Environment Variables)` 버튼을 클릭합니다.
3.  `시스템 변수(System variables)` 또는 `사용자 변수(User variables)` 섹션에서 `새로 만들기(New...)`를 클릭하여 각 환경 변수와 값을 추가합니다.

시스템 환경 변수를 설정한 후에는 IntelliJ IDEA를 포함한 모든 애플리케이션을 다시 시작해야 변경 사항이 적용됩니다.

## 3. 중요 사항

-   **보안**: `SECRET_KEY`와 같은 민감한 정보는 Git 저장소에 직접 커밋하지 않도록 `.gitignore` 파일에 `application.yml` 또는 `.env` 파일을 추가하는 것을 고려해야 합니다. 대신, `.env` 파일 등을 사용하여 로컬에서 관리하고, 배포 시에는 CI/CD 파이프라인이나 클라우드 서비스의 환경 변수 관리 기능을 활용하는 것이 좋습니다.
-   **값 대체**: `application.yml`에서 `${VAR_NAME}` 형식으로 변수를 사용하면, Spring Boot가 애플리케이션 시작 시 해당 환경 변수 값으로 자동 대체합니다.

이 가이드가 환경 변수 설정에 도움이 되기를 바랍니다.