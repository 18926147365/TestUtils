import bean.BinTree;
import bean.Fund;
import bean.IModel;
import bean.Shop;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.google.common.base.Stopwatch;
import com.google.common.hash.BloomFilter;
import com.google.common.hash.Funnels;

import io.netty.util.concurrent.ThreadPerTaskExecutor;
import javafx.concurrent.Task;
import lombok.SneakyThrows;
import org.apache.commons.io.IOUtils;
import org.apache.commons.io.input.ReaderInputStream;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.StopWatch;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.omg.PortableInterceptor.INACTIVE;
import system.DeloyTask;
import system.People;
import system.Singleton;
import system.User;
import utils.*;

import java.io.*;
import java.lang.management.ManagementFactory;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.SocketAddress;
import java.net.URLEncoder;
import java.nio.charset.Charset;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.ReentrantLock;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.sun.management.GarbageCollectorMXBean;

import javax.management.MBeanServer;

public class Test {

    private static String REGEX_CHINESE = "[\u4e00-\u9fa5]";// 中文正则
    private static volatile GarbageCollectorMXBean gcMBean;

    private static final String GC_BEAN_NAME =
            "java.lang:type=GarbageCollector,name=ConcurrentMarkSweep";

    public static ListNode mergeTwoLists(ListNode l1, ListNode l2) {
        //1 2 4
        //3 5 7
//        ListNode l1 = new ListNode(1, new ListNode(2, new ListNode(4)));
//        ListNode l2 = new ListNode(3, new ListNode(5, new ListNode(7)));
        int l1v = l1.val; //
        int l2v = l2.val; //

//        http://10.191.8.120:18802/coupon/json/coupon/directDists--:
//        {token=[a::6C74DC502264439692CEF7421900A908],
//            sign=[C77B89F6E29343E6B534179DFFB91937],
//            jsonData=[{"couList":[{"couTypeCode":"20JFSH000007","validStartDate":1605752093397,"validEndDate":1621304093000,"couNum":3}],"orderType":"200001","activityId":"2455","orderNo":"572614a0-69c0-4cdd-83cb-93c5821d524e","userId":"14969287"}]}
        return null;
    }

    public static void directDists() {

        String url = "http://10.191.8.120:18802/coupon/json/coupon/directDists";
//        String url = "http://10.248.250.254:18802/coupon/json/coupon/directDists";
        String token = "a::A577CA9BB68C4BCEA557D2F8B6101F61";
        String sign = "";
        String str = "[{\"couList\":[{\"couTypeCode\":\"20JFSH000007\",\"validStartDate\":1605752093397,\"validEndDate\":1621304093000,\"couNum\":3}],\"orderType\":\"200001\",\"activityId\":\"2455\",\"orderNo\":\"572614a0-69c0-4cdd-83cb-93c5821d524e\",\"userId\":\"14969287\"}]";

        Map<String, String> paramas = new HashMap<>();
        paramas.put("token", token);
        paramas.put("sign", sign);
        paramas.put("jsonData", str);
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");
        String timestamp = sdf.format(new Date());
        String secretKey = "";
        String urlParams = String.format("jsonData=%s&timestamp=%s&secretKey=%s", str, timestamp, secretKey);
        sign = (urlParams);
        System.out.println(HttpClientUtil.httpString(url, paramas));

    }


    public static String trimSpaces(String IP) {//去掉IP字符串前后所有的空格
        while (IP.startsWith(" ")) {
            IP = IP.substring(1, IP.length()).trim();
        }
        while (IP.endsWith(" ")) {
            IP = IP.substring(0, IP.length() - 1).trim();
        }
        return IP;
    }

    static volatile long totalTask = 1000000;

    private static Queue<User> reStartOrderActivityQueue = new ArrayBlockingQueue<>(10);

