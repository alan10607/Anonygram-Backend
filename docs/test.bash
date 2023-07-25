#!/bin/bash

LOCATION="https://location"

read -p "Press enter to continue"
ab -n 100 -c 1000 -t 30 $LOCATION/id

read -p "Press enter to continue"
ab -n 100 -c 1000 -t 30 -p $LOCATION/article/

# 測試 GET 方法
ab -n <總請求數> -c <併發數> -t <測試時間> <目標網址>

# 測試 POST 方法
ab -n <總請求數> -c <併發數> -t <測試時間> -p <POST數據檔> -T <POST數據類型> <目標網址>

# 測試 PUT 方法
ab -n <總請求數> -c <併發數> -t <測試時間> -u <PUT數據檔> -T <PUT數據類型> <目標網址>

# 測試 PATCH 方法
ab -n <總請求數> -c <併發數> -t <測試時間> -p <PATCH數據檔> -T <PATCH數據類型> <目標網址>

# 測試 DELETE 方法
ab -n <總請求數> -c <併發數> -t <測試時間> <目標網址>
