package utils;

import bean.BoxReject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * @Description:
 * @Author: lihaoming
 * @Date: 2020/11/6 3:13 下午
 */

public class RedisBox {

    private String boxName;

    private Long boxSize;

    private BoxReject reject;

    private Integer keepAliveTime;//当reject为keepAlive时，该参数才生效

    private RedisLuaUtils redisLuaUtils;

    public RedisBox() {
    }

    public RedisBox(String boxName, Long boxSize, BoxReject reject, Integer keepAliveTime, RedisLuaUtils redisLuaUtils) {
        this.boxName = boxName;
        this.boxSize = boxSize;
        this.reject = reject;
        this.keepAliveTime = keepAliveTime;
        this.redisLuaUtils = redisLuaUtils;

    }


    /**
     * @param expire 过期时间（秒）
     */
    public String set(String key, String val, int expire) {
        String result = redisLuaUtils.setBox(boxName, boxSize, key, val, expire, keepAliveTime, reject);
        return result;
    }

    public String get(String key) {
        return redisLuaUtils.get(String.join(":", this.boxName, "v", key));
    }

    public long del(String key) {
        String zkey = String.join(":", this.boxName, "z");
        redisLuaUtils.zrem(zkey, key);
        return redisLuaUtils.del(String.join(":", this.boxName, "v", key));
    }

    /**
     * @param expire 单位秒
     */
    public long expire(String key, int expire) {
        return redisLuaUtils.expire(String.join(":", this.boxName, "v", key), expire);
    }

    public long delBox() {
        return redisLuaUtils.del(String.join(":", this.boxName, "z"));
    }


}
