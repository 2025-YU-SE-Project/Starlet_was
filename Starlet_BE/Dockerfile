# 사용할 기본 이미지 지정 (JDK 17 기본 버전)
FROM eclipse-temurin:17

# 빌드된 JAR 파일 이름을 'app.jar'로 지정
ARG JAR_FILE=build/libs/*.jar

# JAR 파일을 컨테이너의 'app.jar'로 복사
COPY ${JAR_FILE} app.jar

# 컨테이너 시작 시 실행될 명령어
ENTRYPOINT ["java", "-jar", "app.jar"]