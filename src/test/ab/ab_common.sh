#!/bin/bash

clear
id=d5b52b77-244c-4476-b440-ec4746a5cbd7
idList=d5b52b77-244c-4476-b440-ec4746a5cbd7,29d51d94-9201-41db-b20d-ee71dc59cfd9,447a52c8-e967-4038-ae91-dc259bfae5f0,432086fe-1739-463c-b60d-cbff8ee2993c,997bed00-9754-4194-adec-a88a54571978,436a7258-9de8-412c-8fa5-e85180fa0cc0,db9dadc5-a62b-4000-a91f-797eb9d81b64,932585fc-9b52-4077-b418-b04013642493,3c3820b3-de95-4cfa-b732-5128b199f8be,2070b237-3064-4410-9ceb-87e11608b1a5
no=0
noList=0,1,2,3,4,5,6,7,8,9
BEARER=eyJhbGciOiJIUzI1NiJ9.eyJ0b2tlblR5cGUiOiJBQ0NFU1NfVE9LRU4iLCJlbWFpbCI6ImFsYW5AYWxhbiIsInN1YiI6ImFsYW4xMDYwNyIsImlhdCI6MTY5MzA0NDY4NywiZXhwIjoxNjkzMDQ4Mjg3fQ.Foy6Pk1RSDSkE0iYa0VSgYMjqwP3ZxJ7dw9-uv0hWZ4
HOST=https://localhost
NOW=$(date +"%Y-%m-%d.%H:%M:%S")
HEADERS="X-CSRF-TOKEN: 79eecf83-f808-47e8-a242-4cb9d8d81920"
COOKIES="X-CSRF-TOKEN=79eecf83-f808-47e8-a242-4cb9d8d81920;Authorization=$BEARER"
