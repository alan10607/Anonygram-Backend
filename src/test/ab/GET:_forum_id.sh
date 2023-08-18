#!/bin/bash

source ab_common.sh

ab -n 100 -c 10000 -t 60 -H "Authorization: Bearer $BEARER" $HOST/forum/id 
2>&1 | tee "out/GET:_forum_id.$NOW.txt"