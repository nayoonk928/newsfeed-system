#!/bin/bash

# 결과 디렉토리 생성
mkdir -p results/case1
mkdir -p results/case2
mkdir -p results/case3

# 첫 번째 테스트: case1.yml 실행
echo "Running case1 test..."
artillery run --output results/case1/case1_report.json case1.yml
sleep 1m
artillery report results/case1/case1_report.json --output results/case1/case1_report.html

# 두 번째 테스트: case2.yml 실행
echo "Running case2 test..."
artillery run --output results/case2/case2_report.json case2.yml
sleep 1m
artillery report results/case2/case2_report.json --output results/case2/case2_report.html

# 세 번째 테스트: case3.yml 실행
echo "Running case3 test..."
artillery run --output results/case3/case3_report.json case3.yml
sleep 1m
artillery report results/case3/case3_report.json --output results/case3/case3_report.html

echo "All tests completed."