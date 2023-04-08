package service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import utils.RedisLuaUtils;

/**
 * @author lihaoming
 * @date 2023/4/8 16:55
 * @description
 */
@Service
public class GsRedisService {

    static final String GS_KEY = "gs";

    @Autowired
    private RedisLuaUtils redisLuaUtils;


    public String get(String key) {
        return redisLuaUtils.get(getGsKey(key));
    }

    public boolean set(String key,String value) {
        return redisLuaUtils.set(getGsKey(key),value);
    }

    private String getGsKey(String key){
        return GS_KEY + ":" + key;
    }
}
