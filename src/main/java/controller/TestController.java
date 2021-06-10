package controller;

import bean.BigModel;
import bean.Fund;
import bean.User;
import bean.UserInfo;
import com.alibaba.excel.EasyExcel;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.google.common.base.Stopwatch;
import com.google.common.hash.BloomFilter;
import com.sun.corba.se.spi.orbutil.threadpool.ThreadPoolManager;
import easyexcel.DemoData;
import easyexcel.DemoDataListener;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import jdk.nashorn.api.scripting.ScriptObjectMirror;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import mapper.FundMapper;
import mapper.UserMapper;
import org.apache.commons.lang3.StringUtils;
import org.omg.PortableServer.POA;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.util.StopWatch;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.context.ContextLoader;
import org.springframework.web.context.WebApplicationContext;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import service.FundService;
import service.ModelService;
import service.TestService;
import system.LoggerProxy;
import task.FundTask;
import utils.*;

import javax.annotation.Resource;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;
import javax.servlet.http.HttpServletRequest;
import java.io.*;
import java.math.BigDecimal;
import java.rmi.server.UID;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.*;
import java.util.function.Supplier;

/**
 * @author 李浩铭
 * @date 2020/7/9 17:00
 * @descroption
 */
@RestController
@RequestMapping("test")
@Slf4j
public class TestController {
    private static String str = "123123";


    @Autowired
    private UserMapper userMapper;

    @Resource
    private FundMapper fundMapper;

    @Autowired
    private TestService testService;

    @Autowired
    private RedisLuaUtils redisLuaUtils;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Autowired
    private ModelService modelService;

    @RequestMapping("/report")
    public String report(HttpServletRequest request) throws Exception {


        String path = "D:/2.csv";
        File file = new File(path);
        if (!file.exists()) {
            file.createNewFile();
        } else {
            file.delete();
            file.createNewFile();
        }


        //------------------------------------
        StopWatch stopWatch = new StopWatch();
        stopWatch.start();


        List<User> list = userMapper.queryMoney();

        stopWatch.stop();
        System.out.println("查询时间花费：" + stopWatch.getTotalTimeSeconds() + "s");

        stopWatch = new StopWatch();
        stopWatch.start();
        Object[] header = {"id", "name", "money"};


        List<Object[]> dataList = new ArrayList<>();


        for (User user : list) {
            Object[] objects = new Object[3];
            objects[0] = user.getId();
            objects[1] = user.getName();
            objects[2] = user.getMoney().doubleValue();
            dataList.add(objects);
        }


        CsvUtils.writeCsv(header, dataList, path);

        stopWatch.stop();
        System.out.println("写入时间花费：" + stopWatch.getTotalTimeSeconds() + "s");

//            JavaMailUtils.Mail mail=new JavaMailUtils.Mail();
//            mail.setUser("18926147365@163.com");
//            mail.setPassword("GHBQMGRRMWUOKFUY");
//            mail.setContent("测试");
//            mail.setTitle("测试");
//            mail.setToAddree("18926147365@163.com");
//            mail.setFromAddress("18926147365@163.com");
//            mail.setFromName("李浩铭");
//            try {
//                JavaMailUtils.sendMail2(mail,new File("/1.csv"));
//            } catch (Exception e) {
//                e.printStackTrace();

        return "12";
    }


    @RequestMapping("/report2")
    public String report2(HttpServletRequest request) throws Exception {


        StopWatch stopWatch = new StopWatch();
        stopWatch.start();
        List<User> list = new ArrayList<>();
        for (int i = 0; i < 100; i++) {
            List<User> list1 = userMapper.queryMoneys(10 * i, 2000);
            list.addAll(list1);
        }
        stopWatch.stop();


        return JSONObject.toJSONString(list);
    }


    static ExecutorService pool = new ThreadPoolExecutor(1, 1, 5000,
            TimeUnit.MILLISECONDS,
            new LinkedBlockingQueue<>(),
            Executors.defaultThreadFactory(),
            new ThreadPoolExecutor.AbortPolicy());

    static {
        ((ThreadPoolExecutor) pool).allowCoreThreadTimeOut(true);
    }

