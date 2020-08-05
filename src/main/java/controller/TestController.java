package controller;

import bean.User;
import com.alibaba.fastjson.JSONObject;
import lombok.extern.slf4j.Slf4j;
import mapper.UserMapper;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StopWatch;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import service.TestService;
import system.LoggerProxy;
import utils.CsvUtils;
import utils.RedisLuaUtils;

import javax.servlet.http.HttpServletRequest;
import java.io.File;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.*;

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

    @RequestMapping("/test")
    public String test(HttpServletRequest request) {
        StopWatch stopWatch = new StopWatch();
        stopWatch.start();
        List<User> list1 = userMapper.queryMoneys(1, 1);
        stopWatch.stop();
        System.out.println("时间2花费：" + stopWatch.getTotalTimeSeconds() + "s");
        return "123";
    }


    @RequestMapping("/test1")
    public String test1(HttpServletRequest request,String name) {





        return "123123";
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

        String lockKey=key+":lock";
        try {
            if(redisLuaUtils.tryLock(lockKey,1000)){
                //先判断货物库存是否充足
                String lotteryCountStr=redisLuaUtils.get(key);
                long lotteryCount=0l;
                if(!StringUtils.isBlank(lotteryCountStr)){
                    lotteryCount=Long.valueOf(lotteryCountStr);
                }

                if(lotteryCount<=0){
                    return "没有奖品了";
                }

                System.out.println(redisLuaUtils.incrBy(key, -1l));

            }
        } finally {
            redisLuaUtils.unLock(lockKey);
        }

        return "中奖了";
    }

    @RequestMapping("/setRedis")
    public String setRedis(HttpServletRequest request,String key,String value) {
        redisLuaUtils.set(key,value);

        return "123123";
    }

}
