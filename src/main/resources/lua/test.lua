local art = KEYS[1];
local artSet= KEYS[2];
local contNum = ARGV[1];
local contNumValue = ARGV[2];
local updateDate = ARGV[3];
local updateDateValue = ARGV[4];
local score = ARGV[5];

--Increase art contNum
local res = redis.call("HINCRBY", art, contNum, 1);

--Update art updateDate
redis.call("HMSET", art, updateDate, updateDateValue);

--Move artSet to the head of zSet
redis.call('ZADD',  artSet, score, art);

return res;
