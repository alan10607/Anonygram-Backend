#!/bin/bash

source ab_common.sh

ab -n 1000 -c 10 -t 60 -T application/json -p "body/POST:_forum_article_{id}.json" -H "$HEADERS" -C "$COOKIES" $HOST/forum/article/$id 2>&1 | tee "out/POST:_forum_article_{id}.$NOW.txt"