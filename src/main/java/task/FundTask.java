package task;

import bean.Fund;
import com.alibaba.fastjson.JSONObject;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import service.FundService;
import utils.HttpClientUtil;

import java.io.IOException;
import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @Description:
 * @Author: lihaoming
 * @Date: 2021/3/10 下午3:20
 */

@Configuration
@Component
@EnableScheduling
@Slf4j
public class FundTask {


    @Autowired
    private FundService fundService;

    @Scheduled(cron="0 31 11 * * ?")
    public void runTask1(){
        try {
            execute("休盘");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    @Scheduled(cron="0 01 15 * * ?")
    public void runTask2(){
        try {
            execute("收盘");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    public void execute(String title) throws ParseException, IOException {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        SimpleDateFormat daySdf = new SimpleDateFormat("yyyy-MM-dd");
        SimpleDateFormat sanSdf = new SimpleDateFormat("yyyy-MM-dd 15:00:00");
        SimpleDateFormat siSdf = new SimpleDateFormat("yyyy-MM-dd 16:00:00");
        List<Fund> fundList = fundService.queryAll();
        StringBuilder tipBuilder = new StringBuilder();
        int upFundTotal = 0;
        int downFundTotal = 0;
        int fundTotal = 0;

        Date now = new Date();
        for (Fund fund : fundList) {
            String fundCode = fund.getFundCode();
            String result = (HttpClientUtil.httpGet("http://fundgz.1234567.com.cn/js/" + fundCode + ".js"));
            String patterStr = "(jsonpgz\\()(.*)(\\);)";
            Matcher matcher = Pattern.compile(patterStr).matcher(result);
            if (matcher.find()) {
                String json = matcher.group(2);
                log.info("数据更新:" + json);
                JSONObject fundJson = (JSONObject.parseObject(json));
                String fundName = fundJson.getString("name");
                String gztime = fundJson.getString("gztime") + ":00";
                BigDecimal gszzl = fundJson.getBigDecimal("gszzl");
                if (gszzl.doubleValue() > 0) {
                    upFundTotal++;
                } else {
                    downFundTotal++;
                }
                if (daySdf.parse(gztime).getTime() == daySdf.parse(daySdf.format(now)).getTime()) {
                    fundTotal++;
                }else{
                    break;
                }
                Date gzDate = sdf.parse(gztime);
                fundService.updateFund(fundCode, gszzl, gzDate);
                tipBuilder.append("[" + gszzl + "%]");
            }
        }
        if(fundTotal>0){
            String downUpTip = "涨:" + upFundTotal + "支&&跌:" + downFundTotal + "支";
            Runtime.getRuntime().exec("sh /Users/lihaoming/data/shell/notify.sh "+title+" " + tipBuilder + " " + downUpTip);
        }


    }
}
