package utils;

import org.apache.commons.io.IOUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;

import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author 李浩铭
 * @date 2020/7/31 15:22
 * @descroption
 */
@Component
public class RedisLuaUtils {


    public  enum  ScriptLoadEnum{
        BATCHSET("/lua/batchSet.lua"),
        GETANDDEL("/lua/getAndDel.lua");

        private final String path;
        ScriptLoadEnum(String path){
            this.path=path;
        }

        String getPath(){
            return path;
        }

    }

    @Autowired
    private JedisPool jedisPool;




    private static final Map<ScriptLoadEnum,String> SCRIPTLOADMAP=new ConcurrentHashMap<>();

    public String loadScript(ScriptLoadEnum scriptLoadEnum){

        if(SCRIPTLOADMAP.containsKey(scriptLoadEnum)){
            return SCRIPTLOADMAP.get(scriptLoadEnum);
        }
        synchronized (SCRIPTLOADMAP){
            if(SCRIPTLOADMAP.containsKey(scriptLoadEnum)){
                return SCRIPTLOADMAP.get(scriptLoadEnum);
            }
            Jedis jedis=null;
            try (InputStream input = RedisLuaUtils.class.getResourceAsStream("/lua/getAndDel.lua")) {
                jedis=jedisPool.getResource();
                String scriptLoad=jedis.scriptLoad(IOUtils.toString(input, StandardCharsets.UTF_8));
                SCRIPTLOADMAP.put(scriptLoadEnum,scriptLoad);
                return scriptLoad;
            }catch (Exception e){
                e.printStackTrace();
                return null;
            }finally {
                if(jedis!=null){
                    jedis.close();
                }
            }
        }

    }

    public Object evalsha(ScriptLoadEnum scriptLoadEnum, int keyCount, String... params){
        Jedis jedis=null;
        try {
            jedis=jedisPool.getResource();
            return  jedis.evalsha(loadScript(scriptLoadEnum),keyCount,params);
        } finally {
            if(jedis!=null){
                jedis.close();
            }
        }
    }



}
