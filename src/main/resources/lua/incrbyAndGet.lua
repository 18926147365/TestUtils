--[[  当计数器值少于0时则返回为-1 可用场景：秒杀等 ]]--
local total = tonumber(redis.call('get',KEYS[1]))
if total == nil or total <=0  then
    return -1
else
    return redis.call('incrby',KEYS[1],ARGV[1])
end
