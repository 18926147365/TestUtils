package utils;

import bean.BoxReject;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.ObjectUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang3.time.StopWatch;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import redis.clients.jedis.*;
import redis.clients.jedis.params.sortedset.ZIncrByParams;

import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Supplier;

/**
 * @author 李浩铭
 * @date 2020/7/31 15:22
 * @descroption
 */
@Component
@Slf4j
public class RedisLuaUtils {


    public enum ScriptLoadEnum {
        BATCHSET("/lua/batchSet.lua"),
        TEST("/lua/test.lua"),
        GETANDDEL("/lua/getAndDel.lua"),
        SECKILL("/lua/incrbyAndGet.lua"),
        INCRBYGETMAX("/lua/incrbyGetMax.lua"),
        HSETCACHE("/lua/hsetCache.lua"),
        HGETCACHE("/lua/hgetCache.lua"),
        HSETFIELDNX("/lua/hsetFieldnx.lua"),
        GETREDISTIME("/lua/getRedisTime.lua"),
        SETBOX("/lua/setbox.lua");

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

    /**
     * 插入成功返回 ok
     * 容器满时返回 full
     * */
    public String setBox(String boxName,Long boxSize,String key,String val,Integer expire,Integer keepAliveTime,BoxReject boxReject){
        String evaResult = evalsha(ScriptLoadEnum.SETBOX, String.class, boxName, boxSize.toString(), key,val,expire.toString(),keepAliveTime.toString(),boxReject.name());
        return evaResult;
    }

    public boolean setbit(String key,long offset,String value){
        Jedis jedis = jedisPool.getResource();
        try {
            return jedis.setbit(key,offset,value);
        } finally {
            jedis.close();
        }
    }

    public void tests(){
        String key="bitmapTests-1";
        String key1="bitmapTests-2";
        String key2="bitmapTests-3";
        Jedis jedis = jedisPool.getResource();
        try {
        } finally {
            jedis.close();
        }
    }


    public boolean getBit(String key){
        Jedis jedis = jedisPool.getResource();
        try {
            return jedis.getbit(key,1l);
        } finally {
            jedis.close();
        }
    }
    public boolean getBit(String key,long offset){
        Jedis jedis = jedisPool.getResource();
        try {


            return jedis.getbit(key,offset);
        } finally {
            jedis.close();
        }
    }
    public boolean setbit(String key,String value){
        return setbit(key,1l, value);
    }

    public ScanResult<Map.Entry<String, String>> hscan(String key, String cursor, String pattern){
        Jedis jedis = jedisPool.getResource();
        try {
            ScanParams scanParams=new ScanParams();
            scanParams.match(pattern);
            scanParams.count(10);
            return jedis.hscan(key,cursor,scanParams);
        } finally {
            jedis.close();
        }
    }

    public ScanResult<String> scan(String cursor, String pattern){
        return scan(cursor, pattern,10);
    }

    public ScanResult<String> scan(String cursor, String pattern,int count){
        Jedis jedis = jedisPool.getResource();
        try {
            ScanParams scanParams=new ScanParams();
            scanParams.match(pattern);
            scanParams.count(count);
            return  jedis.scan(cursor,scanParams);
        } finally {
            jedis.close();
        }
    }
    public Set<String> keys(String pattern){
        Jedis jedis = jedisPool.getResource();
        try {
             return jedis.keys(pattern);
        } finally {
            jedis.close();
        }
    }

    public boolean getbit(String key){
        Jedis jedis = jedisPool.getResource();
        try {

            return jedis.getbit(key,1l);
        } finally {
            jedis.close();
        }
    }
    public long del(String key){
        Jedis jedis = jedisPool.getResource();
        try {
            return jedis.del(key);
        } finally {
            jedis.close();
        }
    }
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


