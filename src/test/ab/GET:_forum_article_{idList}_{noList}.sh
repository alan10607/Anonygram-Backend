#!/bin/bash

source ab_common.sh

ab -n 10000 -c 100 -t 60 -H "$HEADERS" -C "$COOKIES" $HOST/forum/article/$idList/$noList 2>&1 | tee "out/GET:_forum_article_{idList}_{noList}.$NOW.txt"