#!/bin/bash

source ab_common.sh

exit #Not support 
2>&1 | tee "out/DELETE:_forum_article_{id}_{no}.$NOW.txt"