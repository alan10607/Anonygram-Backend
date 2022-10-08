local new = KEYS[1];
local batch = KEYS[2];
local static = KEYS[3];
local is_like = ARGV[1];
local un_like = ARGV[2];

--check is like or not, 1: like, 0: unlike, -1: set not found
if(redis.call('sismember', new, is_like) == 1) then
    return 1;
elseif(redis.call('sismember', new, un_like) == 1) then
    return 0;
elseif(redis.call('sismember', batch, is_like) == 1) then
    return 1;
elseif(redis.call('sismember', batch, un_like) == 1) then
    return 0;
elseif(redis.call('sismember', static, is_like) == 1) then
    return 1;
elseif(redis.call('sismember', static, un_like) == 1) then
    return 0;
else
    return -1;
end