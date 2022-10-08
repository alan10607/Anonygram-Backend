local new = KEYS[1];
local batch = KEYS[2];
local static = KEYS[3];
local is_target = ARGV[1];
local un_target = ARGV[2];
local function update()
    redis.call("srem", new, un_target);
    redis.call("sadd", new, is_target);
    return 1;
end

--check is already be target or not, 1: succeeded, 0: no change, -1: set not found
if(redis.call('sismember', new, is_target) == 1) then
    return 0;
elseif(redis.call('sismember', new, un_target) == 1) then
    return update();
elseif(redis.call('sismember', batch, is_target) == 1) then
    return 0;
elseif(redis.call('sismember', batch, un_target) == 1) then
    return update();
elseif(redis.call('sismember', static, is_target) == 1) then
    return 0;
elseif(redis.call('sismember', static, un_target) == 1) then
    return update();
else
    return -1;
end