    public static void main(String[] args) throws Exception {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String result = (HttpClientUtil.httpGet("http://fundgz.1234567.com.cn/js/001593.js"));
        String str="jsonpgz({\"fundcode\":\"001593\",\"name\":\"天弘创业板ETF联接基金C\",\"jzrq\":\"2021-03-08\",\"dwjz\":\"1.0643\",\"gsz\":\"1.0289\",\"gszzl\":\"-3.33\",\"gztime\":\"2021-03-09 15:00\"});";
        String patterStr="(jsonpgz\\()(.*)(\\);)";
        Matcher matcher = Pattern.compile(patterStr).matcher(result);
        if(matcher.find()){
            String json = matcher.group(2);
            JSONObject fundJson = (JSONObject.parseObject(json));
            String fundName = fundJson.getString("name");
            String fundCode = fundJson.getString("fundcode");
            String gztime = fundJson.getString("gztime");
            BigDecimal gszzl = fundJson.getBigDecimal("gszzl");
            Date gzDate = sdf.parse(gztime);

        }

        Runtime.getRuntime().exec("sh /Users/lihaoming/data/shell/notify.sh 休盘 +1.53%");


    }

    Object str1 = new Object();
    Object str2 = new Object();
    static WeakHashMap weakHashMap = new WeakHashMap<>();

    public void test() throws InterruptedException {

    }

    static class MyTask extends RecursiveTask<Integer> {

        private long taskCount;

        public MyTask(long taskCount) {
            this.taskCount = taskCount;
        }

        @Override
        protected Integer compute() {
            if (taskCount <= 1000) {
                try {
                    Thread.sleep(1223);
                    synchronized ("!") {
                        totalTask -= taskCount;
                    }
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            } else {
                MyTask myTask = new MyTask(taskCount);
                invokeAll(myTask);
            }
            return 1;
        }
    }

    static class DelayQueueItem extends DelayQueue {

        @Override
        public boolean offer(Object o) {


            return false;
        }
    }


    public static Integer getMonthCnt(Date startTime, Date endTime) {
        int startYear = startTime.getYear();
        int endYear = endTime.getYear();
        int startMonth = startTime.getMonth();
        int endMonth = endTime.getMonth();
        int valueYear = endYear - startYear;
        Integer monthCnt = null;
        if (valueYear == 0) {
            monthCnt = (endMonth - startMonth);
        } else {
            monthCnt = (valueYear * 12) + endMonth - startMonth;
        }
        return monthCnt;
    }

    public static Integer getDifMonth(Date startDate, Date endDate) {
        Calendar start = Calendar.getInstance();
        Calendar end = Calendar.getInstance();
        start.setTime(startDate);
        end.setTime(endDate);
        int result = end.get(Calendar.MONTH) - start.get(Calendar.MONTH);
        int month = (end.get(Calendar.YEAR) - start.get(Calendar.YEAR)) * 12;
        return Math.abs(month + result);
    }

    //1 1
    //2 11
    //3 21
    public static String countAndSay(int n) {
        if (n == 1) {
            return "1";
        }
        String num = getnumer("1");
        for (int i = 1; i < n - 1; i++) {
            num = getnumer(num);
        }
        return num;
    }

    public static String getnumer(String nums) {
        char[] chars = nums.toCharArray();
        int j = 1;
        StringBuilder sbuilder = new StringBuilder();
        Character lastChar = null;
        for (int i = 0; i < chars.length; i++) {
            if (lastChar == null) {
                lastChar = chars[i];
            } else {
                if (lastChar == chars[i]) {
                    j++;
                } else {
                    sbuilder.append(j);
                    sbuilder.append(lastChar);
                    j = 1;
                    lastChar = chars[i];
                }
            }
        }
        sbuilder.append(j);
        sbuilder.append(lastChar);
        return sbuilder.toString();
    }

    public static int searchInsert(int[] nums, int target) {
        int base = (int) Math.ceil((double) nums.length / 2);
        for (int i = 0; i <= base; i++) {
            if (nums[base - 1] >= target) {
                if (nums[i] >= target) {
                    return i;
                }
            } else {
                if (base + i - 1 < nums.length && nums[base + i - 1] >= target) {
                    return base + i - 1;
                }
            }
        }
        return nums.length;
    }


