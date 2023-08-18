#!/bin/bash

clear
read -r BEARER < "token.txt"
NOW=$(date +"%Y-%m-%d.%H:%M:%S")
HOST=https://localhost
id=
no=
idList=
noList=
