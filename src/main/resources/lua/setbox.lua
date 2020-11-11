local box_name = KEYS[1];
local box_size = ARGV[1];
local key = ARGV[2];
local val = ARGV[3];
local expire = ARGV[4];
local keep_alive_time = ARGV[5];
local reject = ARGV[6];
local zkey = box_name .. ":z";
local vkey = box_name .. ":v:" .. key;
local date = redis.call('TIME');
local time = (date[1]) + (date[2] / 1000 / 1000);


local zval = redis.call("zscore", zkey, key);
if zval == false then --判断该值是否存在
    local size = redis.call("zcard", zkey);

    if tonumber(box_size) < (size + 1) then --超过盒子容量时，执行溢出处理策略
        if reject == "NONE" then --不做处理
            return "full";
        end

        if reject == "FIFO" then --先进先出
            local ztable = redis.call("zrange", zkey, 0, 0);
            local fifoKey = ztable[1];
            if fifoKey ~= nil then
                redis.call("del", box_name .. ":v:" .. fifoKey);
            end
            redis.call("zremrangeByRank", zkey, 0, 0);
        end
    end

    size = redis.call("zcard", zkey);
    if tonumber(box_size) < (size + 1) then
        return "full";
    end
    redis.call("zadd", zkey, time, key);

else

    if reject == "FIFO" then --先进先出
        redis.call("zadd", zkey, time + tonumber(keep_alive_time), key);
    end
end

redis.call("set", vkey, val);
redis.call("expire", vkey, tonumber(expire));
return "ok"
