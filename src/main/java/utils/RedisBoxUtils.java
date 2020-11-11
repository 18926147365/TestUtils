package utils;

import bean.BoxReject;
import lombok.extern.slf4j.Slf4j;
import org.checkerframework.checker.units.qual.A;
import org.redisson.Redisson;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * @Description:
 * @Author: lihaoming
 * @Date: 2020/11/4 5:42 下午
 */
@Component
@Slf4j
public class RedisBoxUtils {


    @Autowired
    private RedisLuaUtils redisLuaUtils;


    public RedisBox getNoneBox(String boxName, long boxSize) {
        return getBox(boxName, boxSize, 0, BoxReject.NONE);
    }

    /**
     * 先进先出
     * */
    public RedisBox getFifoBox(String boxName, long boxSize) {
        return getBox(boxName, boxSize, 0, BoxReject.FIFO);
    }

    /**
     * 先进先出
     * @param boxSize 容器大小
     * @param keepAliveTime 存活时长 单位秒（数据第二次读取缓存时，数据存活时长增加对应该值）
     * */
    public RedisBox getFifoBox(String boxName, long boxSize,int keepAliveTime) {
        if(keepAliveTime<=0){
            keepAliveTime=0;
        }
        return getBox(boxName, boxSize, keepAliveTime, BoxReject.FIFO);
    }

    private RedisBox getBox(String boxName, long boxSize, Integer keepAliveTime, BoxReject reject) {
        if (keepAliveTime == null) {
            keepAliveTime = 30;
        }
        return new RedisBox(boxName, boxSize, reject, keepAliveTime, redisLuaUtils);
    }

}
