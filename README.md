# Anonygram
<p align="center">
	<a href="http://35.201.171.165:8081/" target="_blank">
        <img src="https://img.shields.io/badge/Demo-GCP-orange">
	</a>
	<a href="https://github.com/alan10607/LeafHub" target="_blank">
        <img src="https://img.shields.io/badge/Github-LeafHub-green">
	</a>
    <img src="https://img.shields.io/badge/Spring Boot-2.7.3-lightgray">
    <img src="https://img.shields.io/badge/Redis-7.0.4-lightgray">
    <img src="https://img.shields.io/badge/MySql-8.0.30-lightgray">
</p>

## Article Redis Structure
#### idStr
- data:idStr
- string, idSet組好後放入這裡, 之後只需從這裡找所有id
- 讀: O(1)
- 寫: O(1)

#### idSet
- data:idSet
- zset, 用來存放文章id
- 讀: O(log(N)+M), N=M=所有id
- 寫: O(1), 因為採用時間逆序加入zset

#### article
- data:art:{id}
- hash, 用來存放文章主資料: 標題, 發文者...
- 讀: O(N), N=id數量
- 寫: O(1)

#### content
- data:cont:{id}:{no}
- hash, 用來存放留言資料: 留言, 留言者, 時間...
- 讀: O(N*M), N=id數量, M=no數量
- 寫: O(1)

```
        +------------+
   ---> | data:idStr | > {id},{id},{id}...
   |    +----------- +
   |
+------------+
| data:idSet | > {id}
+------------+    ...
   |
   |
   |    +---------------+
   |--> | data:art:{id} | > {title,contNum,status,updateDate,createDate}
   |    +---------------+    ...
   |
   |
   |    +---------------------+
   ---> | data:cont:{id}:{no} | > {author,word,likes,status,updateDate,createDate}
        +---------------------+    ...
```

## Like Redis Structure
- set, 全部讀寫皆為: O(1)
- 批次時: O(N), N=所有異動資料
- 查詢順序: LIKE_NEW > LIKE_BATCH > LIKE_STATIC > DB
- Value格式: {id}:{no}:{userId}:{likeStatus}, likeStatus: 1=islike, 0=unlike

#### LIKE_NEW
- 有任何異動優先修改這個, 查詢也優先以這個為主, 批次開始時轉移資料到LIKE_BATCH

#### LIKE_BATCH
- 只有剛好在批次中才會出現, 批次結束後要刪除LIKE_BATCH與LIKE_STATIC

#### LIKE_STATIC
- 只能由DB放入資料, 唯讀, 禁止別的方法修改

```
Query  Result  Modify
   |     ^      |
   v     |      v
+------------------+
| data:like:new    | > {id}:{no}:{userId}:{likeStatus}
+------------------+    ...
   |     ^      |
   v     |      v Batch start
+------------------+
| data:like:batch  | -------
+------------------+       |
   |     ^                 |
   v     |                 |
+------------------+       | Batch save to DB
| data:like:static |       |
+------------------+       |
   |     ^                 |
   v     |                 |
+------------------+       |
|        DB        | <------
+------------------+
```

# Imgur OAuth

圖片伺服器使用Imgur, 透過OAuth驗證後, 將使用者上傳的圖片存到指定的相簿後待用

```
           +----------+   1. Get token authorization       +---------------------+
           |          |  --------------------------------> |                     |
           |          |   2. Get access token              | Imgur Authorization |
           |          |  <-------------------------------- |                     |
           |          |                                    +---------------------+
3. User    |          |
 request   |          |   4. Upload img with access token  +---------------------+
---------> |          |  --------------------------------> |                     |
           |  server  |   5. Get img url                   | Imgur Resource      |
           |          |  <-------------------------------- |                     |
           |          |                                    +---------------------+
           |          |
           |          |   6. Re-auth with refresh token    +---------------------+
           |          |  --------------------------------> |                     |
           |          |   7. Get access token              | Imgur Authorization |
           |          |  <-------------------------------- |                     |
           +----------+                                    +---------------------+
```

## 可改進空間
- 前端框架, 前後分離
- 消息中間件
- 資料庫優化

## Demo
#### 用戶登入
<img src="https://raw.githubusercontent.com/alan10607/LeafHub/master/docs/demo1.jpg" width="300"/>

###
#### 用戶註冊
<img src="https://raw.githubusercontent.com/alan10607/LeafHub/master/docs/demo2.jpg" width="300"/>

###
#### 文章瀏覽(登入)
<img src="https://raw.githubusercontent.com/alan10607/LeafHub/master/docs/demo3.jpg" width="300"/>

###
#### 文章瀏覽(登入)
<img src="https://raw.githubusercontent.com/alan10607/LeafHub/master/docs/demo5.jpg" width="300"/>

###
#### 展開留言
<img src="https://raw.githubusercontent.com/alan10607/LeafHub/master/docs/demo4.jpg" width="300"/>