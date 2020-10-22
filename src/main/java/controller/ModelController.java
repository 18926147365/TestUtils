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
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import service.ModelService;
import service.TestService;
import utils.CsvUtils;
import utils.HttpClientUtil;
import utils.RedisLuaUtils;
import utils.ThreadExecutorPool;

import javax.servlet.http.HttpServletRequest;
import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.math.BigDecimal;
import java.net.URLEncoder;
import java.util.*;
import java.util.concurrent.*;
import java.util.function.Supplier;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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
                    String[] uaModels=uaTrim.split("Build/");
                    if(uaModels.length!=0){
                        return (uaModels[0]).trim();
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

    @RequestMapping("/startSP")
    public void startSP() throws Exception {
        String papgeIndexRedisKey="startSP2:index";
        long pageIndex=10;
        int index=0;
        String  url = "http://www.baidu.com/s?wd="+ URLEncoder.encode("Mozilla/5.0 (Linux; Android","UTF-8");
        while (true){
            int rand=(int)(Math.random()*3000)+1200;
            Thread.sleep(rand);
            pageIndex=redisLuaUtils.incrBy(papgeIndexRedisKey,10l);
            String htmlResp=HttpClientUtil.httpBaiduGet(url+"&pn="+pageIndex);
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
                log.info("爬取网页地址:{}",href);
                String modelResp= null;
                index++;
                if(index%20==0){
                    Thread.sleep(20000);
                }
                try {
                    Thread.sleep((int)(Math.random()*4000)+1200);
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
                    if(org.apache.commons.lang3.StringUtils.isBlank(uaModel)){
                        continue;
                    }
                    log.info("抓取网页数据model:{}",uaModel);
                    modelService.reModelAsync(ua);
                }
            }
        }



    }

}