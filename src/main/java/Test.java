import bean.BinTree;
import bean.IModel;
import bean.User;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.google.common.base.Stopwatch;
import com.google.common.hash.BloomFilter;
import com.google.common.hash.Funnels;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.StopWatch;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import utils.*;

import java.io.*;
import java.lang.management.ManagementFactory;
import java.net.URLEncoder;
import java.nio.charset.Charset;
import java.util.*;
import java.util.concurrent.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.sun.management.GarbageCollectorMXBean;

import javax.management.MBeanServer;

/**
 * @author 李浩铭
 * @date 2020/7/3 10:28
 * @descroption
 */

public class Test {

    private static String REGEX_CHINESE = "[\u4e00-\u9fa5]";// 中文正则
    private static volatile GarbageCollectorMXBean gcMBean;

    private static final String GC_BEAN_NAME =
            "java.lang:type=GarbageCollector,name=ConcurrentMarkSweep";

    public static void main(String[] args) throws Exception {
        System.out.println("123");
    }


    private static List<String> test1() throws Exception {
        String  url = "http://www.baidu.com/s?wd="+ URLEncoder.encode("Mozilla/5.0 (Linux; Android","UTF-8");
        String htmlResp=HttpClientUtil.httpBaiduGet(url);
        Document doc = Jsoup.parse(htmlResp);

        Elements elements=doc.getElementsByClass("c-container");
        for (Element element : elements) {
            Elements tele=element.getElementsByClass("t");
            if(tele.size()==0){
                continue;
            }
            Elements aele=tele.get(0).getElementsByTag("a");
            if(aele.size()==0){
                continue;
            }
            String href=(aele.get(0).attr("href"));
            System.out.println(href);
            String modelResp= null;
            try {
                modelResp = HttpClientUtil.httpBaiduGet(href);
            } catch (Exception e) {
                System.out.println("请求页面异常");
                continue;
            }

            String patterStr="(?<=Mozilla/5.0 \\(Linux\\;)(.+?)(?=\\))";
            Pattern pattern = Pattern.compile(patterStr);
            Matcher matcher = pattern.matcher(modelResp);
            while (matcher.find()){
                String ua=("Mozilla/5.0 (Linux; "+matcher.group()+"; wv)");
                String uaModel=getUAModel(ua);
                if(StringUtils.isBlank(uaModel)){
                    continue;
                }

            }

        }
        return null;

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
