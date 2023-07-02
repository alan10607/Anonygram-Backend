local new_key = KEYS[1];
local static_key = KEYS[2];

-- Check is there any like status in new_key or static_key
-- 1: like, 0: dislike
if(redis.call('get', new_key) == '1') then
    return 1;
elseif(redis.call('get', static_key) == '1') then
    return 1;
else
    return 0;
end