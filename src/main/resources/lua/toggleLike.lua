local new = KEYS[1];
local batch = KEYS[2];
local static = KEYS[3];
local isTarget = ARGV[1];
local unTarget = ARGV[2];
local function update()
    redis.call("srem", new, unTarget);
    redis.call("sadd", new, isTarget);
    return 1;
end

--check is already be target or not
if(redis.call('sismember', new, isTarget) == 1) then
    return 0;
elseif(redis.call('sismember', new, unTarget) == 1) then
    return update();
elseif(redis.call('sismember', batch, isTarget) == 1) then
    return 0;
elseif(redis.call('sismember', batch, unTarget) == 1) then
    return update();
elseif(redis.call('sismember', static, isTarget) == 1) then
    return 0;
elseif(redis.call('sismember', static, unTarget) == 1) then
    return update();
else
    return -1;
end
