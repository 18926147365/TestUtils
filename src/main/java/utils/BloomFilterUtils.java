package utils;

import com.google.common.hash.BloomFilter;
import com.google.common.hash.Funnel;
import com.google.common.hash.Funnels;

import java.nio.charset.Charset;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author 李浩铭
 * @date 2020/8/12 17:43
 * @descroption 布隆过滤器工具类
 */
public class BloomFilterUtils {
    private static final Map<String, BloomFilter> bloomFilterMap = new ConcurrentHashMap<>();



    public static <T> BloomFilter<T> createOrGet(String key, Funnel<? super T> funnel, int expectedInsertions, double fpp){
        if(bloomFilterMap.containsKey(key)){
            return bloomFilterMap.get(key);
        }
        synchronized (bloomFilterMap){
            if(bloomFilterMap.containsKey(key)){
                return bloomFilterMap.get(key);
            }
            BloomFilter<T> filter = BloomFilter.create(funnel,expectedInsertions,fpp);
            bloomFilterMap.put(key,filter);
            return filter;

        }
    }

    public static  BloomFilter<String> createOrGetString(String key, int expectedInsertions, double fpp){
        return createOrGet(key,Funnels.stringFunnel(Charset.defaultCharset()),expectedInsertions,fpp);
    }

    public static  BloomFilter<Integer> createOrGetIntger(String key, int expectedInsertions, double fpp){
        return createOrGet(key,Funnels.integerFunnel(),expectedInsertions,fpp);
    }

}
