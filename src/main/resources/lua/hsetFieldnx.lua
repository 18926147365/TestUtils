--
-- Created by IntelliJ IDEA.
-- User: 李浩铭
-- Date: 2020/8/10
-- Time: 14:22
-- To change this template use File | Settings | File Templates.
--
local val = redis.call("hget",KEYS[1],ARGV[1]);
if val == false or val == ARGV[2] then
    redis.call("hset",KEYS[1],ARGV[1],ARGV[3]);
    redis.call("hset",KEYS[1],'queryTimeout',ARGV[4])
    return 1;
else
    return 0;
end


