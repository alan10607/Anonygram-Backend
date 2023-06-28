local new_key = KEYS[1];
local batch_key = KEYS[2];
local static_key = KEYS[3];
local user_id = ARGV[1];
local like_status = ARGV[2];
local is_target = (like_status == 1) and (user_id .. ":1") or (user_id .. ":0");
local un_like = (like_status == 1) and (user_id .. ":0") or (user_id .. ":1");

local function update()
    redis.call("srem", new_key, un_target);
    redis.call("sadd", new_key, is_target);
    return 1;
end

--check is already be target or not, 1: succeeded, 0: no change, -1: set not found
if(redis.call('sismember', new_key, is_target) == 1) then
    return 0;
elseif(redis.call('sismember', new_key, un_target) == 1) then
    return update();
elseif(redis.call('sismember', batch_key, is_target) == 1) then
    return 0;
elseif(redis.call('sismember', batch_key, un_target) == 1) then
    return update();
elseif(redis.call('sismember', static_key, is_target) == 1) then
    return 0;
elseif(redis.call('sismember', static_key, un_target) == 1) then
    return update();
else
    return -1;
end