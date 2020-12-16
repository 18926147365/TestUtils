package service;

import bean.IModel;
import com.alibaba.fastjson.JSONArray;
import com.google.common.hash.BloomFilter;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import utils.BloomFilterUtils;
import utils.HttpClientUtil;
import utils.RedisLuaUtils;
import utils.ThreadExecutorPool;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.*;
import java.util.concurrent.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @Description:
 * @Author: lihaoming
 * @Date: 2020/10/14 9:32 上午
 */
@Service
@Slf4j
public class ModelService {

    private static String REGEX_CHINESE = "[\u4e00-\u9fa5]";// 中文正则


    public static Map<String,List<String>> dictMap=new HashMap<>();

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Autowired
    private RedisLuaUtils redisLuaUtils;

    public String getBrand(String ua){
        String uaModel=getUAModel(ua);
        if(StringUtils.isNotBlank(uaModel)){
            if("iphone".equalsIgnoreCase(uaModel)){
                return "iPhone";
            }
            String key="model:uaModel:"+uaModel;
            String redisVal=redisLuaUtils.get(key);
            if (StringUtils.isNotBlank(redisVal)) {
                return redisVal;
            }
            String sql="SELECT * FROM `model` WHERE  status=0 and ua_model=? limit 0,1";
            List<Map<String,Object>> list=jdbcTemplate.queryForList(sql,uaModel);
            if(list!=null && list.size()!=0){
                String brand=list.get(0).get("brand")+"";
                redisLuaUtils.set(key,brand,24*60*60);
                return brand;
            }
            reModel(ua);
        }
        return "unknown";
    }
    static ExecutorService pool = new ThreadPoolExecutor(2, 3, 6000,
            TimeUnit.MILLISECONDS,
            new ArrayBlockingQueue<>(5000),
            Executors.defaultThreadFactory(),
            new ThreadPoolExecutor.AbortPolicy());
    static {
        ((ThreadPoolExecutor) pool).allowCoreThreadTimeOut(true);
    }
    public void reFix(){
        String sql="SELECT * FROM `model` WHERE  (STATUS=2  and sp_data='[]') or status=1";
        String updateSql="update model set status=?,brand=?,sp_data=?,brands=?,model=? where id=?";
        List<Map<String,Object>> list=jdbcTemplate.queryForList(sql);
        //设置空闲回收线程

        for (Map<String, Object> map : list) {
            pool.submit(new Runnable() {
                @Override
                public void run() {
                    String ua=map.get("ua")+"";
                    Long id= (Long) map.get("id");
                    String uaModel=getUAModel(ua);
                    int rand=(int)(Math.random()*4000)+1032;
                    try {
                        Thread.sleep(rand);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    List<String> dataList=getContentList(ua);
                    IModel iModel=(getIModel(dataList, uaModel));
                    if(iModel!=null){
                        log.info("解析成功:{} 品牌：{}",uaModel,iModel.getBrand());
                        jdbcTemplate.update(updateSql,iModel.getStatus(),iModel.getBrand(),JSONArray.toJSONString(dataList),JSONArray.toJSONString(iModel.getBrands()),iModel.getModel(),id);
                    }
                }
            });

        }

        String sql1="SELECT * FROM `model` WHERE STATUS=2 AND sp_data!='[]' AND sp_data IS NOT NULL";
        List<Map<String,Object>> list1=jdbcTemplate.queryForList(sql1);
        for (Map<String, Object> map : list1) {
            String ua=map.get("ua")+"";
            Long id= (Long) map.get("id");
            String uaModel=getUAModel(ua);
            List<String> dataList=getContentList(ua);
            IModel iModel=(getIModel(dataList, uaModel));
            if(iModel!=null){
                log.info("解析成功:{} 品牌：{}",uaModel,iModel.getBrand());
                jdbcTemplate.update(updateSql,iModel.getStatus(),iModel.getBrand(),JSONArray.toJSONString(dataList),JSONArray.toJSONString(iModel.getBrands()),iModel.getModel(),id);
            }
        }

    }

    private List<String> getContentList(String ua){
        String uaModel=getUAModel(ua);
        if(StringUtils.isBlank(uaModel)){
            return new ArrayList<>();
        }
        if("iPhone".equals(uaModel)){
            return new ArrayList<>();
        }
//        List<String> dataList= romzhijiaSp(uaModel);
//        if(dataList==null ||dataList.size()==0){
        List<String> dataList=baiduSp(uaModel);
//        }
        return dataList;

    }


    public void reModel(String ua){
        String uaModel=getUAModel(ua);
        if(uaModel==null){
            return;
        }
        BloomFilter<String> bloomFilter=BloomFilterUtils.createOrGetString("phoneModel",1000000,0.02d);
        if (!bloomFilter.put(uaModel)) {
            return;
        }
        String existsSql="select count(1) from model where ua_model = ?";
        if((jdbcTemplate.queryForObject(existsSql, Long.class, uaModel))>0){
            return;
        }

        List<String> dataList=getContentList(ua);
        String insertSql="INSERT INTO model (`status`,ua,ua_model,sp_data,brand,brands,model) VALUES(?,?,?,?,?,?,?);";
        IModel iModel=(getIModel(dataList, uaModel));
        if(iModel!=null){
            int status=iModel.getStatus();
            if(dataList.size()==0){
                status=1;
            }
            log.info("解析成功:{} 品牌：{}",uaModel,iModel.getBrand());
            jdbcTemplate.update(insertSql,status,ua,uaModel,JSONArray.toJSONString(dataList),iModel.getBrand(),JSONArray.toJSONString(iModel.getBrands()),iModel.getModel());
        }
    }


    private List<String> romzhijiaSp(String uaModel){
        String  url = null;
        try {
            url="http://so.romzhijia.net/cse/search?s=12725138872909822094&q="+URLEncoder.encode(uaModel,"UTF-8");
            return httpClientSp(url);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return null;
    }

    private List<String> baiduSp(String uaModel){
        String  url = null;
        try {
            url = "http://www.baidu.com/s?wd="+ URLEncoder.encode(uaModel,"UTF-8");
            return httpClientSp(url);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return null;
    }

    private  List<String> httpClientSp(String url){
        List<String> spList=new ArrayList<>();

        String htmlStr= HttpClientUtil.httpBaiduGet(url);

        Document doc = Jsoup.parse(htmlStr);
        Elements elements=doc.getElementsByClass("c-abstract");
        for (Element element : elements) {
            String text=element.text();
            spList.add(text);
        }
        if(elements.size()==0){
            if(htmlStr.contains("<title>百度安全验证</title>")){
                log.info("百度安全验证");
            }
        }
        return spList;


    }



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

    private  IModel getIModel(List<String> uaModellist,String uaModel) {
        try {
            if(dictMap==null || dictMap.size()==0){
                synchronized (dictMap){
                    if(dictMap.size()==0){
                        dictMap=getDictMap();
                    }
                }
            }
            Map<String, Integer> totalMap = new HashMap<>();
            Map<String, Integer> modelMap = new HashMap<>();
            for (String content : uaModellist) {
                for (String brand : dictMap.keySet()) {
                    List<String> str = dictMap.get(brand);
                    for (String s : str) {
                        if (content.toUpperCase().contains(s.toUpperCase())) {
                            getTotalMap(totalMap, brand);
                            //-------获取手机型号
                            String contentUp=content.toUpperCase();
                            String sUp=s.toUpperCase();
                            String modelStr = content.substring(contentUp.indexOf(sUp), (contentUp.indexOf(sUp) + 20)>content.length()?contentUp.length():(contentUp.indexOf(sUp) + 20));
                            Pattern pat = Pattern.compile(REGEX_CHINESE);
                            Matcher mat = pat.matcher(modelStr);
                            modelStr = (mat.replaceAll(" "));
                            modelStr = modelStr.replaceAll(",", " ").replace("。", " ").replace(":", " ").replace("  ", " ");
                            modelStr = modelStr.replace(s, "").toUpperCase();
                            String[] ll = modelStr.split(" ");
                            for (String s1 : ll) {
                                s1 = s1.replace(" ", "");
                                if (org.apache.commons.lang3.StringUtils.isBlank(s1) || s1.length() <= 1 || s1.equalsIgnoreCase(uaModel)) {
                                    continue;
                                }
                                Integer count = modelMap.get(s1);
                                if (count == null) {
                                    count = 0;
                                }
                                modelMap.put(s1, count + 1);
                            }
                            //获取手机型号结束
                        }
                    }
                }
            }


            List<Map.Entry<String, Integer>> list = new ArrayList<>(totalMap.entrySet());
            Collections.sort(list, new Comparator<Map.Entry<String, Integer>>() {
                //升序排序
                public int compare(Map.Entry<String, Integer> o1, Map.Entry<String, Integer> o2) {
                    return o2.getValue().compareTo(o1.getValue());
                }
            });


            List<Map.Entry<String, Integer>> list1 = new ArrayList<>(modelMap.entrySet());
            Collections.sort(list1, new Comparator<Map.Entry<String, Integer>>() {
                //升序排序
                public int compare(Map.Entry<String, Integer> o1, Map.Entry<String, Integer> o2) {
                    return o2.getValue().compareTo(o1.getValue());
                }
            });
            IModel iModel = new IModel();
            iModel.setStatus(2);
            if (list != null && list.size() != 0) {
                iModel.setStatus(0);
                iModel.setBrands(list);
                iModel.setBrand(list.get(0).getKey());
                if (list1 != null && list1.size() != 0) {
                    iModel.setModel(list1.get(0).getKey());
                }
                return iModel;
            }
            return iModel;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }


    private Map<String,List<String>> getDictMap(){
        Map<String,List<String>> dictMap=new HashMap<>();
        String sql="SELECT * FROM `model_dict`";
        List<Map<String,Object>> dictList= jdbcTemplate.queryForList(sql);
        for (Map<String, Object> map : dictList) {
            String brand= (String) map.get("brand");
            String keywork= (String) map.get("keyword");
            List<String> list=Arrays.asList(keywork.split(","));
            dictMap.put(brand,list);
        }
        return dictMap;
    }

    private  void getTotalMap(Map<String, Integer> totalMap, String brand) {
        Integer count = totalMap.get(brand);
        if (count == null) {
            count = 0;
        }
        totalMap.put(brand, count + 1);
    }
}
