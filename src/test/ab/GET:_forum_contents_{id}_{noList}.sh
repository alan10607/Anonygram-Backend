#!/bin/bash

source ab_common.sh

ab -n 100 -c 10000 -t 60 -H "Authorization: Bearer $BEARER" $HOST/forum/contents/$id/$noList 
2>&1 | tee "out/GET:_forum_contents_{id}_{noList}.$NOW.txt"