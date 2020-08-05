

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.google.common.base.Stopwatch;
import io.lettuce.core.ScriptOutputType;
import io.lettuce.core.api.sync.RedisCommands;
import io.lettuce.core.api.sync.RedisScriptingCommands;
import javafx.scene.paint.Stop;
import org.apache.commons.io.IOUtils;
import org.apache.http.client.utils.HttpClientUtils;
import org.junit.Test;
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

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
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
    private RedisLuaUtils redisLuaUtils;


    @Autowired
    private JedisPool jedisPool;


    static int total=1000;
    @Test
    public void test1() throws InterruptedException {
        redisLuaUtils.set("lotterycount1","5000");
//        System.out.println(redisLuaUtils.incrBy("lottery:count:1"));
    }
    @Test
    public void clients() throws InterruptedException {
        String key="lotterycount1";
        int prizeCount=100;//奖品总数
        redisLuaUtils.set(key,prizeCount+"");

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
