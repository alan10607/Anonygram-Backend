#!/bin/bash

source test_common.sh

ab -n 10000 -c 100 -t 1 -H "Authorization: Bearer $BEARER" \
  "$HOST/forum/article/$ARTICLE_ID" \
  2>&1 | tee "out/get:forum_article.$NOW.txt"