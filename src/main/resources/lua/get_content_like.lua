local new_key = KEYS[1];
local static_key = KEYS[2];

local function sum_result(check_key, key_result)
    if(redis.call('get', check_key) == '1') then
        return key_result + 1;
    elseif(redis.call('get', check_key) == '0') then
        return key_result + 0;
    end
end

-- Check is there any like status
-- -1: not exist
-- 0: static_key dislike
-- 1: static_key like
-- 2: new_key dislike
-- 3: new_key like
if(redis.call('exists', new_key) == 1) then
    return sum_result(new_key, 2);
elseif(redis.call('exists', static_key) == 1) then
    return sum_result(static_key, 0);
else
    return -1;
end
