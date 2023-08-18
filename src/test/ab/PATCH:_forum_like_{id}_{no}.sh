#!/bin/bash

source ab_common.sh

ab -n 10 -c 1000 -t 60 -H "Authorization: Bearer $BEARER" -T application/json -p "body/PATCH:_forum_like_{id}_{no}.json" $HOST/forum/like/$id/$no 
2>&1 | tee "out/PATCH:_forum_like_{id}_{no}.$NOW.txt"