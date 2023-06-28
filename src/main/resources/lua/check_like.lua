local new_key = KEYS[1];
local batch_key = KEYS[2];
local static_key = KEYS[3];
local user_id = ARGV[1];
local is_like = user_id .. ':1';
local un_like = user_id .. ':0';

--check is like or not, 1: like, 0: unlike, -1: set not found
if(redis.call('sismember', new_key, is_like) == 1) then
    return 1;
elseif(redis.call('sismember', new_key, un_like) == 1) then
    return 0;
elseif(redis.call('sismember', batch_key, is_like) == 1) then
    return 1;
elseif(redis.call('sismember', batch_key, un_like) == 1) then
    return 0;
elseif(redis.call('sismember', static_key, is_like) == 1) then
    return 1;
elseif(redis.call('sismember', static_key, un_like) == 1) then
    return 0;
else
    return -1;
end