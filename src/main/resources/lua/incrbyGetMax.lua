--ARGV[1]:判断最大值 ARGV[2]:自增步长 当当前值大于等于最大值时返回-1
local count = tonumber(redis.call('get',KEYS[1]));
local max = tonumber(ARGV[1]);
local incrby = tonumber(ARGV[2]);

if count == nil then
    count = 0;
end
if count >= max or count + incrby > max then
    return -1;
else
    return redis.call('incrby',KEYS[1],incrby);
end