    public static int strStr(String haystack, String needle) {
        if (needle.equals("")) {
            return 0;
        }
        char[] haystacks = haystack.toCharArray();
        char[] needles = needle.toCharArray();
        int j = 0;
        for (int i = 0; i < haystacks.length; i++) {
            if (haystacks[i] == needles[j]) {
                j++;
                if (needles.length == j) {
                    return i - j + 1;
                }
            } else {
                if (j != 0) {
                    i -= j;

                }
                j = 0;
            }
        }
        return -1;
    }

    public static class ListNode {
        int val;
        ListNode next;

        ListNode() {
        }

        ListNode(int val) {
            this.val = val;
        }

        ListNode(int val, ListNode next) {
            this.val = val;
            this.next = next;
        }
    }


    public static boolean isValid(String s) {
        Stack<Character> stack = new Stack<>();
        Map<String, String> map = new HashMap<>();
        map.put("}", "{");
        map.put(")", "(");
        map.put("]", "[");
        for (char c : s.toCharArray()) {
            String str = String.valueOf(c);
            if (map.containsKey(str)) {
                if (stack.size() == 0) {
                    return false;
                }
                char temp = stack.pop();
                if (!map.get(str).equals(String.valueOf(temp))) {
                    return false;
                }
            } else {
                stack.add(c);
            }
        }
        return stack.isEmpty();
    }

    public static int reverse(int x) {
        if (x == 0 || x == Integer.MIN_VALUE) {
            return 0;
        }
        StringBuilder sbuilder = new StringBuilder(Math.abs(x) + "");
        sbuilder.reverse();
        long num = Long.parseLong((x < 0 ? "-" : "") + sbuilder.toString());
        if (num > Integer.MAX_VALUE || num < Integer.MIN_VALUE) {
            return 0;
        }
        return (int) num;
    }

    private static String getOverStr(String str1, String str2) {
        if (str1.equals("") || str2.equals("")) {
            return "";
        }
        StringBuilder sbuilder = new StringBuilder();
        for (int i = 0; i < str1.length(); i++) {
            if (i < str2.length() && str1.charAt(i) == str2.charAt(i)) {
                sbuilder.append(str1.charAt(i));
            } else {
                break;
            }
        }
        return sbuilder.toString();
    }

    private static void tests() {
        String moneys = "0.11";
        BigDecimal totalMoney = new BigDecimal(moneys);
        int people = 10;
        BigDecimal valid = new BigDecimal(0);
        for (int i = 0; i < people; i++) {
            BigDecimal money = getRandMoney(totalMoney, people - i);
            totalMoney = totalMoney.subtract(money);
            System.out.println(money.doubleValue());
            valid = valid.add(money.setScale(2, RoundingMode.HALF_UP));
        }

        if (valid.compareTo(new BigDecimal(moneys)) == 0) {
            System.out.println("正常");
        } else {

            System.out.println("异常:" + valid + " >> " + moneys);
            throw new RuntimeException("222");
        }

    }

    private static BigDecimal getRandMoney(BigDecimal totalMoney, int people) {

        //平均每人得到金额
        BigDecimal avgMoney = (totalMoney.divide(new BigDecimal(people), 2, BigDecimal.ROUND_DOWN));
        BigDecimal thisMoney = new BigDecimal(avgMoney.doubleValue());

        if (thisMoney.doubleValue() < 0.01) {
            throw new RuntimeException("平均每人不够1分钱");
        } else if (thisMoney.setScale(3, RoundingMode.HALF_UP).compareTo(new BigDecimal("0.01")) == 0) {
            return new BigDecimal("0.01");
        }
        Random random = new Random();
        if (people == 1) {
            return thisMoney;
        }
        for (int i = 0; i < people; i++) {
            System.out.println((Math.random() * avgMoney.doubleValue()) * (random.nextInt() > 0 ? 1d : -1d));
            BigDecimal rand = new BigDecimal((Math.random() * avgMoney.doubleValue()) * (random.nextInt() > 0 ? 1d : -1d));
            thisMoney = avgMoney.add(rand);
        }

        return thisMoney.setScale(2, RoundingMode.DOWN);
    }


