#!/bin/bash

source ab_common.sh

ab -n 10 -c 1000 -t 60 -H "Authorization: Bearer $BEARER" -T application/json -p "body/POST:_forum_article.json" $HOST/forum/article 
2>&1 | tee "out/POST:_forum_article.$NOW.txt"