package task;

import bean.Fund;
import bean.FundLog;
import bean.FundTalkConf;
import bean.NettyMsg;
import com.alibaba.fastjson.JSONObject;
import dingtalk.DingTalkSend;
import dingtalk.DingText;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import jdk.nashorn.api.scripting.ScriptObjectMirror;
import lombok.extern.slf4j.Slf4j;
import mapper.FundLogMapper;
import mapper.FundMapper;
import mapper.FundTalkConfMapper;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import service.FundService;
import utils.HttpClientUtil;
import utils.NettyServerHandler;
import utils.RSAUtils;
import utils.RedisLuaUtils;

import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;
import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
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
    private RedisLuaUtils redisLuaUtils;
    @Autowired
    private FundService fundService;

    @Autowired
    private FundMapper fundMapper;

    @Autowired
    private FundLogMapper fundLogMapper;

    @Autowired
    private FundTalkConfMapper fundTalkConfMapper;

    @Scheduled(cron = "0 32 11 * * ?")
    public void runTask1() {
        try {
            execute(1);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Scheduled(cron = "0 03 15 * * ?")
    public void runTask2() {
        try {
            execute(2);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void execute(Integer type) throws ParseException, IOException {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        SimpleDateFormat daySdf = new SimpleDateFormat("yyyy-MM-dd");


        List<Fund> fundList = fundService.queryAll();
        StringBuilder tipBuilder = new StringBuilder();
        int upFundTotal = 0;
        int downFundTotal = 0;
        int fundTotal = 0;
        double totalCalcMoney = 0d;
        Date now = new Date();
        Map<String, FundStatModel> fundStatMap = new HashMap<>();
        Map<String, String> fundResultCache = new HashMap<>();
        for (Fund fund : fundList) {
            String fundCode = fund.getFundCode();
            String result = fundResultCache.get(fundCode);
            if (StringUtils.isBlank(result)) {
                result = (HttpClientUtil.httpGet("http://fundgz.1234567.com.cn/js/" + fundCode + ".js"));
            }
            String patterStr = "(jsonpgz\\()(.*)(\\);)";
            Matcher matcher = Pattern.compile(patterStr).matcher(result);
            if (matcher.find()) {
                fundResultCache.put(fundCode, result);
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
                String fundKeyName = fundCode + fund.getBelongName();
                if (!fundStatMap.containsKey(fundKeyName)) {
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
                    statModel.setBelongName(fund.getBelongName());
                    statModel.setFundId(fund.getId());
                    fundStatMap.put(fundKeyName, statModel);
                } else {
                    FundStatModel statModel = fundStatMap.get(fundCode);
                    statModel.setCalcAmount(statModel.getCalcAmount() + calcValue);
                    fundStatMap.put(fundKeyName, statModel);
                }
                totalCalcMoney = totalCalcMoney + calcValue;
                fundService.updateFund(fund.getId(), gszzl, gzDate);
            }
        }
        if (fundTotal > 0) {
            for (String fundKeyName : fundStatMap.keySet()) {
                FundStatModel statModel = fundStatMap.get(fundKeyName);
                log.info("结算基金代码:" + statModel.getFundCode() + ",基金名称:" + statModel.getFundName() +
                        ",归属者:" + statModel.getBelongName() + ",金额变化:" + statModel.getCalcAmount().longValue() + "元");
                String gszzStr = statModel.getGszzl().toString();
                String calcMoney = (statModel.getCalcAmount().intValue()) + "";
                if (statModel.getGszzl().doubleValue() > 0) {
                    gszzStr = "+" + statModel.getGszzl().toString();
                }
                if ((statModel.getCalcAmount().intValue()) > 0) {
                    calcMoney = "+" + (statModel.getCalcAmount().intValue());
                }
                tipBuilder.append("[").append(gszzStr).append("%]:");
                tipBuilder.append(statModel.getFundName());
                tipBuilder.append("(").append(calcMoney).append("元)\n");
                updateFundLog(statModel);
            }

//            String dateDay = daySdf.format(new Date());
//            String content = title + "%s\n涨:%s支 跌:%s支\n收益:%s元\n";
//            content = String.format(content, dateDay, upFundTotal, downFundTotal, (int) totalCalcMoney);
//            content += tipBuilder.toString();
//            DingText dingText = new DingText(content);
//            dingText.setAtAll(true);
//            DingTalkSend dingTalkSend = new DingTalkSend(dingText);
//            dingTalkSend.setAccessToken("e13e4148cb80bb1927cd5d9e8f340590b7df06780587c0233c9fa9b996647a9a");
//            dingTalkSend.send();

            updateFundNotify(fundStatMap);//钉钉通知
            notifyTalk(type);
        }
    }

    private void updateFundNotify(Map<String, FundStatModel> fundStatMap) {
        for (String fundCode : fundStatMap.keySet()) {
            FundStatModel statModel = fundStatMap.get(fundCode);
            fundMapper.updateEarFund(statModel.getFundId(), BigDecimal.valueOf(statModel.getCalcAmount()), new Date());
        }
    }

    public void notifyTalk(Integer type) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        List<FundTalkConf> talkConfList = fundTalkConfMapper.queryAll();
        Map<String, List<Integer>> groupMap = new HashMap<>();
        for (FundTalkConf fundTalkConf : talkConfList) {
            String accessToken = fundTalkConf.getAccessToken();
            if (!groupMap.containsKey(accessToken)) {
                groupMap.put(accessToken, new ArrayList<>());
            }
            groupMap.get(accessToken).add(fundTalkConf.getFundId());
        }
        for (String accessToken : groupMap.keySet()) {
            List<Integer> fundIds = groupMap.get(accessToken);
            int up = 0, down = 0;
            BigDecimal earTotal = new BigDecimal("0");//总收益
            StringBuilder fundTipBuilder = new StringBuilder();
            BigDecimal calcTotal = new BigDecimal("0");//剩余金额
            for (Integer fundId : fundIds) {
                Fund fund = fundMapper.queryById(fundId);
                if (fund.getEarAmount().doubleValue() > 0) {
                    up++;
                } else {
                    down++;
                }
                calcTotal = calcTotal.add(fund.getCalcAmount());
                earTotal = earTotal.add(fund.getEarAmount());
                String moneyStr = fund.getEarAmount().setScale(2, RoundingMode.HALF_DOWN).toString();
                if (fund.getEarAmount().doubleValue() > 0) {
                    moneyStr = "+" + moneyStr;
                } else {
                }


                String gzzlStr = fund.getGszzl().setScale(3, RoundingMode.HALF_DOWN).toString();
                if (fund.getGszzl().doubleValue() > 0) {
                    gzzlStr = "+" + gzzlStr;
                }
                gzzlStr += "%";
                String content = String.format("[%s]：%s(%s元)", gzzlStr, fund.getFundName(), moneyStr);
                fundTipBuilder.append(content + "\n");
            }
            BigDecimal earAmount = new BigDecimal("0");
            if (type == 1) {
                redisLuaUtils.set(accessToken + ":erarToal", earTotal.setScale(2, RoundingMode.HALF_DOWN).toString(), 60 * 60 * 8);
                earAmount = earTotal.setScale(2, RoundingMode.HALF_DOWN);
            } else if (type == 2) {
                String lastEar = redisLuaUtils.get(accessToken + ":erarToal");
                if (StringUtils.isBlank(lastEar)) {
                    lastEar = "0";
                }
                BigDecimal lastEarTotal = new BigDecimal(lastEar);
                earAmount = earTotal.subtract(lastEarTotal);
            }
            StringBuilder sendContent = new StringBuilder();
            String dat = "上午";
            if (type == 2) {
                dat = "下午";
            }
            sendContent.append(sdf.format(new Date()) + " " + dat + "\n");
            sendContent.append(dat + "收益：" + formatMoney(earAmount.setScale(2, RoundingMode.HALF_DOWN)) + "元\n");
            sendContent.append("今天收益：" + formatMoney(earTotal.setScale(2, RoundingMode.HALF_DOWN)) + "元\n");
            sendContent.append("金额剩余："+calcTotal.add(earTotal)+"元\n");
            sendContent.append("涨:" + up + ",跌:" + down + "\n");
            sendContent.append(fundTipBuilder.toString());
            DingText dingText = new DingText(sendContent.toString());
            dingText.setAtAll(true);
            DingTalkSend dingTalkSend = new DingTalkSend(dingText);
            dingTalkSend.setAccessToken(accessToken);
            dingTalkSend.send();
        }
    }

    private String formatMoney(BigDecimal money){
        if(money.doubleValue()>=0){
            return "+"+money.toString();
        }
        return money.toString();
    }
    private void updateFundLog(FundStatModel statModel) {
        FundLog fundLog = fundLogMapper.queryById(statModel.getFundId());
        if (fundLog == null) {
            fundLog = new FundLog();
            fundLog.setCalcAmount(BigDecimal.valueOf(statModel.getCalcAmount()));
            fundLog.setCalcDate(new Date());
            fundLog.setFundCode(statModel.getFundCode());
            fundLog.setFundId(statModel.getFundId());
            fundLog.setGszzl(statModel.getGszzl());
            fundLogMapper.insert(fundLog);
        } else {
            fundLog.setFundCode(statModel.getFundCode());
            fundLog.setCalcAmount(BigDecimal.valueOf(statModel.getCalcAmount()));
            fundLog.setCalcDate(new Date());
            fundLog.setFundId(statModel.getFundId());
            fundLog.setGszzl(statModel.getGszzl());
            fundLogMapper.update(fundLog);
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

        private Integer fundId;

        private String fundCode;

        private String fundName;

        private Double calcAmount;

        private BigDecimal gszzl;

        private String belongName;

        public Integer getFundId() {
            return fundId;
        }

        public void setFundId(Integer fundId) {
            this.fundId = fundId;
        }

        public String getBelongName() {
            return belongName;
        }

        public void setBelongName(String belongName) {
            this.belongName = belongName;
        }

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
