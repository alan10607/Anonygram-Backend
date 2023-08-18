#!/bin/bash

source ab_common.sh

ab -n 100 -c 10000 -t 60 -H "Authorization: Bearer $BEARER" $HOST/forum/content/$id/$no 
2>&1 | tee "out/GET:_forum_content_{id}_{no}.$NOW.txt"