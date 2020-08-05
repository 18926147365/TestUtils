--[[  批量set数据 ]]--
local jsonstr = KEYS[2]
local json_data = cjson.decode(jsonstr)
for i,v in pairs(json_data) do
    redis.call('set','testnames'..v,v)
end
