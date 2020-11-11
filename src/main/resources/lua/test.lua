local keys = KEYS[1]

redis.call("zremrangebyrank", keys, 1, 1);
local zkey = "testbox:z";
local key = "name:110";
local zval = redis.call("zscore", zkey, key);
print(zval);
