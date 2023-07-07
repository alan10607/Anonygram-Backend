local new_key = KEYS[1];
local static_key = KEYS[2];
local like_status = ARGV[1]; -- 1: like, 0: dislike

local function update(check_key)
    if(redis.call('get', check_key) == like_status) then
        return 0;
    else
        redis.call('set', new_key, like_status);
        return 1;
    end
end

-- Check if it is already the target status or not
-- 1: succeeded, 0: no change, -1: key not exist
if(redis.call('exists', new_key) == 1) then
    return update(new_key);
elseif(redis.call('exists', static_key) == 1) then
    return update(static_key);
else
    redis.call('set', new_key, like_status);
    return -1;
end
