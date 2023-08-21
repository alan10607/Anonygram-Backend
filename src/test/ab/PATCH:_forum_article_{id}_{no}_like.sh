#!/bin/bash

source ab_common.sh

ab -n 10 -c 1000 -t 60 -H "Authorization: Bearer $BEARER" -T application/json -p "body/PATCH:_forum_article_{id}_{no}_like.json" $HOST/forum/article/$id/$no/like 
2>&1 | tee "out/PATCH:_forum_article_{id}_{no}_like.$NOW.txt"