    @RequestMapping("/test")
    public String test(HttpServletRequest request) {
        for (int i = 0; i < 100000; i++) {
            final int k = i;
            BigModel bigModel = new BigModel(k);
            pool.submit(new Runnable() {
                @Override
                public void run() {

                    try {
                        Thread.sleep(3000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    System.out.println(((ThreadPoolExecutor) pool).getQueue().size());
//                    bigModel.getContent();
                }
            });
        }

        return "123";
    }

    private static String randStr = "";

    static {
        randStr = new Date().getTime() + "";
    }


    @RequestMapping("/test1")
    public String test1(HttpServletRequest request, String name) throws ExecutionException, InterruptedException {


        String key = "test:cache:11118";
//        System.out.println(redisLuaUtils.hget(key,"state"));
        String val = redisLuaUtils.hgetCache(key, "默认值", new Supplier<String>() {
            @Override
            public String get() {
                return testService.getName();
            }
        }, 2500, 1000);

        return val;
    }


    private void saveCSV(List<User> list) {
        Object[] header = {"id", "name", "money", "businessId"};


        List<Object[]> dataList = new ArrayList<>();


        for (User user : list) {
            Object[] objects = new Object[4];
            objects[0] = user.getId();
            objects[1] = "123";
            objects[2] = user.getMoney().doubleValue();
            objects[3] = user.getBusinessId();
            dataList.add(objects);
        }

        String path = "D:/test.csv";
        try {
            CsvUtils.writeCsv(header, dataList, path);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public char getRandomChar() {
        return (char) (0x4e00 + (int) (Math.random() * (0x9fa5 - 0x4e00 + 1)));
    }


    @RequestMapping("/peopleCount")
    public String peopleCount(HttpServletRequest request) throws ExecutionException, InterruptedException {


        String key = "peopleCount:3";
        String val = redisLuaUtils.hgetCache(key, "默认值", new Supplier<String>() {
            @Override
            public String get() {
                System.out.println("查询db.....");
                return userMapper.countMoney() + "";
            }
        }, 6000, 12000);

        return val;
    }

    @RequestMapping("/batchSaveUser")
    public String batchSaveUser() {
        ExecutorService pool = new ThreadPoolExecutor(5, 5, 5000,
                TimeUnit.MILLISECONDS,
                new LinkedBlockingQueue<>(),
                Executors.defaultThreadFactory(),
                new ThreadPoolExecutor.AbortPolicy());

        for (int k = 0; k < 95000; k++) {
            final int d = k;
            pool.submit(new Runnable() {
                @Override
                public void run() {
                    System.out.println("当前批次：" + d);
                    List<User> list = new ArrayList<>();
                    for (int i = 0; i < 1000; i++) {
                        User user = new User();
                        user.setName(String.valueOf(getRandomChar()) + String.valueOf(getRandomChar()) + String.valueOf(getRandomChar()));
                        long businessId = (long) (Math.random() * 100) + 1;
                        user.setMoney(BigDecimal.valueOf(Math.random() * 100000 + 200));
                        user.setBusinessId(businessId);
                        list.add(user);
                    }
                    userMapper.insertBatch(list);
                }
            });

        }
        return "123";
    }

    @RequestMapping("/seckill")
    public String seckill() throws InterruptedException {
        String lockKey = "seckill:count1";
        long result = redisLuaUtils.evalsha(RedisLuaUtils.ScriptLoadEnum.SECKILL, Long.class, lockKey, "-1");
        if (result == -1) {
            System.out.println("没有了:" + redisLuaUtils.get(lockKey));
        }

        return "1";
    }


    @RequestMapping("/lottery")
    public String lottery(HttpServletRequest request, String key, String name) throws InterruptedException {


        long obj = (redisLuaUtils.evalsha(RedisLuaUtils.ScriptLoadEnum.INCRBYGETMAX, Long.class, key, "10000", "1"));
        if (obj == -1) {
            System.out.println("没有奖品了");
        } else {
            System.out.println(obj);
        }

        return "中奖了";
    }

    @RequestMapping("/setRedis")
    public String setRedis(HttpServletRequest request, String key, String value) {
        redisLuaUtils.set(key, value);

        return "123123";
    }


    @RequestMapping("/test11")
    public String test11(HttpServletRequest request) {

        return "123";
    }

    static ExecutorService mypool = new ThreadPoolExecutor(20, 20, 5000,
            TimeUnit.MILLISECONDS,
            new LinkedBlockingQueue<>(),
            Executors.defaultThreadFactory(),
            new ThreadPoolExecutor.AbortPolicy());


    @GetMapping("/test22")
    public String test22(HttpServletRequest request) {

        System.out.println(userMapper.queryMoneys(0, 1).size());
        return "";


    }

    @RequestMapping("/test33")
    public void test33() {
//        String sql="insert into test2 (hash_code,`key`) value";
//
//        List<String> list=new ArrayList<>();
//        int k=0;
//        for (int i = 0; i < 20000000; i++) {
//            String  key=String.format("10001-1-%s",UUID.randomUUID().toString());
//            int hashCode=key.hashCode();
//            list.add("("+hashCode+",'"+key+"')");
//            if(i%2000==0){
//                k++;
//                System.out.println((double)k/(double)10000);
//                jdbcTemplate.update(sql+String.join(",", list));
//                list=new ArrayList<>();
//            }
//
//        }
        System.out.println("完成");
    }

    private static Object lock = new Object();

    @RequestMapping("/test34")
    public void test34() throws InterruptedException {
        Thread thread = new Thread(new Runnable() {
            @SneakyThrows
            @Override
            public void run() {
                Thread.sleep(1000);
                System.out.println("123");
            }
        });
        thread.start();
        thread.join();
        System.out.println("111");

    }

    @RequestMapping("/test344")
    public void test344() throws InterruptedException {
        synchronized (lock) {
            lock.notifyAll();
        }
    }


    @RequestMapping("/test35")
    public void test35() {


        BloomFilter<String> bloomFilter = BloomFilterUtils.createOrGetString("modelss", 10000000, 0.02d);

        ExecutorService pool = new ThreadPoolExecutor(5, 5, 5000,
                TimeUnit.MILLISECONDS,
                new LinkedBlockingQueue<>(),
                Executors.defaultThreadFactory(),
                new ThreadPoolExecutor.AbortPolicy());


        long index = 0;
        String indexStr = redisLuaUtils.get("modelsIndex");
        if (StringUtils.isNotBlank(indexStr)) {
            index = Long.valueOf(indexStr);
        }
        String sql = "SELECT id,model FROM `activity_access_log` WHERE model IS NOT NULL AND systemos=1 ORDER BY id DESC LIMIT ?,5000";
        for (int i = 0; i < 100; i++) {
            pool.submit(new Runnable() {
                @Override
                public void run() {
                    long myIndex = redisLuaUtils.incrBy("modelsIndex", 5000l);
                    System.out.println(myIndex);
                    List<Map<String, Object>> list = jdbcTemplate.queryForList(sql, myIndex);
                    for (Map<String, Object> stringObjectMap : list) {
                        String model = stringObjectMap.get("model") + "";
                        if (bloomFilter.put(model)) {
                            redisLuaUtils.lpush("modelLists", model);
                        }
                    }
                }
            });

        }


    }

    @RequestMapping("/test36")
    public void test36() {
        String insertSql = "insert into model (ua,ua_model,status) values(?,?,1)";
        BloomFilter<String> bloomFilter = BloomFilterUtils.createOrGetString("phoneModel", 1000000, 0.02d);
        int i = 0;
        do {
            String ua = redisLuaUtils.lpop("modelLists");
            if (StringUtils.isBlank(ua)) {
                break;
            }
            System.out.println(i++);
            String uaModel = getUAModel(ua);
            if (uaModel == null) {
                continue;
            }
            if (!bloomFilter.put(uaModel)) {
                continue;
            }
            String existsSql = "select count(1) from model where ua_model = ?";
            if ((jdbcTemplate.queryForObject(existsSql, Long.class, uaModel)) > 0) {
                continue;
            }
            jdbcTemplate.update(insertSql, ua, uaModel);
        } while (true);


    }


    @RequestMapping("/test37")
    public void test37() {
        String key = "testbit222222";
//        redisLuaUtils.setbit(key,22,"0");
        System.out.println(redisLuaUtils.setBitIfFalse(key, 2l));
        System.out.println(redisLuaUtils.getbit(key, 2l));
    }

    private String getUAModel(String ua) {
        if (ua.toLowerCase().contains("linux;")) {//安卓判断
            String[] uas = ua.split(";");
            for (String s : uas) {
                String uaTrim = (s.trim());
                if (uaTrim.contains("Build/")) {
                    String[] uaModels = uaTrim.split(" Build/");
                    if (uaModels.length != 0) {
                        return (uaModels[0]);
                    }

                }

            }
        } else if (ua.toLowerCase().contains("iPhone;")) {
            return "iPhone";
        }
        return null;
    }

    @Autowired
    private FundTask fundTask;

    @Autowired
    private User userPool1;

    @RequestMapping("/test377")
    public String test377() throws Exception {
        String str = HttpClientUtil.get("http://localhost:18816/api/api/auth/getAccessToken?appNo=yxzxhx&appKey=8F3BAC1E202AD69CBE56C2B62F5F0B23");
        JSONObject authJson = JSONObject.parseObject(str);
        String token = authJson.getString("data");
        JSONObject paramsJson = JSONObject.parseObject("{\"orderType\":\"100\",\"amount\":\"12.26\",\"isOperateVehicleUser\":\"true\",\"orderNo\":\"\",\"userId\":\"18531938\",\"refuelPayMode\":\"98910\",\"matertialCode\":\"60209058\",\"proList\":[{\n" +
                "\"proCode\":\"60209058\",\"categoryCode\":\"汽油\",\"refuelGsOuCode\":\"33250141\",\"salePrice\":\"5.63\",\"num\":\"1\",\"amount\":\"5.63\",\"refuelGsOuName\":\"广州沙太加油站\",\"matertialCode\":\"60209058\",\"oilLiter\":\"1\"\n" +
                "},{\"proCode\":\"60209058\",\"categoryCode\":\"汽油\",\"refuelGsOuCode\":\"33250135\",\"salePrice\":\"6.63\",\"num\":\"1\",\"amount\":\"6.63\",\"refuelGsOuName\":\"广州天平架加油站\",\"matertialCode\":\"60209058\",\"oilLiter\":\"1\"\n" +
                "}]}");
        Set<String> set = paramsJson.keySet();
        paramsJson.put("userId", "10001176");
        Map<String, String> params = new HashMap<>();
        for (String key : set) {
            params.put(key, paramsJson.getString(key));
        }
        String url = "http://localhost:18816/market/api/mkt_promote/queryPromoteActivityDiscountList?token=" + token;
        String result = HttpClientUtil.doPost(url, params);
        System.out.println("请求响应:" + result);
        JSONObject resultJSON = JSONObject.parseObject(result);
        JSONArray array = resultJSON.getJSONArray("data");
        String response = "";
        for (int i = 0; i < array.size(); i++) {
            JSONObject dataJson = array.getJSONObject(i);
            BigDecimal afterAmount = dataJson.getBigDecimal("afterAmount");
            BigDecimal amount = dataJson.getBigDecimal("amount");
            System.out.println("优惠前金额:" + afterAmount.toString());
            System.out.println("优惠后金额:" + amount.toString());
            if (afterAmount.doubleValue() == amount.doubleValue()) {
                System.out.println("没有优惠");
                response = "没有优惠";
            } else {
                System.out.println("已优惠金额");
                response = "已优惠";
            }
            break;
        }

        return new Date().getTime() + ":" + response;

    }

    @RequestMapping("/test113")
    public void test113(int i) {
        StringBuilder sb = new StringBuilder();
        for (int j = 0; j < 10000; j++) {
            try {
                Thread.sleep(11);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            BigModel bigModel = new BigModel(1);
            sb.append(bigModel.getContent());
        }
    }

    Object object = new Object();

    @RequestMapping("/test114")
    public void test114(int i) {
        String path = "/Users/lihaoming/Desktop/demo.xlsx";
        EasyExcel.read(path, DemoData.class, new DemoDataListener(userMapper)).sheet().doRead();
    }

    @RequestMapping("/ccc")
    public void test115(int i) {
        try {
            fundTask.execute(i);
        } catch (ParseException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }


    }
}