--
-- Created by IntelliJ IDEA.
-- User: 李浩铭
-- Date: 2021/02/08
-- Time: 15:55
-- set bit偏移量值对应二进制值，
-- 若值已为"1" 则返回 0
-- 若值为"0" 则设置为"1" 且 返回1

local val = redis.call('getbit',KEYS[1],ARGV[1]);
if val == 0 then
    redis.call('setbit',KEYS[1],ARGV[1],"1");
    return 1;
end
return 0;
