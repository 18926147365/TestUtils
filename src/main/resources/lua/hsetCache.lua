--
-- Created by IntelliJ IDEA.
-- User: 李浩铭
-- Date: 2020/8/10
-- Time: 10:32
--
--

redis.call('hset',KEYS[1],'state','OK');
redis.call('hset',KEYS[1],'value',ARGV[2]);
redis.call('hset',KEYS[1],'expireDate',ARGV[1]);
redis.call('hset',KEYS[1],'queryTimeout',ARGV[3]);
return 1;