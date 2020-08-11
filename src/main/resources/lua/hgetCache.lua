--
-- Created by IntelliJ IDEA.
-- User: 李浩铭
-- Date: 2020/8/10
-- Time: 10:32
--
--


local state = redis.call("hget",KEYS[1],'state'); --QUERYING：查询中 OK：正常 EXPIRED：无效的、过期的
local expireDate = redis.call("hget",KEYS[1],'expireDate');
local queryTimeout = redis.call("hget",KEYS[1],'queryTimeout');
local value = redis.call("hget",KEYS[1],'value');
local timestamp=tonumber(ARGV[2]);

if state == false or value == false or expireDate == false  or queryTimeout ==false then
    if state == false then
        redis.call("hset",KEYS[1],'state','EXPIRED');
    end
    if  value == false  then
        redis.call("hset",KEYS[1],'value',ARGV[1]);
    end
    if  expireDate == false  then
        redis.call("hset",KEYS[1],'expireDate',timestamp);
    end
    if  queryTimeout == false  then
        redis.call("hset",KEYS[1],'queryTimeout',timestamp);
    end
    return 'EXPIRED:'..ARGV[1]   --返回默认值
end




if state == 'OK' then
    if  timestamp > tonumber(expireDate) then --判断是否超过有效时间
        --若超过缓存有效期
        redis.call("hset",KEYS[1],'state','EXPIRED');
        redis.call("hset",KEYS[1],'expireDate',expireDate+1000);
        return 'EXPIRED:'..value;
    else
        return 'OK:'..value;
    end

elseif state == 'EXPIRED' then --判断状态是否为无效的
    return 'EXPIRED:'..value;

elseif state == 'QUERYING' then

    if  timestamp > tonumber(queryTimeout) then --判断查询是否超时
        redis.call("hset",KEYS[1],'state','EXPIRED');
        redis.call("hset",KEYS[1],'queryTimeout',queryTimeout+1000);
        return 'EXPIRED:'..value;
    else
        return 'QUERYING:'..value;--判断状态是否为查询中
    end

else
    return 'EXPIRED:',value;
end