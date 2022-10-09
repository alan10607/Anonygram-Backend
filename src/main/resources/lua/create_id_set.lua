local id_set = KEYS[1];
local id = ARGV[1];
local score = tonumber(ARGV[2]);
local max_count = tonumber(ARGV[3]);

local res = redis.call('zadd', id_set, score, id);
local count = redis.call('zcard', id_set);

--check need zpopmax or not
if(res == 1 and count > max_count) then
    redis.call('zpopmax', id_set, 1);
    return 1;
else
    return 0;
end