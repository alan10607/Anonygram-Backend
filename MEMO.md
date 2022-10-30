# MEMO
### 進入docker mysql
docker exec -it leaf-mysql bash
mysql -u root -p

### ab test
ab -n 2000 -c 100 -p post -T application/json http://localhost:8080/view/test

### 臨時設定redis
CONFIG SET requirepass "root"
AUTH root

### redis-cli其他port
redis-cli -h 127.0.0.1 -p 6380

### maven install skip test
mvn -Dmaven.test.skip=true install

### docker build / 其他platform
docker build -f Dockerfile -t alan10607/leaf:0.8.5 .
docker buildx ls
docker build --platform linux/amd64 -f Dockerfile -t alan10607/leaf:0.9.0 .

### docker run
docker run --env-file env/leaf-env -p 8081:8080 -v ~/docker/volume/leaf/log:/log --name leaf-server -d alan10607/leaf:0.8.5 ./wait-for-it.sh leaf-mysql:3306 -- ./wait-for-it.sh leaf-redis:6379 -- java -jar /leaf-server.jar
docker run --env-file env/mysql-env -p 3307:3306 --name leaf-mysql -d mysql
docker run -p 6380:6379 -v ~/docker/volume/redis/data:/data -v ~/docker/volume/redis/conf:/usr/local/etc/redis/redis.conf --name leaf-redis -d redis redis-server /usr/local/etc/redis/redis.conf --appendonly yes --requirepass "root"

### create-db checkout
git checkout master  src/main/java/com/alan10607/leaf/dto
git checkout master  src/main/java/com/alan10607/leaf/dao
git checkout master  src/main/java/com/alan10607/leaf/model

### JVM params
-DLOG_PATH=/Users/kuoping/Documents/GitHub/Leaf/log
-DMYSQL_HOST=localhost
-DMYSQL_PORT=3307
-DMYSQL_USER=root
-DMYSQL_PASSWORD=root
-DREDIS_HOST=localhost
-DREDIS_PORT=6380
-DREDIS_PASSWORD=root

### GCP setting
https://console.cloud.google.com  
compute engine > create vm

1. 取得遠端更新的檔案清單
```bash
sudo apt-get -y update
 ```
2. 在 Docker 加入官方 GPG KEY
```bash
curl -fsSL https://download.docker.com/linux/ubuntu/gpg | sudo apt-key add -
```
3. 新增APP伺服器
```bash
sudo add-apt-repository "deb [arch=amd64] https://download.docker.com/linux/ubuntu ${lsb_release -cs} stable"
```
4. 安裝docker-ce
```bash
sudo apt-get install -y docker-ce
```
5. 查看docker
```bash
docker -v
```
6. docker-compose(其他版本: https://github.com/docker/compose/releases)
```bash
sudo curl -L "https://github.com/docker/compose/releases/download/1.25.4/docker-compose-`uname -s`-`uname -m`" -o /usr/local/bin/docker-compose
```
```bash
sudo curl -L "https://github.com/docker/compose/releases/download/v2.11.0/docker-compose-linux-x86_64" -o /usr/local/bin/docker-compose
```

7. 增加權限
```bash
sudo chmod +x /usr/local/bin/docker-compose
```
8. 查看docker-compose
```bash
docker-compose -v
```

9. 新增sudo user
```bash
sudo adduser alan10607
sudo usermod -aG sudo alan10607
```

10. 新增docker user
```bash
sudo groupadd docker
sudo usermod -aG docker $USER
```

11. 確認user
```bash
su - alan10607
exit
docker run hello-world
```




