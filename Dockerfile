# 베이스 이미지로 OpenJDK 사용
FROM openjdk:21-jdk

# 작업 디렉토리 설정
WORKDIR /app

# 애플리케이션 JAR 파일 복사
COPY build/libs/reward-0.0.1-SNAPSHOT.jar app.jar


# 애플리케이션 실행
ENTRYPOINT ["java", "-jar", "app.jar"]
