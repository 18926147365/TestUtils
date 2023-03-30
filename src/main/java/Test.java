import bean.BinTree;
import bean.User;
import com.alibaba.fastjson.JSONObject;
import com.google.common.base.Stopwatch;
import com.google.common.hash.BloomFilter;
import com.google.common.hash.Funnels;
import com.sun.management.GcInfo;
import javassist.*;
import javassist.bytecode.MethodInfo;
import org.apache.commons.lang.ObjectUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.SystemUtils;
import org.apache.commons.lang3.time.StopWatch;
import org.checkerframework.checker.units.qual.C;
import utils.*;

import java.io.Console;
import java.lang.management.ManagementFactory;
import java.math.BigDecimal;
import java.nio.charset.Charset;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.*;
import java.util.stream.Collectors;

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
        System.out.println("12312312222222");
        System.out.println("123测123试1231112");
    }















    public static void httpMarket(){

        String url="http://10.191.8.120:18803/market/json/mkt_activity_order/startOrderActivity?token=a::159894032CA94F04B9A05951CBBCA03B&sign=100";

        ExecutorService pool = new ThreadPoolExecutor(20, 20, 5000,
                TimeUnit.MILLISECONDS,
                new LinkedBlockingQueue<>(),
                Executors.defaultThreadFactory(),
                new ThreadPoolExecutor.AbortPolicy());
        ((ThreadPoolExecutor) pool).allowCoreThreadTimeOut(true);
        CountDownLatch countDownLatch=new CountDownLatch(1);

        StopWatch stopWatch=new StopWatch();
        stopWatch.start();
        for (int i = 0; i < 10; i++) {
            final int k=i;
            pool.submit(new Runnable() {
                @Override
                public void run() {
                    Map<String,String> map=new HashMap<>();
                    String jsonStr="{\"orderType\":\"950013\",\"usedCouIsDist\":\"true\",\"promoteIsDist\":\"false\",\"requestOrderModels\":\"[{\\\"combinationPayMode\\\":\\\"071His_6202\\\"},{\\\"combinationPayMode\\\":\\\"071His_64\\\"}]\",\"telephone\":\"15813811408\",\"distributionChannel\":\"1981\",\"orderProductList\":\"[{\\\"proCode\\\":\\\"35339\\\",\\\"categoryCode\\\":\\\"830906\\\",\\\"salePrice\\\":234.6700,\\\"num\\\":1.0000,\\\"amount\\\":234.67}]\",\"orderNo\":\"2020090571921314417_44030886106002009080028\",\"storeConsumeAmount\":\"234.6700\",\"tradeTime\":\"2020-09-08 07:38:54.000\",\"storeOuCode\":\"100043024010013\",\"userId\":\"12385922\",\"userLevel\":\"1001\",\"acceptTime\":\"2020-09-08 07:39:05.450\",\"birthday\":\"\",\"payTime\":\"2020/9/5 8:48:31\",\"tradeNode\":\"4403088\",\"networkType\":\"001001001\"}";
                    map= (Map<String, String>) JSONObject.parse(jsonStr);
                    map.put("occurOuCode","2001150");
                    String orderNo="2020091071921314416_44030886106002031"+k;
                    map.put("orderNo",orderNo);
                    System.out.println(HttpClientUtil.doPost(url, map,null));
                    countDownLatch.countDown();
                }
            });

        }
        try {
            countDownLatch.await();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        stopWatch.stop();
        System.out.println(stopWatch.getTime());
    }


    //布隆过滤器例子2
    public static void testBloom2(){

        String key="123123";
        BloomFilter<String> bloomFilter=BloomFilterUtils.createOrGetString(key,1000000,0.03);
        //提前将10W白名单插入过滤器中
        for (int i = 0; i < 100000; i++) {
            bloomFilter.put(i+"");
        }


        //100W访问，判断是否有白名单用户
        int f=0;
        for (int i = 0; i < 1000000; i++) {
            int rand=(int)(Math.random()*10000000)+500;
            if(bloomFilter.mightContain(rand+"")){
                if(rand>100000){
                    f++;
                    System.err.println(rand);
                }
            }
        }
        System.out.println(f);
    }

    public static  void testBloom(){
        BloomFilter<CharSequence> filter = BloomFilter.create(Funnels.stringFunnel(Charset.defaultCharset()), 10000000, 0.03F);
        int size=1000000;
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
//        gcMBean = getGCMBean();
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