    public static void httpMarket() {

        String url = "http://10.191.8.120:18803/market/json/mkt_activity_order/startOrderActivity?token=a::159894032CA94F04B9A05951CBBCA03B&sign=100";

        ExecutorService pool = new ThreadPoolExecutor(20, 20, 5000,
                TimeUnit.MILLISECONDS,
                new LinkedBlockingQueue<>(),
                Executors.defaultThreadFactory(),
                new ThreadPoolExecutor.AbortPolicy());
        ((ThreadPoolExecutor) pool).allowCoreThreadTimeOut(true);
        CountDownLatch countDownLatch = new CountDownLatch(1);

        StopWatch stopWatch = new StopWatch();
        stopWatch.start();
        for (int i = 0; i < 10; i++) {
            final int k = i;
            pool.submit(new Runnable() {
                @Override
                public void run() {
                    Map<String, String> map = new HashMap<>();
                    String jsonStr = "{\"orderType\":\"950013\",\"usedCouIsDist\":\"true\",\"promoteIsDist\":\"false\",\"requestOrderModels\":\"[{\\\"combinationPayMode\\\":\\\"071His_6202\\\"},{\\\"combinationPayMode\\\":\\\"071His_64\\\"}]\",\"telephone\":\"15813811408\",\"distributionChannel\":\"1981\",\"orderProductList\":\"[{\\\"proCode\\\":\\\"35339\\\",\\\"categoryCode\\\":\\\"830906\\\",\\\"salePrice\\\":234.6700,\\\"num\\\":1.0000,\\\"amount\\\":234.67}]\",\"orderNo\":\"2020090571921314417_44030886106002009080028\",\"storeConsumeAmount\":\"234.6700\",\"tradeTime\":\"2020-09-08 07:38:54.000\",\"storeOuCode\":\"100043024010013\",\"userId\":\"12385922\",\"userLevel\":\"1001\",\"acceptTime\":\"2020-09-08 07:39:05.450\",\"birthday\":\"\",\"payTime\":\"2020/9/5 8:48:31\",\"tradeNode\":\"4403088\",\"networkType\":\"001001001\"}";
                    map = (Map<String, String>) JSONObject.parse(jsonStr);
                    map.put("occurOuCode", "2001150");
                    String orderNo = "2020091071921314416_44030886106002031" + k;
                    map.put("orderNo", orderNo);
                    System.out.println(HttpClientUtil.doPost(url, map, null));
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
    public static void testBloom2() {

        String key = "123123";
        BloomFilter<String> bloomFilter = BloomFilterUtils.createOrGetString(key, 1000000, 0.03);
        //提前将10W白名单插入过滤器中
        for (int i = 0; i < 100000; i++) {
            bloomFilter.put(i + "");
        }


        //100W访问，判断是否有白名单用户
        int f = 0;
        for (int i = 0; i < 1000000; i++) {
            int rand = (int) (Math.random() * 10000000) + 500;
            if (bloomFilter.mightContain(rand + "")) {
                if (rand > 100000) {
                    f++;
                    System.err.println(rand);
                }
            }
        }
        System.out.println(f);
    }

    public static void testBloom() {
        BloomFilter<CharSequence> filter = BloomFilter.create(Funnels.stringFunnel(Charset.defaultCharset()), 10000000, 0.03F);
        int size = 1000000;
        for (int i = 0; i < size; i++) {
            filter.put(i + "");
        }
        StopWatch stopWatch = new StopWatch();
        stopWatch.start();

        int t = 0;
        for (int i = 0; i < size * 10; i++) {
            boolean isex = (filter.mightContain(i + ""));
            if (isex) {
                t++;
            }
        }
        stopWatch.stop();
        System.out.println("共检索:" + t + "误报个数:" + (t - size) + " 误报率:" + ((1 - (((double) size) / (double) t)) * 100) + "%" + " 耗时：" + stopWatch.getTime() + "ms");
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
