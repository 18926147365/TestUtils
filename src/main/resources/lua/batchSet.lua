
local jsonstr = KEYS[2]
print(jsonstr)
local json_data = cjson.decode(jsonstr)
for i,v in pairs(json_data) do
    redis.call('set','testnames'..v,v)
end
