package controller;

import bean.IModel;
import bean.ModelResponse;
import bean.User;
import com.alibaba.fastjson.JSONObject;
import com.google.common.hash.BloomFilter;
import com.google.common.util.concurrent.RateLimiter;
import lombok.extern.slf4j.Slf4j;
import mapper.UserMapper;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import service.ModelService;
import service.TestService;
import utils.CsvUtils;
import utils.RedisLuaUtils;
import utils.ThreadExecutorPool;

import javax.servlet.http.HttpServletRequest;
import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.*;
import java.util.concurrent.*;
import java.util.function.Supplier;

/**
 * @author 李浩铭
 * @date 2020/7/9 17:00
 * @descroption
 */
@RestController
@RequestMapping("model")
@Slf4j
public class ModelController {





    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Autowired
    private ModelService modelService;


    @Autowired
    private RedisLuaUtils redisLuaUtils;

    private  String getUAModel(String ua){
        if(ua.toLowerCase().contains("linux;")){//安卓判断
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
        }else if(ua.toLowerCase().contains("iPhone;")){
            return "iPhone";
        }
        return null;
    }

    //200ms 产生10个桶
    static RateLimiter modelGetLimiter=RateLimiter.create(15,500,TimeUnit.MILLISECONDS);

    @RequestMapping("/get")
    public ModelResponse get(String ua){
        log.info("请求查询brand:{}",ua);
        ModelResponse modelResponse=new ModelResponse();

        if (modelGetLimiter.tryAcquire(1)) {
            String brand=modelService.getBrand(ua);
            modelResponse.setBrand(brand);
            modelResponse.setCode(-1);
            if(StringUtils.isNotBlank(brand) && !brand.equalsIgnoreCase("unknown")){
                modelResponse.setCode(0);
            }
        }else{
            modelResponse.setCode(-1);
            modelResponse.setMsg("接口请求过于频繁，请稍后再试！");
        }
        return modelResponse;
    }

    @RequestMapping("/get1")
    public String get1(){
        String countSql="select count(1) from model";
        String modelIndexStr=redisLuaUtils.get("modelIndex");
        Long count=jdbcTemplate.queryForObject(countSql,Long.class);
        return "已查询条数："+modelIndexStr+ " 已解析条数:"+count;
    }

    @RequestMapping("/set")
    public String set(Integer index){
        redisLuaUtils.set("modelIndex",index+"");
        return "成功";
    }

    @RequestMapping("/fix")
    public void fix(){
        modelService.reFix();
    }

    @RequestMapping("/start")
    public void start(){
        String modelIndexStr=redisLuaUtils.get("modelIndex");
        Long modelIndex=0l;
        if (StringUtils.isNotBlank(modelIndexStr)) {
            modelIndex=Long.valueOf(modelIndexStr);
        }
        long size=100;
        int poolSize=3;//查询线程数
        ExecutorService pool=ThreadExecutorPool.getExecutorService("modelPool",poolSize,poolSize);
        String sql="SELECT model FROM access_log limit ?,?";
        while (true){
            List<Map<String,Object>> list=jdbcTemplate.queryForList(sql,modelIndex,size);
            log.info("查询数据库 当前下标:{}",modelIndex);
            if(list==null || list.size()==0){
                break;
            }
            CountDownLatch countDownLatch=new CountDownLatch(list.size());
            for (Map<String, Object> stringObjectMap : list) {
                String ua=(stringObjectMap.get("model"))+"";
                pool.submit(new Runnable() {
                    @Override
                    public void run() {
                        modelService.reModel(ua);
                        countDownLatch.countDown();
                    }
                });
            }
            try {
                countDownLatch.await();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            modelIndex=redisLuaUtils.incrBy("modelIndex",size);
        }

    }

    @RequestMapping("/reflushDict")
    public String reflushDict(){
        ModelService.dictMap=new HashMap<>();
        return "刷新dict成功";
    }

}