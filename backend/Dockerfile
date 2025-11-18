# 1단계: 빌드(Build) 환경
# Gradle 8.5와 Java 17 (Amazon Corretto)을 사용해 앱을 빌드합니다.
FROM gradle:8.5.0-jdk17-jammy AS build

# 작업 디렉터리를 /app/backend로 설정합니다.
WORKDIR /app/backend

# 모든 파일을 복사합니다 (빌드 파일 + 소스 코드)
COPY . .

# Gradle 실행 권한을 부여합니다.
RUN chmod +x ./gradlew

# Gradle을 사용해 테스트를 건너뛰고, *상세 로그*와 함께 빌드합니다.
RUN ./gradlew build -x test --info --stacktrace


# 2단계: 실행(Runtime) 환경
# 빌드된 .jar 파일을 실행할 가벼운 이미지를 사용합니다.
FROM amazoncorretto:17-alpine

# 작업 디렉터리를 /app으로 설정합니다.
WORKDIR /app

# 1단계(build)에서 생성된 .jar 파일을 복사해옵니다.
COPY --from-build /app/backend/build/libs/*.jar app.jar

# Spring Boot 앱은 기본적으로 8080 포트를 사용합니다.
EXPOSE 8080

# 컨테이너가 시작될 때 이 명령어를 실행합니다.
ENTRYPOINT ["java","-jar","/app/app.jar"]
