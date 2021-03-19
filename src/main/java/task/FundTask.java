package task;

import bean.Fund;
import bean.NettyMsg;
import com.alibaba.fastjson.JSONObject;
import dingtalk.DingTalkSend;
import dingtalk.DingText;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import jdk.nashorn.api.scripting.ScriptObjectMirror;
import lombok.extern.slf4j.Slf4j;
import mapper.FundMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import service.FundService;
import utils.HttpClientUtil;
import utils.NettyServerHandler;
import utils.RSAUtils;

import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;
import java.io.IOException;
import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
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

    @Autowired
    private FundMapper fundMapper;


    @Scheduled(cron = "0 32 11 * * ?")
    public void runTask1() {
        try {
            execute("休盘");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Scheduled(cron = "0 03 15 * * ?")
    public void runTask2() {
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
        double totalCalcMoney = 0d;
        Date now = new Date();
        Map<String, FundStatModel> fundStatMap = new HashMap<>();
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
                if (daySdf.parse(gztime).getTime() == daySdf.parse(daySdf.format(now)).getTime()) {
                    fundTotal++;
                } else {
                    break;
                }
                Date gzDate = sdf.parse(gztime);
                double calcAmount = calcFund(fund);
                double calcValue = (calcAmount * gszzl.doubleValue() / 100);
                if (!fundStatMap.containsKey(fundCode)) {
                    if (gszzl.doubleValue() > 0) {
                        upFundTotal++;
                    } else {
                        downFundTotal++;
                    }
                    FundStatModel statModel = new FundStatModel();
                    statModel.setGszzl(gszzl);
                    statModel.setCalcAmount(calcValue);
                    statModel.setFundCode(fundCode);
                    statModel.setFundName(fundName);
                    fundStatMap.put(fundCode, statModel);
                } else {
                    FundStatModel statModel = fundStatMap.get(fundCode);
                    statModel.setCalcAmount(statModel.getCalcAmount() + calcValue);
                    fundStatMap.put(fundCode, statModel);
                }
                totalCalcMoney = totalCalcMoney + calcValue;
                fundService.updateFund(fund.getId(), gszzl, gzDate);
            }
        }
        if (fundTotal > 0) {
            for (String fundCode : fundStatMap.keySet()) {
                FundStatModel statModel = fundStatMap.get(fundCode);
                String gszzStr = statModel.getGszzl().toString();
                String calcMoney = (statModel.getCalcAmount().intValue())+"";
                if (statModel.getGszzl().doubleValue() > 0) {
                    gszzStr = "+" + statModel.getGszzl().toString();
                }
                if((statModel.getCalcAmount().intValue())>0){
                    calcMoney = "+"+(statModel.getCalcAmount().intValue());
                }
                tipBuilder.append("[").append(gszzStr).append("%]:");
                tipBuilder.append(statModel.getFundName());
                tipBuilder.append("(").append(calcMoney).append("元)\n");
            }

            String dateDay = daySdf.format(new Date());
            String content = title + "%s\n涨:%s支 跌:%s支\n收益:%s元\n";
            content = String.format(content, dateDay, upFundTotal, downFundTotal, (int) totalCalcMoney);
            content += tipBuilder.toString();
            DingText dingText = new DingText(content);
            dingText.setAtAll(true);
            DingTalkSend dingTalkSend = new DingTalkSend(dingText);
            dingTalkSend.setAccessToken("e13e4148cb80bb1927cd5d9e8f340590b7df06780587c0233c9fa9b996647a9a");
            dingTalkSend.send();
        }
    }

    private double calcFund(Fund fund) {
        String result = HttpClientUtil.get("http://fund.eastmoney.com/pingzhongdata/" + fund.getFundCode() + ".js");
        ScriptEngineManager manager = new ScriptEngineManager();
        ScriptEngine engine = manager.getEngineByName("js");
        try {
            engine.eval(result);
        } catch (ScriptException e) {
            e.printStackTrace();
        }
        ScriptObjectMirror scriptObjectMirror = (ScriptObjectMirror) engine.get("Data_netWorthTrend");
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        BigDecimal calcTemp = new BigDecimal("0");
        if (fund.getCalcAmount() == null) {
            fund.setCalcAmount(fund.getPayAmount());
        }
        if (fund.getCalcTime() == null) {
            fund.setCalcTime(fund.getPayTime());
        }
        double total = fund.getCalcAmount().doubleValue();
        Date lastDate = null;
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
            if (datetime.longValue() > fund.getCalcTime().getTime()) {
                calcTemp = calcTemp.add(BigDecimal.valueOf(equityReturn));
                String after = total + "";
                total = total + total * (equityReturn.doubleValue() / 100);
                lastDate = new Date(datetime.longValue());
                log.info(sdf.format(new Date(datetime.longValue())) + "  " + equityReturn + " 原有金额:" + after + " 变更后:" + total);
            }
        }
        if (lastDate != null) {
            fundMapper.updateCalcFund(fund.getId(), new BigDecimal(total), lastDate);
        }
        return total;
    }

    class FundStatModel {
        private String fundCode;

        private String fundName;

        private Double calcAmount;

        private BigDecimal gszzl;

        public BigDecimal getGszzl() {
            return gszzl;
        }

        public void setGszzl(BigDecimal gszzl) {
            this.gszzl = gszzl;
        }

        public String getFundCode() {
            return fundCode;
        }

        public void setFundCode(String fundCode) {
            this.fundCode = fundCode;
        }

        public String getFundName() {
            return fundName;
        }

        public void setFundName(String fundName) {
            this.fundName = fundName;
        }

        public Double getCalcAmount() {
            return calcAmount;
        }

        public void setCalcAmount(Double calcAmount) {
            this.calcAmount = calcAmount;
        }
    }
}
