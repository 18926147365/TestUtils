package utils;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.ObjectUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
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


    public enum ScriptLoadEnum {
        BATCHSET("/lua/batchSet.lua"),
        GETANDDEL("/lua/getAndDel.lua"),
        SECKILL("/lua/incrbyAndGet.lua"),
        INCRBYGETMAX("/lua/incrbyGetMax.lua");

        private final String path;

        ScriptLoadEnum(String path) {
            this.path = path;
        }

        String getPath() {
            return path;
        }

    }

    @Autowired
    private JedisPool jedisPool;


    private static final Map<ScriptLoadEnum, String> SCRIPTLOADMAP = new ConcurrentHashMap<>();

    public String loadScript(ScriptLoadEnum scriptLoadEnum) {

        if (SCRIPTLOADMAP.containsKey(scriptLoadEnum)) {
            return SCRIPTLOADMAP.get(scriptLoadEnum);
        }
        synchronized (SCRIPTLOADMAP) {
            if (SCRIPTLOADMAP.containsKey(scriptLoadEnum)) {
                return SCRIPTLOADMAP.get(scriptLoadEnum);
            }
            Jedis jedis = null;
            try (InputStream input = RedisLuaUtils.class.getResourceAsStream(scriptLoadEnum.path)) {
                jedis = jedisPool.getResource();
                String scriptLoad = jedis.scriptLoad(IOUtils.toString(input, StandardCharsets.UTF_8));
                SCRIPTLOADMAP.put(scriptLoadEnum, scriptLoad);
                return scriptLoad;
            } catch (Exception e) {
                e.printStackTrace();
                return null;
            } finally {
                if (jedis != null) {
                    jedis.close();
                }
            }
        }

    }


    public <T extends Comparable> T  evalsha(ScriptLoadEnum scriptLoadEnum, Class<T> classz, String... params) {
        if(classz!=Long.class && classz!=String.class && classz!=Boolean.class){
            throw new RuntimeException("不支持的类型class:"+classz);
        }
        Object obj=evalsha(scriptLoadEnum,1,params);
        if(obj==null){
            if(classz==Boolean.class){
                return (T) Boolean.valueOf(false);
            }
            return null;
        }
        if(classz==Boolean.class){
            if(ObjectUtils.equals(obj,1l) || ObjectUtils.equals(obj,1)){
                return (T) Boolean.valueOf(true);
            }else {
                return (T) Boolean.valueOf(false);
            }
        }
        return (T)obj;
    }
    public Object evalsha(ScriptLoadEnum scriptLoadEnum, int keyCount, String... params) {
        Jedis jedis = null;
        try {
            jedis = jedisPool.getResource();
            return jedis.evalsha(loadScript(scriptLoadEnum), keyCount, params);
        } finally {
            if (jedis != null) {
                jedis.close();
            }
        }
    }

    public boolean set(String key,String value){
        Jedis jedis=jedisPool.getResource();
        try {
            if("OK".equals(jedis.set(key,value))){
                return true;
            }
            return false;
        } finally {
            jedis.close();
        }
    }

    public long incr(String key){
        return incrBy(key,1l);
    }

    public String get(String key){
        Jedis jedis=jedisPool.getResource();
        try {
            return jedis.get(key);
        } finally {
            jedis.close();
        }
    }

    public long incrBy(String key,Long value){
        if(value==null){
            value=1l;
        }
        Jedis jedis=jedisPool.getResource();
        try {
            return jedis.incrBy(key,value);
        } finally {
            jedis.close();
        }
    }
    private static Map<String, Thread> threadMap = new ConcurrentHashMap<>();


    public boolean tryLock(String key, long timeout) {
        try {
            while (!"OK".equals(setnx(key, "1", timeout))) {
                Thread.sleep(80);
            }
            Thread thread = new Thread(new Runnable() {
                @Override
                public void run() {
                    while (true) {
                        try {
                            Thread.sleep(Double.valueOf(timeout*0.7).longValue());
                        } catch (InterruptedException e) {
                            break;
                        }
                        //TODO 若这里网络请求延迟，还是会出现并发问题
                        pexpire(key,timeout);
                    }
                }

            });
            threadMap.put(key, thread);
            thread.setDaemon(true);
            thread.start();

        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return true;
    }


    /**
     *
     * */
    public String setnx(String key, String value, long timeout) {
        Jedis jedis = null;
        try {
            jedis = jedisPool.getResource();
            String result = jedis.set(key, value, "NX", "PX", timeout);
            if (StringUtils.isBlank(result)) {
                return "0";
            }
            return result;
        } finally {
            jedis.close();
        }
    }

    public boolean pexpire(String key, long timeouts) {
        Jedis jedis = null;
        try {
            jedis = jedisPool.getResource();
            if (jedis.pexpire(key, timeouts) > 0) {
                return true;
            }
        } finally {
            if (jedis != null) {
                jedis.close();
            }
        }
        return false;


    }

    public boolean unLock(String key) {
        Jedis jedis = null;
        try {
            jedis = jedisPool.getResource();
            if (threadMap.containsKey(key)) {
                threadMap.get(key).interrupt();
                threadMap.remove(key);
            }
            if (jedis.del(key) > 0) {
                return true;
            }
            return false;
        } finally {
            if (jedis != null) {
                jedis.close();
            }
        }

    }


}
