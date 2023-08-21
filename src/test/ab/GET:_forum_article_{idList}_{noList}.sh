#!/bin/bash

source ab_common.sh

ab -n 100 -c 10000 -t 60 -H "Authorization: Bearer $BEARER" $HOST/forum/article/$idList/$noList 
2>&1 | tee "out/GET:_forum_article_{idList}_{noList}.$NOW.txt"