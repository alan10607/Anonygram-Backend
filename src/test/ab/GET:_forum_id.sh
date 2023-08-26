#!/bin/bash

source ab_common.sh

ab -n 10000 -c 100 -t 60 -H "$HEADERS" -C "$COOKIES" $HOST/forum/id 2>&1 | tee "out/GET:_forum_id.$NOW.txt"