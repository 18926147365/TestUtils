

import bean.Fund;
import bean.FundDayLog;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.google.common.base.Stopwatch;
import io.lettuce.core.ScriptOutputType;
import io.lettuce.core.api.sync.RedisCommands;
import io.lettuce.core.api.sync.RedisScriptingCommands;
import javafx.scene.paint.Stop;
import javassist.ClassPool;
import javassist.CtClass;
import javassist.CtMethod;
import javassist.NotFoundException;
import jdk.nashorn.api.scripting.ScriptObjectMirror;
import lombok.SneakyThrows;
import mapper.FundDayLogMapper;
import mapper.FundMapper;
import mapper.FundTodayLogMapper;
import org.apache.commons.io.IOUtils;
import org.apache.http.client.utils.HttpClientUtils;
import org.checkerframework.checker.units.qual.C;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.rules.Timeout;
import org.junit.runner.RunWith;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.util.StopWatch;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import utils.Application;
import utils.HttpClientUtil;
import utils.RedisLuaUtils;

import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.concurrent.*;

/**
 * @author 李浩铭
 * @date 2020/7/30 11:57
 * @descroption
 */

@RunWith(SpringRunner.class)
@SpringBootTest(classes = Application.class, webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class test {

    @Autowired
    private static RedisLuaUtils redisLuaUtils;


    @Autowired
    private JedisPool jedisPool;


    static int total=1000;
    @Test
    public void Run() throws Exception {
        Integer a = 1;
        Assert.assertEquals(1,1);
    }

    @Autowired
    private FundMapper fundMapper;
    @Autowired
    private FundDayLogMapper fundDayLogMapper;
    @Test
    public void clients() throws InterruptedException {
        ExecutorService pool = new ThreadPoolExecutor(3, 3, 500,
                TimeUnit.MILLISECONDS,
                new ArrayBlockingQueue<>(50),
                Executors.defaultThreadFactory(),
                new RejectedExecutionHandler() {
                    @SneakyThrows
                    @Override
                    public void rejectedExecution(Runnable r, ThreadPoolExecutor executor) {
                        while (true) {
                            int capacity = executor.getQueue().remainingCapacity();//任务队列剩余容量
                            if (capacity != 0) {
                                break;
                            }
                            Thread.sleep(300);//线程休眠
                        }
                        if (!executor.isShutdown()) {
                            r.run();
                        }
                    }
                });
        ((ThreadPoolExecutor) pool).allowCoreThreadTimeOut(true);
        List<Fund> list = fundMapper.queryAll(0);
        CountDownLatch countDownLatch = new CountDownLatch(list.size());
        for (Fund fund : list) {
            pool.submit(new Runnable() {
                @SneakyThrows
                @Override
                public void run() {
                    updateDayLog(fund.getFundCode());
                }
            });
        }

        countDownLatch.await();

    }

    @Test
    public void dd() throws ParseException {
        for (Fund fund : fundMapper.queryAll(0)) {
            updateDayLog(fund.getFundCode());
        }
        List<String> fundCodeList = Arrays.asList("000960");
        for (String s : fundCodeList) {

        }
    }


    private void updateDayLog(String fundCode) throws ParseException {
        String result = HttpClientUtil.get("http://fund.eastmoney.com/pingzhongdata/"+fundCode+".js");
        ScriptEngineManager manager = new ScriptEngineManager();
        ScriptEngine engine = manager.getEngineByName("js");
        try {
            engine.eval(result);
        } catch (ScriptException e) {
            e.printStackTrace();
        }
        ScriptObjectMirror scriptObjectMirror = (ScriptObjectMirror) engine.get("Data_netWorthTrend");
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        SimpleDateFormat sdf1 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        BigDecimal calcTemp = new BigDecimal("0");
        double total = 2000;
        for (String s : scriptObjectMirror.keySet()) {
            ScriptObjectMirror mirror = (ScriptObjectMirror) scriptObjectMirror.get(s);
            Double datetime = (Double) mirror.get("x");
            Object obj = mirror.get("equityReturn");
            Double equityReturn = new Double("0");
            if (obj instanceof Integer) {
                equityReturn = Double.valueOf((Integer) mirror.get("equityReturn"));
            } else if (obj instanceof Double) {
                equityReturn = (Double) obj;
            }
            BigDecimal d1 = new BigDecimal(equityReturn);
            Date d2 = sdf.parse(sdf.format(new Date(datetime.longValue())));
            if(datetime.longValue()==1624896000000l){
                FundDayLog dayLog = new FundDayLog();
                dayLog.setFundCode(fundCode);
                dayLog.setGszzl(d1);
                dayLog.setGztime(d2);
                fundDayLogMapper.insert(dayLog);
                System.out.println("完成:" + fundCode + ",gztime:" + d2);
            }

        }
        System.out.println("完成:"+fundCode);
    }
    @Test
    public void getAndDel() throws IOException {

        String key="myNameTests";
        Jedis jedis=jedisPool.getResource();
        jedis.set(key,"123");

        try (InputStream input = test.class.getResourceAsStream("/lua/getAndDel.lua")) {
            String scriptLoad = jedis.scriptLoad(IOUtils.toString(input, StandardCharsets.UTF_8));

             System.out.println(jedis.evalsha(scriptLoad,1,key));
            System.out.println("获取值："+jedis.get(key));
        }catch (Exception e){
            e.printStackTrace();
        }

    }


    @Test
    public void batchInsert(){
        Jedis jedis=jedisPool.getResource();

        StopWatch stopWatch=new StopWatch();
        stopWatch.start();
        String key="testss";

        try (InputStream input = test.class.getResourceAsStream("/lua/batchSet.lua")) {
            String scriptLoad = jedis.scriptLoad(IOUtils.toString(input, StandardCharsets.UTF_8));

            for (int i = 0; i < 2000; i++) {
                List<String> list=new ArrayList<>();
                for (int j = 0; j < 100; j++) {
                    list.add(((i*100)+j)+"");//1484ms
                }
                System.out.println(jedis.evalsha(scriptLoad,2,key, JSONObject.toJSONString(list)));
            }
        }catch (Exception e){
            e.printStackTrace();
        }
        stopWatch.stop();
        System.out.println(stopWatch.getTotalTimeMillis()+"ms");
    }



    @Test
    public void test(){
        String key="testsss";
        Jedis jedis=jedisPool.getResource();

        System.out.println(jedis.get("testnames1999990"));
    }

}
