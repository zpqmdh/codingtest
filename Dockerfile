# Dockerfile

# 기반이 될 도커 이미지
FROM openjdk:11

# 작업 디렉토리 생성
WORKDIR /usr/src/app

# 소스 코드 복사
COPY . .

# 컴파일 및 실행 스크립트 복사
COPY compile_and_run.sh .

# 컴파일 및 실행 스크립트 실행 권한 부여
RUN chmod +x compile_and_run.sh

# 컴파일 및 실행 스크립트 실행
CMD ["./compile_and_run.sh"]

