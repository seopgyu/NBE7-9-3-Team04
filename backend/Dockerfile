# 1단계: 빌드(Build) 환경
# Gradle 8.5와 Java 17 (Amazon Corretto)을 사용해 앱을 빌드합니다.
FROM gradle:8.5.0-jdk17-jammy AS build

# 작업 디렉터리를 /app/backend로 설정합니다.
WORKDIR /app/backend

# build.gradle.kts, settings.gradle.kts 등 빌드에 필요한 파일을 먼저 복사합니다.
COPY build.gradle.kts settings.gradle.kts gradlew ./
COPY gradle ./gradle

# Gradle 실행 권한 부여
RUN chmod +x ./gradlew

# 의존성만 먼저 다운로드합니다.
RUN ./gradlew dependencies

# 나머지 소스 코드(src)를 복사합니다.
COPY src ./src

# Gradle을 사용해 테스트를 건너뛰고 빌드합니다.
# Render의 Free Plan은 리소스가 적으므로 -x test로 테스트를 건너뛰는 것이 좋습니다.
RUN ./gradlew build -x test


# 2단계: 실행(Runtime) 환경
# 빌드된 .jar 파일을 실행할 가벼운 JRE(Java 실행 환경) 이미지를 사용합니다.
FROM amazoncorretto:17-alpine

# 작업 디렉터리를 /app으로 설정합니다.
WORKDIR /app

# 1단계(build)에서 생성된 .jar 파일을 복사해옵니다.
# 'backend-0.0.1-SNAPSHOT.jar'는 실제 파일명으로 변경해야 할 수 있습니다.
# build/libs/ 폴더에 .jar 파일이 하나만 있다고 가정합니다.
COPY --from=build /app/backend/build/libs/*.jar app.jar

# Spring Boot 앱은 기본적으로 8080 포트를 사용합니다.
EXPOSE 8080

# 컨테이너가 시작될 때 이 명령어를 실행합니다.
ENTRYPOINT ["java","-jar","/app/app.jar"]
