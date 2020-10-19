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

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
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


    private static String getUAModel(String ua){
        if(ua.toLowerCase().contains("linux")){
            String[] uas=ua.split(";");
            for (String s : uas) {
                String uaTrim=(s.trim());
                if(uaTrim.contains("Build/")){
                    String[] uaModels=uaTrim.split(" Build/");
                    if(uaModels.length!=0){
                        return (uaModels[0]);
                    }

                }

            }
        }
        return null;
    }


    public static Map<String,List<String>> map=new HashMap<>();
//        static String str="[\"别名:vivo V1809A 红外面部识别,第四代屏幕指纹解锁,双涡轮加速引擎,Jovi语音助手综述介绍 参数 图片(123) 视频(2) 点评(44) 报价 竞品对比 评测行情 论坛 问答...\",\"4G+ TD-LTE:B38C/B39C/B40C/B41C/B39A+B41A,B40D/B41D/B39A+B41C/B39C+B41A;4G+ FDD-LTE:B1C/B3C/B7C/B1A+B3A 1、全网通版X23(V1809A):...\",\"最佳答案: vivox23型号有V1809T和V1809A两种,主要的区别是 V1089T是移动全网通版本,对使用的数据网络有限制,如果双卡中有一张移动卡,其它电话卡就无法使用数据,... 更多关于V1809A的问题>>\",\"2018年9月27日 vivo官网社区»版块 › 产品圈 › X系列 › 这个V1809A是什么意思 vivo43026274536 这个V1809A是什么意思 [问题反馈]| 发表于 2018-9-27 20:56 66691...\",\"2018年9月3日 手机信息网为您提供维沃 V1809A手机的详细资料,包括维沃 V1809A手机最新报价,维沃 V1809A手机规格参数、功能介绍,维沃 V1809A评测行情,维沃 V1809A手机图片等...\",\"2019年10月1日 v1809a是什么手机牌子 - v1809a是vivox23(8GB+128GB)这款手机全网通版的型号,除了v1809a这个型号之外,vivox23还有一个v1809t型号,也就是vivox23移动全...\",\"2020年2月28日 中关村在线(ZOL.COM.CN)提供vivo X23幻彩版 8GB RAM/全网通 手机最新报价,同时包括vivo X23幻彩版 8GB RAM/全网通 图片、vivo X23幻彩版 8GB RAM/全网通 参数...\",\"2020年5月15日 v1809a是vivox23全网通版手机型号,该手机配有高通骁龙670移动平台、vivo Dual Turbo 双涡轮引擎,支持超大广角、超逆光、AI摄影,并且采用了第四代光电屏幕指纹及零...\",\"2018年9月28日 这V1809A vivo X50 Pro 参考价: ￥3998.00 热门排名: 第2名 1 买买买 4 太贵了 0 已入手 0 买不到 0 山寨货 0 不值得 1 没兴趣 ...\"]\n";
//        static String str="[\"别名:TAS-AN00,MATE30 5G IP53防水防尘,磁悬浮发声技术,4000万超感光徕卡影像,OLED全面屏综述介绍 参数 图片(258) 视频(73) 点评(40) 报价 竞品对比 评测行情...\",\"2020年3月13日 tas-an00是华为手机Mate30。华为Mate30核心硬件包括麒麟990,6/8GB+128GB存储,4200mAh电池(40W有线、27W无线),预装EMUI 10系统,屏幕指纹识别,IP53防水。\",\"2019年12月21日 首页»版块 › 华为手机 › 华为Mate30系列 › 为什么机型是TAS-AN00啊?[分享交流] 为什么机型是TAS-AN00啊?[复制帖子标题和链接] 213147...\",\"2020年9月9日 华为tas-an00是华为mate30手机的型号。外观方面,华为Mate30配备6.7英寸FHD+OLED OLED屏幕,提供有星河银,罗兰紫、翡冷翠、亮黑色等四种配色。相机方面,华为Mate 30...\",\"最佳答案: 华为mate30 手机忘记锁屏密码,只能通过强制恢复出厂设置清除密码。这个方法会清除您之前的数据,请谨慎操作。操作步... 更多关于TAS-AN00的问题>>\",\"Huawei Mate 30 5G, TAS-AN00; TAS-TN00, 触控手机, 彩色 / OLED 1080 x 2340 px 6.62\\\", Li-Po 4200 mAh, HiSilicon Kirin 990 5G\",\"最佳答案: AN00是5G版,AL00是4G版 更多关于TAS-AN00的问题>>\",\"2020年2月22日 华为mate30pro 5G的lio-an00和TAS-an00有何区别? 华为Mate 80% 知友推荐 · 7,300 人评价 华为Mate是2013年3月上市的一款智能手机。采用6.1英寸全贴合IPS材...\",\"2020年1月6日 HUAWEI Mate30(5G)(TAS-AN00)8GB+128GB亮黑色全网通版手机苏宁易购(suning.com)提供【华为(HUAWEI)系列】HUAWEI Mate30(5G)(TAS-AN00)8GB+128GB亮黑色...\"]\n";
    static String str="[\"vivo y71系列-「京东」手机,旗舰品质,运行流畅,一触即发,续航无忧,一手掌握大\\\"视\\\"界!购手机到「京东」!JD.COM,正品手机优惠价,配送快售后棒,退货无忧可信任!\",\"vivo Y71 更大全面屏,畅快玩不停\",\"2 vivo S7(8GB/128GB/全网通/5G版) 3 vivo X50(8GB/128GB/全网通/5G版) 4 vivo S6(8GB/128GB/全网通/5G版) 5 vivo Y71(3GB RAM/全网通) ...\",\"购物上淘宝，百万诚信商家，25亿高人气热卖商品，淘你满意!支付无忧，交易更放心!淘宝网，淘你喜欢! www.taobao.com 2020-10 \uE62B\uE62D评价广告\",\"1天前 近日,vivo宣布将即将在10月16日推出自家 Y系列的新一代产品vivo Y73s。这款手机作为vivo千元市场的主力军,起步价为1998元。\",\"查询ATMEGA2560-16AU价格，ATMEGA2560-16AU报价，就上华强电子交易网。百万商家入驻，亿万IC产品任选，买卖IC上华强电子网\",\"2018年6月30日 vivo手机Y71,苏宁易购提供vivoY71 4GB+64GB 金色 4G全网通 全面屏 拍照手机,18:9超大全面屏,纤薄精致手感,强劲配置,流畅体验,Face Wake面部识别,买vivo手机,就...\",\"2天前 2020年10月12日,深圳 ——作为vivo千元市场的主力军,vivo Y系列即将在10月16日迎来新一代产品vivo Y73s,起步价为1998元。 vivo Y73s轻薄时尚的机身,让用户...\",\"太平洋电脑网提供vivo Y71手机全面信息,包括vivo Y71报价、图片、参数、网友点评、评测、论坛、vivo Y71软件、游戏等信息,帮您全面了解vivo Y71手机\",\"2020年2月28日 ZOL手机版提供vivo Y71系列手机所有单品的型号、报价、配置、评测、行情、图片、论坛、点评、视频、驱动下载等内容,以及vivo Y71系列手机的经销商报价,为您购买...\",\"2018年3月31日 CNMO手机中国为您提供vivoY71报价、图片、参数、点评等信息,让您快速全面了解vivo Y71(32GB) ,更多vivoY71 手机相关信息尽在手机中国。\"]\n";
    static {
        List<String> l1=Arrays.asList("华为","huawei");
        List<String> l2=Arrays.asList("vivo");
        map.put("华为",l1);
        map.put("vivo",l2);
    }






    public static void main(String[] args) throws Exception {


//
        File file = new File("/Users/lihaoming/Downloads/手机UA大全.txt");
        BufferedReader reader = null;
        StringBuffer sbf = new StringBuffer();
        try {
            reader = new BufferedReader(new FileReader(file));
            String tempStr;
            while ((tempStr = reader.readLine()) != null) {
                System.out.println(getUAModel(tempStr));

            }
            reader.close();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (reader != null) {
                try {
                    reader.close();
                } catch (IOException e1) {
                    e1.printStackTrace();
                }
            }
        }
        System.out.println(getUAModel("Mozilla/5.0 (Linux; U; Android 4.4.2; zh-cn; H60-L03 Build/HDH60-L03) AppleWebKit/537.36 (KHTML, like Gecko)Version/4.0 Chrome/37.0.0.0 MQQBrowser/7.2 Mobile Safari/537.36\n"));
        
//     String  url = "http://www.baidu.com/s?wd="+ URLEncoder.encode("SM-N900S","UTF-8");
////        String url="http://so.romzhijia.net/cse/search?s=12725138872909822094&q="+ URLEncoder.encode("SM-N900S","UTF-8");
//        String htmlResp=HttpClientUtil.httpBaiduGet(url);
//        System.out.println(htmlResp);
//        Document doc = Jsoup.parse(htmlResp);
//        Elements elements=doc.getElementsByClass("c-abstract");
//        for (Element element : elements) {
//            String text=element.text();
//            System.out.println(text);
//        }
    }

    private static IModel getIModel(List<String> uaModellist){
        Map<String,Integer> totalMap=new HashMap<>();
        Map<String,Integer> modelMap=new HashMap<>();
        for (String content : uaModellist) {
            for (String brand : map.keySet()) {
                List<String> str=map.get(brand);
                for (String s : str) {
                    if (content.contains(s)) {
                        getTotalMap(totalMap,brand);
                        //-------获取手机型号
                        String modelStr=content.substring(content.indexOf(s), content.indexOf(s)+20);
                        Pattern pat = Pattern.compile(REGEX_CHINESE);
                        Matcher mat = pat.matcher(modelStr);
                        modelStr=(mat.replaceAll(" "));
                        modelStr=modelStr.replaceAll(","," ").replace("。"," ").replace(":"," ").replace("  "," ");
                        modelStr=modelStr.replace(s,"").toUpperCase();
                        String[] ll=modelStr.split(" ");
                        for (String s1 : ll) {
                            s1=s1.replace(" ","");
                            if(StringUtils.isBlank(s1) || s1.length()<=1){
                               continue;
                            }
                            Integer count=modelMap.get(s1);
                            if(count==null){
                                count=0;
                            }
                            modelMap.put(s1,count+1);
                        }
                        //获取手机型号结束
                    }
                }
            }
        }


        List<Map.Entry<String, Integer>> list = new ArrayList<>(totalMap.entrySet());
        Collections.sort(list,new Comparator<Map.Entry<String, Integer>>() {
            //升序排序
            public int compare(Map.Entry<String, Integer> o1, Map.Entry<String, Integer> o2) {
                return o2.getValue().compareTo(o1.getValue());
            }
        });


        List<Map.Entry<String, Integer>> list1 = new ArrayList<>(modelMap.entrySet());
        Collections.sort(list1,new Comparator<Map.Entry<String, Integer>>() {
            //升序排序
            public int compare(Map.Entry<String, Integer> o1, Map.Entry<String, Integer> o2) {
                return o2.getValue().compareTo(o1.getValue());
            }
        });

        if(list!=null && list.size()!=0){
            IModel iModel=new IModel();
            iModel.setBrands(list);
            iModel.setBrand(list.get(0).getKey());
            if(list1!=null && list1.size()!=0){
                iModel.setModel(list1.get(0).getKey());
            }
            return iModel;
        }
        return null;
    }

    private static void getTotalMap(Map<String,Integer> totalMap,String brand){
       Integer count= totalMap.get(brand);
       if(count==null){
           count=0;
       }
       totalMap.put(brand,count+1);
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
