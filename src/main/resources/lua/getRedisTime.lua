--
-- Created by IntelliJ IDEA.
-- User: 李浩铭
-- Date: 2020/8/10
-- Time: 17:18
-- To change this template use File | Settings | File Templates.
--
local date=redis.call('TIME')
return date[1]*1000 + date[2]/1000;

