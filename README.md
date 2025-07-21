# SE_BE: 백엔드 API 서버 (초기 설정 단계)

이 프로젝트는 Spring Boot를 기반으로 사용자의 일기 데이터와 별자리 정보를 관리하는 백엔드 API 서버를 목표로 개발 중입니다. 현재는 프로젝트 초기 설정 단계로, ERD를 기반으로 데이터베이스 엔티티를 구성한 상태입니다.

## 목적

이 README 파일은 백엔드 로직의 변경사항을 추적하고, 프로젝트의 구조와 향후 개발될 API 명세를 명확하게 전달하는 것을 목표로 합니다.

## 현재 진행 상황

*   **Entity:** ERD(개체-관계 다이어그램)를 기반으로 `User`, `Diary`, `Star`, `Constellation` 등 핵심 엔티티 클래스 설계가 완료되었습니다.
*   **Controller, Service, Repository:** 기본적인 클래스 구조만 생성되었으며, 내부에 비즈니스 로직은 아직 구현되지 않았습니다.

## 예정된 주요 기능

*   **사용자 관리:** 회원가입, 로그인, 사용자 정보 조회를 위한 API를 제공할 예정입니다.
*   **일기 관리:** 일기 생성, 조회, 수정, 삭제 기능을 제공할 예정입니다.
*   **별자리 생성:** 사용자의 일기 데이터를 기반으로 별자리를 생성하고 조회할 예정입니다.
*   **OpenAI 연동:** OpenAI API를 활용하여 사용자의 일기에 대한 감정 분석 및 피드백을 제공할 예정입니다.

## 기술 스택

*   **Framework:** Spring Boot 3.5.3
*   **Database:** MySQL
*   **ORM:** Spring Data JPA
*   **Security:** Spring Security, JWT
*   **ETC:** Lombok

## 프로젝트 구조

```
SE_BE/
├── src/
│   ├── main/
│   │   ├── java/
│   │   │   └── com/
│   │   │       └── example/
│   │   │           └── se_be/
│   │   │               ├── controller/  # API 엔드포인트를 정의하는 컨트롤러 (구현 예정)
│   │   │               ├── dto/         # 데이터 전송 객체 (필요시 추가 예정)
│   │   │               ├── entity/      # 데이터베이스 엔티티 (설계 완료)
│   │   │               ├── repository/  # 데이터베이스 접근을 위한 레포지토리 (구현 예정)
│   │   │               ├── security/    # Spring Security 설정 (구현 예정)
│   │   │               └── service/     # 비즈니스 로직을 처리하는 서비스 (구현 예정)
│   │   └── resources/
│   │       └── application.yml # 애플리케이션 설정 파일
│   └── test/
└── build.gradle # 프로젝트 의존성 및 빌드 설정
```

## API 명세

향후 개발이 진행됨에 따라 API 문서를 추가할 예정입니다.
