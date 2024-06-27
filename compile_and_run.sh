#!/bin/bash

# 소스 코드 컴파일
javac Solution.java

# 결과 파일 초기화
> result.txt

# 컴파일이 성공했는지 확인
if [ $? -eq 0 ]; then

    # input.txt 파일에서 각 줄을 읽어와서 처리
    while IFS= read -r line || [[ -n "$line" ]]; do

        # Java 프로그램을 실행하고 입력을 전달
        echo "$line" | java Solution >> result.txt
    done < input.txt

else
    echo "Compilation failed"
fi