    public <T extends Comparable> T evalsha(ScriptLoadEnum scriptLoadEnum, Class<T> classz, String... params) {
        if (classz != Long.class && classz != String.class && classz != Boolean.class) {
            throw new RuntimeException("不支持的类型class:" + classz);
        }
        Object obj = evalsha(scriptLoadEnum, 1, params);
        if (obj == null) {
            if (classz == Boolean.class) {
                return (T) Boolean.valueOf(false);
            }
            return null;
        }
        if (classz == Boolean.class) {
            if (ObjectUtils.equals(obj, 1l) || ObjectUtils.equals(obj, 1)) {
                return (T) Boolean.valueOf(true);
            } else {
                return (T) Boolean.valueOf(false);
            }
        }
        return (T) obj;
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

    public void scriptKill(){
        Jedis jedis = null;
        try {
            jedis = jedisPool.getResource();
            jedis.scriptKill();
        } finally {
            if (jedis != null) {
                jedis.close();
            }
        }
    }

    public boolean set(String key, String value) {
        Jedis jedis = jedisPool.getResource();
        try {
            if ("OK".equals(jedis.set(key, value))) {
                return true;
            }
            return false;
        } finally {
            jedis.close();
        }
    }

    public Long hsetFieldnx(String key, String field, String ifVal, String val, Long queryTimeout) {
        return evalsha(ScriptLoadEnum.HSETFIELDNX, Long.class, key, field, ifVal, val, queryTimeout + "");
    }



    public Long hset(String key, String field, String val) {
        Jedis jedis = jedisPool.getResource();
        try {
            return jedis.hset(key, field, val);
        } finally {
            jedis.close();
        }
    }

    public String hget(String key, String field) {
        Jedis jedis = jedisPool.getResource();

        try {
            return jedis.hget(key, field);
        } finally {
            jedis.close();
        }
    }


    public Long pfadd(String key, String... elements) {
        Jedis jedis = jedisPool.getResource();
        try {
            return jedis.pfadd(key,elements);
        } finally {
            jedis.close();
        }
    }
    public Long pfcount(String key) {
        Jedis jedis = jedisPool.getResource();
        try {
            return jedis.pfcount(key);
        } finally {
            jedis.close();
        }
    }

    public Long getRedisTime() {
        return (Long) evalsha(ScriptLoadEnum.GETREDISTIME, 0);
    }


    public String hgetCache(String key, String defalutVal, Supplier<String> supplier) {
        return hgetCache(key, defalutVal, supplier, 5000, 3000);
    }

    public String hgetCache(String key, String defalutVal, Supplier<String> supplier, Long expireTime) {
        return hgetCache(key, defalutVal, supplier, expireTime, 3000);
    }

    /**
     * 缓存获取
     *
     * @param key
     * @param defalutVal   默认值(初始值)
     * @param supplier     查询方法体
     * @param expireTime   缓存过期有效时间(ms) 默认 5000ms
     * @param queryTimeOut 查询超时时间(ms) 默认3000ms 该值建议为查询方法体平均查询时间的1.3倍
     */
    public String hgetCache(String key, String defalutVal, Supplier<String> supplier, long expireTime, long queryTimeOut) {


        String evaResult = evalsha(ScriptLoadEnum.HGETCACHE, String.class, key, defalutVal, getRedisTime() + "");
        if (StringUtils.isBlank(evaResult)) {
            return defalutVal;
        }
        int indexOf = evaResult.indexOf(":");
        String state = evaResult.substring(0, indexOf);
        String val = evaResult.substring(indexOf + 1, evaResult.length());
        if ("OK".equals(state.toUpperCase())) {
            return val;
        } else if ("EXPIRED".equals(state.toUpperCase())) {
            if (hsetFieldnx(key, "state", "EXPIRED", "QUERYING", getRedisTime() + queryTimeOut) == 1) {

                //为了保证永远只有一条线程去执行查询体方法，这里使用一个守护线程进行状态维护
                Thread dThread = new Thread(new Runnable() {
                    @Override
                    public void run() {
                        while (true) {
                            hset(key, "queryTimeout", (getRedisTime() + queryTimeOut) + "");
                            try {
                                Thread.sleep(500);
                            } catch (InterruptedException e) {
                                break;
                            }
                        }
                    }
                });
                dThread.setDaemon(true);
                dThread.start();
                Thread thread = new Thread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            StopWatch watch = new StopWatch();
                            watch.start();
                            log.info("hgetCache方法体执行查询开始thread{}---执行方法体:{}",
                                    Thread.currentThread().getId(),
                                    supplier.getClass().getName());

                            String value = supplier.get();
                            dThread.interrupt();//维护状态的守护线程停止
                            evalsha(ScriptLoadEnum.HSETCACHE, String.class, key, (getRedisTime() + expireTime) + "", value);

                            log.info("hgetCache方法体执行查询结束thread{}---执行方法体:{}  耗时:{}ms  返回值:{}",
                                    Thread.currentThread().getId(),
                                    supplier.getClass().getName(),
                                    watch.getTime(), value);
                        }catch (Exception e){
                            dThread.interrupt();//维护状态的守护线程停止
                            e.printStackTrace();
                            log.error("hgetCache方法体执行异常thread{}：异常信息:{}",Thread.currentThread().getId(),e);
                        }

                    }
                });
                thread.setDaemon(true);
                thread.start();


            }
            return val;
        } else if ("QUERYING".equals(state.toUpperCase())) {
            return val;
        }

        return defalutVal;
    }


    public void hset(String key,String field,String val,int expire){
        Jedis jedis = jedisPool.getResource();
        try {
           jedis.hset(key,field,val);
           jedis.expire(key,expire);
        } finally {
            jedis.close();
        }
    }
    /**
     * seconds 秒
     * */
    public boolean set(String key, String value, int expire) {
        Jedis jedis = jedisPool.getResource();
        try {
            if ("OK".equals(jedis.setex(key, expire, value))) {
                return true;
            }
            return false;
        } finally {
            jedis.close();
        }
    }

    public Long hsetnx(String key, String filed, String value) {
        Jedis jedis = jedisPool.getResource();
        try {
            return jedis.hsetnx(key, filed, value);
        } finally {
            jedis.close();
        }
    }

    public long incr(String key) {
        return incrBy(key, 1l);
    }

    public String get(String key) {
        Jedis jedis = jedisPool.getResource();
        try {
            return jedis.get(key);
        } finally {
            jedis.close();
        }
    }
    /**
     *
     * */
    public Double zincrby(String key,double score,String  member) {
        Jedis jedis = null;
        try {
            jedis = jedisPool.getResource();

            return jedis.zincrby(key, score, member);
        } finally {
            jedis.close();
        }
    }

    /**
     *
     * */
    public long zrem(String key,String ... rems) {
        Jedis jedis = null;
        try {
            jedis = jedisPool.getResource();
            return jedis.zrem(key,rems);
        } finally {
            jedis.close();
        }
    }
    /**
     *
     * */
    public Long zcard(String key) {
        Jedis jedis = null;
        try {
            jedis = jedisPool.getResource();
            return jedis.zcard(key);
        } finally {
            jedis.close();
        }
    }
    /**
     *
     * */
    public long hIncrby(String key,String field,long value) {
        Jedis jedis = null;
        try {
            jedis = jedisPool.getResource();
            return jedis.hincrBy(key, field, value);
        } finally {
            jedis.close();
        }
    }
    public long incrBy(String key, Long value) {
        if (value == null) {
            value = 1l;
        }
        Jedis jedis = jedisPool.getResource();
        try {
            return jedis.incrBy(key, value);
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
                            Thread.sleep(Double.valueOf(timeout * 0.7).longValue());
                        } catch (InterruptedException e) {
                            break;
                        }
                        //TODO 若这里网络请求延迟，还是会出现并发问题
                        pexpire(key, timeout);
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
    public long expire(String key, int expire) {
        Jedis jedis = null;
        try {
            jedis = jedisPool.getResource();
          return jedis.expire(key,expire);
        } finally {
            if (jedis != null) {
                jedis.close();
            }
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
            if (threadMap.containsKey(key)) {
                threadMap.get(key).interrupt();
                threadMap.remove(key);
            }
        }

    }


}
