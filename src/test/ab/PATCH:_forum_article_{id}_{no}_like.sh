#!/bin/bash

source ab_common.sh

ab -n 1000 -c 10 -t 60 -T application/json -p "body/PATCH:_forum_article_{id}_{no}_like.json" -H "$HEADERS" -C "$COOKIES" $HOST/forum/article/$id/$no/like 2>&1 | tee "out/PATCH:_forum_article_{id}_{no}_like.$NOW.txt"