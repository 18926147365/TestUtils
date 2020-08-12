import bean.User;
import com.alibaba.fastjson.JSONObject;
import com.google.common.hash.BloomFilter;
import com.google.common.hash.Funnels;
import com.sun.management.GcInfo;
import org.apache.commons.lang.ObjectUtils;
import org.apache.commons.lang.SystemUtils;
import org.apache.commons.lang3.time.StopWatch;
import utils.BloomFilterUtils;
import utils.ThreadExecutorPool;

import java.io.Console;
import java.lang.management.ManagementFactory;
import java.math.BigDecimal;
import java.nio.charset.Charset;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.*;

import com.sun.management.GarbageCollectorMXBean;

import javax.management.MBeanServer;

/**
 * @author 李浩铭
 * @date 2020/7/3 10:28
 * @descroption
 */

public class Test {

    private static volatile GarbageCollectorMXBean gcMBean;

    private static final String GC_BEAN_NAME =
            "java.lang:type=GarbageCollector,name=ConcurrentMarkSweep";


    public static void main(String[] args) throws Exception {
        BloomFilter<Integer> filter=BloomFilterUtils.createOrGetIntger("str",10000,0.01);
        filter.put(123);
        filter.put(1233);
        filter.put(12312);
        System.out.println(filter.approximateElementCount());
        System.out.println(filter.mightContain(1231));


    }



    public static  void testBloom(){
        BloomFilter<CharSequence> filter = BloomFilter.create(Funnels.stringFunnel(Charset.defaultCharset()), 10000000, 0.03F);
        int size=100000;
        for (int i = 0; i < size; i++) {
            filter.put(i+"");
        }
        StopWatch stopWatch=new StopWatch();
        stopWatch.start();

        int t=0;
        for (int i = 0; i <size*10; i++) {
            boolean isex=(filter.mightContain(i+""));
            if(isex){
                t++;
            }
        }
        stopWatch.stop();
        System.out.println("共检索:"+t +"误报个数:"+(t-size) +" 误报率:" +((1-(((double)size)/(double)t))*100)+"%"+" 耗时："+stopWatch.getTime()+"ms");
    }


    static {
        gcMBean = getGCMBean();
    }
    private static GarbageCollectorMXBean getGCMBean() {
        try {
            MBeanServer server = ManagementFactory.getPlatformMBeanServer();
            GarbageCollectorMXBean bean =
                    ManagementFactory.newPlatformMXBeanProxy(server,
                            GC_BEAN_NAME, GarbageCollectorMXBean.class);
            return bean;
        } catch (RuntimeException re) {
            throw re;
        } catch (Exception exp) {
            throw new RuntimeException(exp);
        }
    }
}
