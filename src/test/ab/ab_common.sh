#!/bin/bash

clear
id=9373682a-7903-4f9c-bf31-60fe544cf9f9
idList=9373682a-7903-4f9c-bf31-60fe544cf9f9,e6db013e-924c-4b6d-9789-cbacac975d6e,7c17b906-ede0-4f1e-9931-4a908d112fdb,a294166e-d44f-4fe2-b62e-f185ac4d623f,131fadcf-6eeb-4692-917b-8249cd5970f3,22424159-03d1-4ba5-93a8-d08b8349d2ac,38c37c4d-0f46-4435-acae-8de38d86ab39,5c5878c1-1a2e-4e12-a1fd-fa5d93d2d857,0d2241cf-acc6-48e0-b221-58067bf2a9a6,39bf6275-ca99-4226-ba4b-e61782f3f19a
no=0
noList=0,1,2,3,4,5,6,7,8,9
BEARER=eyJhbGciOiJIUzI1NiJ9.eyJlbWFpbCI6ImFsYW5AYWxhbiIsInRva2VuVHlwZSI6IkFDQ0VTU19UT0tFTiIsInN1YiI6ImFsYW4xMDYwNyIsImlhdCI6MTY5MzA1NDg2NCwiZXhwIjoxNjkzMDU4NDY0fQ.gzC0nJ_TaCZqG8UtsP3lLvGgs2XRoLK9SZzCimix8as
HOST=https://localhost
NOW=$(date +"%Y-%m-%d.%H:%M:%S")
HEADERS="X-CSRF-TOKEN: 79eecf83-f808-47e8-a242-4cb9d8d81920"
COOKIES="X-CSRF-TOKEN=79eecf83-f808-47e8-a242-4cb9d8d81920;Authorization=$BEARER"
