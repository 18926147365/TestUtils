package controller;

import bean.User;
import bean.UserInfo;
import com.alibaba.fastjson.JSONObject;
import com.google.common.hash.BloomFilter;
import com.sun.corba.se.spi.orbutil.threadpool.ThreadPoolManager;
import lombok.extern.slf4j.Slf4j;
import mapper.UserMapper;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StopWatch;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import service.TestService;
import system.LoggerProxy;
import utils.BloomFilterUtils;
import utils.CsvUtils;
import utils.RedisLuaUtils;

import javax.servlet.http.HttpServletRequest;
import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;
import java.rmi.server.UID;
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

    @Autowired
    private TestService testService;

    @Autowired
    private RedisLuaUtils redisLuaUtils;

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
        List<User> list=new ArrayList<>();
        for (int i = 0; i < 100; i++) {
            List<User> list1 = userMapper.queryMoneys(10*i, 2000);
            list.addAll(list1);
        }
        stopWatch.stop();


        return JSONObject.toJSONString(list);
    }


   static ExecutorService pool = new ThreadPoolExecutor(20, 20, 5000,
            TimeUnit.MILLISECONDS,
            new LinkedBlockingQueue<>(),
            Executors.defaultThreadFactory(),
            new ThreadPoolExecutor.AbortPolicy());
    static {
        ((ThreadPoolExecutor) pool).allowCoreThreadTimeOut(true);
    }


    @RequestMapping("/test")
    public String test(HttpServletRequest request) {




        return "123";
    }

    private static String randStr="";

    static {
        randStr=new Date().getTime()+"";
    }


    @RequestMapping("/test1")
    public String test1(HttpServletRequest request,String name) throws ExecutionException, InterruptedException {



        String key="test:cache:11118";
//        System.out.println(redisLuaUtils.hget(key,"state"));
        String val=redisLuaUtils.hgetCache(key, "默认值", new Supplier<String>() {
            @Override
            public String get() {
                return testService.getName();
            }
        },2500,1000);

        return val;
    }






    private void saveCSV(List<User> list)  {
        Object[] header = {"id", "name", "money","businessId"};


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

    public  char getRandomChar() {
        return (char) (0x4e00 + (int) (Math.random() * (0x9fa5 - 0x4e00 + 1)));
    }



    @RequestMapping("/peopleCount")
    public String peopleCount(HttpServletRequest request) throws ExecutionException, InterruptedException {


        String key="peopleCount:3";
        String val=redisLuaUtils.hgetCache(key, "默认值", new Supplier<String>() {
            @Override
            public String get() {
                System.out.println("查询db.....");
                return userMapper.countMoney()+"";
            }
        },6000,12000);

        return val;
    }

    @RequestMapping("/batchSaveUser")
    public String batchSaveUser(){
        ExecutorService pool = new ThreadPoolExecutor(5, 5, 5000,
                TimeUnit.MILLISECONDS,
                new LinkedBlockingQueue<>(),
                Executors.defaultThreadFactory(),
                new ThreadPoolExecutor.AbortPolicy());

        for (int k = 0; k < 95000; k++) {
            final int d=k;
            pool.submit(new Runnable() {
                @Override
                public void run() {
                    System.out.println("当前批次："+d);
                    List<User> list=new ArrayList<>();
                    for (int i = 0; i < 1000; i++) {
                        User user=new User();
                        user.setName(String.valueOf(getRandomChar())+String.valueOf(getRandomChar())+String.valueOf(getRandomChar()));
                        long businessId=(long)(Math.random()*100)+1;
                        user.setMoney(BigDecimal.valueOf(Math.random()*100000+200));
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
        String lockKey="seckill:count1";
        long result=redisLuaUtils.evalsha(RedisLuaUtils.ScriptLoadEnum.SECKILL,Long.class,lockKey,"-1");
        if(result==-1){
            System.out.println("没有了:"+redisLuaUtils.get(lockKey));
        }

        return "1";
    }



    @RequestMapping("/lottery")
    public String lottery(HttpServletRequest request,String key,String name) throws InterruptedException {


        long obj=(redisLuaUtils.evalsha(RedisLuaUtils.ScriptLoadEnum.INCRBYGETMAX, Long.class, key, "10000", "1"));
        if(obj==-1){
            System.out.println("没有奖品了");
        }else{
            System.out.println(obj);
        }

        return "中奖了";
    }

    @RequestMapping("/setRedis")
    public String setRedis(HttpServletRequest request,String key,String value) {
        redisLuaUtils.set(key,value);

        return "123123";
    }



    @RequestMapping("/test11")
    public String test11(HttpServletRequest request){

        return "123";
    }

    static ExecutorService mypool = new ThreadPoolExecutor(20, 20, 5000,
            TimeUnit.MILLISECONDS,
            new LinkedBlockingQueue<>(),
            Executors.defaultThreadFactory(),
            new ThreadPoolExecutor.AbortPolicy());


    @GetMapping("/test22")
    public String test22(HttpServletRequest request){

        String key="test:@2222:21";
        redisLuaUtils.del(key);
        int size=1000*100;
        CountDownLatch countDownLatch=new CountDownLatch(size);
        for (int i = 0; i < size; i++) {
            mypool.submit(new Runnable() {
                @Override
                public void run() {
                    redisLuaUtils.pfadd(key, UUID.randomUUID().toString());
                    countDownLatch.countDown();
                }
            });
        }
        try {
            countDownLatch.await();
            long count=redisLuaUtils.pfcount(key);
            System.out.println("待插入数据总数："+size);
            System.out.println("hyper统计后："+count);
            System.out.println("误算率:"+(((double)((Math.abs(size-count)))/(double)(count))*100)+"%");

        } catch (InterruptedException e) {
            e.printStackTrace();
        }



        return "";




    }

    @RequestMapping("/test33")
    public void test33(){
        String key="phones:1";

        BloomFilter<String> firstFiter=BloomFilterUtils.createOrGetString(key,10000000000l,0.03);
        BloomFilter<String> secoeFiter=BloomFilterUtils.createOrGetString(key,100000000l,0.03);
        //18926147365
        //10000000000
        //20000000000
        org.apache.commons.lang3.time.StopWatch stopWatch=new org.apache.commons.lang3.time.StopWatch();
        stopWatch.start();
        System.out.println("开始");
        long f1=0;
        long f2=0;
        for (long i = 0; i < 10000000000l; i++) {
            if(!firstFiter.put((10000000000l+i)+"")){
                f1++;
                if(!secoeFiter.put((10000000000l+i)+"")){
                    f2++;
                }
            }
            if(i%1000000==0){
                System.out.println((double) i/(double) (10000000000l)+"%");
            }

        }

        System.out.println("f1:"+f1);
        System.out.println("f2:"+f2);

        stopWatch.stop();
        System.out.println("共耗时:"+stopWatch.getTime());

    }


}