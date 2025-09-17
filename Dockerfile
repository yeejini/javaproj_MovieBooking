# FROM eclipse-temurin:17-jre-alpine
# WORKDIR /app

# # 필수 패키지 설치
# RUN apk add --no-cache bash mysql-client

# # 앱 JAR 복사
# COPY app.jar app.jar
# COPY ./db/init.sql /docker-entrypoint-initdb.d/init.sql


# # DB 준비될 때까지 기다렸다가 앱 실행
# ENTRYPOINT ["sh", "-c", "until mysqladmin ping -h movie-mysql -uroot -p1111 --silent; do echo '⏳ Waiting for DB'; sleep 2; done; java -jar app.jar"]

FROM eclipse-temurin:17-jre  

WORKDIR /app

# MySQL 클라이언트 설치
RUN apt-get update && apt-get install -y default-mysql-client

# 앱 JAR 복사
COPY app.jar app.jar

# DB 준비될 때까지 기다렸다가 앱 실행
ENTRYPOINT ["sh", "-c", "until mysqladmin ping -h db -uroot -p1111 --silent; do echo '⏳ Waiting for DB'; sleep 2; done; java -jar app.jar"]