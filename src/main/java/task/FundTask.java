package task;

import bean.*;
import com.alibaba.fastjson.JSONObject;
import dingtalk.DingMarkDown;
import dingtalk.DingTalkSend;
import dingtalk.DingText;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import jdk.nashorn.api.scripting.ScriptObjectMirror;
import lombok.extern.slf4j.Slf4j;
import mapper.*;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
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

    @Autowired
    private FundTodayLogMapper fundTodayLogMapper;
    @Autowired
    private FundDayLogMapper fundDayLogMapper;

    @Value("${is_notify_fund}")
    private String isNotifyFund;

    @Scheduled(cron = "0 0/5 * * * ?")
    public void updateTask() {
        try {
            Date startDate = new Date();
            startDate.setHours(9);
            startDate.setSeconds(0);
            startDate.setMinutes(0);
            Date endDate = new Date();
            endDate.setHours(15);
            endDate.setSeconds(0);
            endDate.setMinutes(30);
            Date nowDate = new Date();
            if (startDate.getTime() < nowDate.getTime() &&
                    endDate.getTime() > nowDate.getTime()) {
                log.info("定时任务更新基金开始--------");
                this.execute(-1);
            }
        } catch (ParseException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Scheduled(cron = "0 55 08 * * ?")
    public void updateConfirmFund() {
        log.info("更新待确认的基金列表");
        fundMapper.updateConfirmFundList();
    }

    @Scheduled(cron = "0 30 08 * * ?")
    public void cailData() {
        for (Fund fund : fundMapper.queryAll(0)) {
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
            BigDecimal cailTotal = fund.getPayAmount();
            Date lastDate = new Date();
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
                Date gztime = new Date(datetime.longValue());
                BigDecimal gzssl = new BigDecimal(equityReturn);
                if (fund.getConfirmTime().getTime() <= gztime.getTime()) {
                    BigDecimal dayAmount = cailTotal.multiply(gzssl.multiply(new BigDecimal("0.01")));
                    cailTotal = cailTotal.add(dayAmount);
                }
                lastDate = gztime;
            }
            fundMapper.updateCalcFund(fund.getId(), cailTotal, lastDate);
            log.info(fund.getFundName() + "日期: " + sdf.format(lastDate) + " 相差金额：" + (cailTotal.setScale(2, RoundingMode.HALF_DOWN).subtract(fund.getCalcAmount())) + " 修正后:" + cailTotal.setScale(2, BigDecimal.ROUND_UP));
        }
        try {
            this.execute(-1);
        } catch (ParseException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        log.info("修正基金金额数据完成");
    }


    @Scheduled(cron = "0 36 11 * * ?")
    public void runTask1() {
        try {
            execute(1);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Scheduled(cron = "0 06 15 * * ?")
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
        List<Fund> fundList = fundService.queryAll(0);
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
                JSONObject fundJson = (JSONObject.parseObject(json));
                if (fundJson == null) {
                    continue;
                }
                log.info("数据更新:" + json);
                String gztime = fundJson.getString("gztime") + ":00";
                //判断今天是否是基金计算日
                if (daySdf.parse(gztime).getTime() == daySdf.parse(daySdf.format(new Date())).getTime()) {

                } else {
                    return;
                }
                //当前收益计算
                fundMapper.updateFund(fund.getId(), fundJson.getBigDecimal("gszzl"), (fundJson.getDate("gztime")));
                updateEarAmount(fund, fundJson.getDate("gztime"), fundJson.getBigDecimal("gszzl"),fundJson.getBigDecimal("gsz"));
                updateCalcAmount(fund, fundJson.getDate("gztime"), fundJson.getBigDecimal("gszzl"));
                updateFundLogToday(fundCode, fundJson.getDate("gztime"), fundJson.getBigDecimal("gszzl"));
                if (type == 2) {
                    updateFundLogDay(fundCode, fundJson.getDate("gztime"), fundJson.getBigDecimal("gszzl"),fundJson.getBigDecimal("gsz"));
                }
            }
        }
        if (type == 1 || type == 2) {
            notifyTalk(type);
        }
    }

    private void updateFundLogToday(String fundCode, Date gztime, BigDecimal gszzl) {
        FundTodayLog todayLog = new FundTodayLog();
        todayLog.setGszzl(gszzl);
        todayLog.setFundCode(fundCode);
        todayLog.setGztime(gztime);
        fundTodayLogMapper.insert(todayLog);
    }

    private void updateFundLogDay(String fundCode, Date gztime, BigDecimal gszzl,BigDecimal netValue) {
        FundDayLog dayLog = new FundDayLog();
        dayLog.setGszzl(gszzl);
        dayLog.setFundCode(fundCode);
        dayLog.setGztime(gztime);
        dayLog.setNetValue(netValue);
        fundDayLogMapper.insert(dayLog);
    }

    private void updateEarAmount(Fund fund, Date gztime, BigDecimal gszzl,BigDecimal netValue) {
        BigDecimal earAmount = fund.getCalcAmount();
        earAmount = earAmount.multiply(gszzl.multiply(new BigDecimal("0.01")));
        fundMapper.updateEarFund(fund.getId(), earAmount, new Date());
        FundStatModel statModel = new FundStatModel();
        statModel.setEarAmount(earAmount);
        statModel.setFundCode(fund.getFundCode());
        statModel.setFundId(fund.getId());
        statModel.setGszzl(gszzl);
        statModel.setNetValue(netValue);
        updateFundLog(statModel);
    }

    private void updateCalcAmount(Fund fund, Date gztime, BigDecimal gszzl) throws ParseException {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd ");
        SimpleDateFormat sdf15 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date validDate = sdf15.parse(sdf.format(new Date()) + " 15:00:00");
        if (validDate.getTime() == gztime.getTime() &&
                fund.getCalcTime().getTime() < sdf.parse(sdf.format(new Date())).getTime()) {
            BigDecimal calcAmount = fund.getCalcAmount().add(fund.getCalcAmount().multiply(gszzl).multiply(new BigDecimal("0.01")));
            BigDecimal earAmount = fund.getCalcAmount();
            earAmount = earAmount.multiply(gszzl.multiply(new BigDecimal("0.01")));
            fundMapper.updateCalcFund(fund.getId(), calcAmount, new Date());
            FundStatModel statModel = new FundStatModel();
            statModel.setEarAmount(earAmount);
            statModel.setFundCode(fund.getFundCode());
            statModel.setFundId(fund.getId());
            statModel.setGszzl(gszzl);
            updateFundLog(statModel);
        }
    }

    private void updateFundNotify(Map<String, FundStatModel> fundStatMap) {
        for (String fundCode : fundStatMap.keySet()) {
            FundStatModel statModel = fundStatMap.get(fundCode);
            fundMapper.updateEarFund(statModel.getFundId(), BigDecimal.valueOf(statModel.getCalcAmount()), new Date());
        }
    }

    public void notifyTalk(Integer type) {
        if ("0".equals(isNotifyFund)) {
            return;
        }
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        List<FundTalkConf> talkConfList = fundTalkConfMapper.queryAll();
        Map<String, List<Integer>> groupMap = new HashMap<>();
        BigDecimal allTotalAmount = new BigDecimal("0");
        for (FundTalkConf fundTalkConf : talkConfList) {
            String belongName = fundTalkConf.getBelongName();
            BigDecimal totalAmount = fundMapper.totalAmount(belongName);
            List<Fund> fundList = fundMapper.queryByBelongName(belongName);
            int up = 0, down = 0;
            BigDecimal earTotal = new BigDecimal("0");//总收益
            StringBuilder fundTipBuilder = new StringBuilder();
            DingMarkDown dingMarkDown = new DingMarkDown("结算", "\n").lineBreak();
            BigDecimal calcTotal = new BigDecimal("0");//剩余金额
            Map<String, Fund> mergeMap = new HashMap<>();
            for (Fund fund : fundList) {
                if (fund.getState() == 1) {//待确认
                    fundTipBuilder.append(buleDmk("[待确认]：" + fund.getFundName() + "(购入" + fund.getCalcAmount() + "元)") + "\n\n");
                    continue;
                }
                if (fund.getState() == 0) {//正常交易
                    if (fund.getGszzl() == null) continue;
                    if (!mergeMap.containsKey(fund.getFundCode())) {
                        mergeMap.put(fund.getFundCode(), fund);
                    } else {
                        Fund temp = mergeMap.get(fund.getFundCode());
                        temp.setCalcAmount(temp.getCalcAmount().add(fund.getCalcAmount()));
                        temp.setEarAmount(temp.getEarAmount().add(fund.getEarAmount()));
                        temp.setPayAmount(temp.getPayAmount().add(fund.getPayAmount()));
                    }
                }else if(fund.getState() == 2){//卖出确认中
                    fundTipBuilder.append(buleHui("[卖出确认中]：" + fund.getFundName() + " 持有:"+fund.getCalcAmount().setScale(0,RoundingMode.FLOOR)+"元(预计收益" + fund.getCalcAmount().subtract(fund.getPayAmount()).setScale(0,RoundingMode.FLOOR) + "元)") + "\n\n");
                }
            }

            for (String fundCode : mergeMap.keySet()) {
                Fund fund = mergeMap.get(fundCode);
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
                String gzzlStr = fund.getGszzl().setScale(2, RoundingMode.HALF_DOWN).toString();
                if (fund.getGszzl().doubleValue() > 0) {
                    gzzlStr = "+" + gzzlStr;
                }
                gzzlStr += "%";

                String content = String.format("[%s]：%s(%s元)", gzzlStr, fund.getFundName(), moneyStr);
                if (fund.getGszzl().doubleValue() > 0) {
                    content = redDmk(content);
                } else {
                    content = greeDmk(content);
                }
                fundTipBuilder.append(content + "\n\n");
            }

            BigDecimal earAmount = new BigDecimal("0");
            if (type == 1) {
                redisLuaUtils.set(fundTalkConf.getBelongId() + ":erarToal", earTotal.setScale(2, RoundingMode.HALF_DOWN).toString(), 60 * 60 * 13);
                earAmount = earTotal.setScale(2, RoundingMode.HALF_DOWN);
                totalAmount = totalAmount.add(earTotal);
            } else if (type == 2) {
                String lastEar = redisLuaUtils.get(fundTalkConf.getBelongId() + ":erarToal");
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
            dingMarkDown.h2(sdf.format(new Date()) + " " + dat).lineBreak();

            String dear = dat + "收益：" + formatMoney(earAmount.setScale(2, RoundingMode.HALF_DOWN)) + "元";
            String tear = "今天收益：" + formatMoney(earTotal.setScale(2, RoundingMode.HALF_DOWN)) + "元";
            allTotalAmount = allTotalAmount.add(earTotal);
            dingMarkDown.add("持有者：" + fundTalkConf.getBelongName()).lineBreak();
            dingMarkDown.add(whichDmk(dear, earAmount.doubleValue())).lineBreak();
            dingMarkDown.add(whichDmk(tear, earTotal.doubleValue())).lineBreak();
            dingMarkDown.add("基金余额：" + totalAmount.setScale(2, RoundingMode.HALF_DOWN) + "元").lineBreak();
            dingMarkDown.add("涨:" + up + ",跌:" + down).lineBreak();
            dingMarkDown.add(fundTipBuilder.toString()).lineBreak();
            dingMarkDown.line("点击查看更多基金信息", "http://42.194.205.61:8082/#/fund/" + fundTalkConf.getBelongId());

            DingTalkSend dingTalkSend1 = new DingTalkSend(dingMarkDown);

            dingTalkSend1.setAccessToken(fundTalkConf.getAccessToken());

            dingTalkSend1.send();


        }

        DingText dingText = new DingText();
        dingText.setContent("今天收益:" + formatMoney(allTotalAmount) + "元");
        dingText.setAtAll(false);
        DingTalkSend dingTalkSend2 = new DingTalkSend(dingText);
        dingTalkSend2.setAccessToken("e13e4148cb80bb1927cd5d9e8f340590b7df06780587c0233c9fa9b996647a9a");
        dingTalkSend2.send();
    }

    private String whichDmk(String content, double val) {
        if (val > 0) {
            return redDmk(content);
        } else {
            return greeDmk(content);
        }
    }

    private static String buleDmk(String content) {
        return "<font color=#003e9f  face=\"黑体\">" + content + "</font>";
    }
    private static String buleHui(String content) {
        return "<font color=#9007c1  face=\"黑体\">" + content + "</font>";
    }


    private String redDmk(String content) {
        return "<font color=#dc2626  face=\"黑体\">" + content + "</font>";
    }

    private String greeDmk(String content) {
        return "<font color=#21960d  face=\"黑体\">" + content + "</font>";
    }

    private String formatMoney(BigDecimal money) {
        if (money.doubleValue() >= 0) {
            return "+" + money.toString();
        }
        return money.toString();
    }

    private void updateFundLog(FundStatModel statModel) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        FundLog fundLog = null;
        try {
            fundLog = fundLogMapper.queryByIdAndDate(statModel.getFundId(), sdf.parse(sdf.format(new Date())));
        } catch (ParseException e) {
            e.printStackTrace();
        }
        if (fundLog == null) {
            fundLog = new FundLog();
            fundLog.setEarAmount((statModel.getEarAmount()));
            fundLog.setCalcDate(new Date());
            fundLog.setFundCode(statModel.getFundCode());
            fundLog.setFundId(statModel.getFundId());
            fundLog.setGszzl(statModel.getGszzl());
            fundLog.setNetValue(statModel.getNetValue());
            fundLogMapper.insert(fundLog);
        } else {
            fundLog.setFundCode(statModel.getFundCode());
            fundLog.setEarAmount(statModel.getEarAmount());
            fundLog.setCalcDate(new Date());
            fundLog.setFundId(statModel.getFundId());
            fundLog.setGszzl(statModel.getGszzl());
            fundLog.setNetValue(statModel.getNetValue());
            fundLogMapper.update(fundLog);
        }

    }

    /**
     * 修正金额
     */
    private void cailAmountUpdate() {


    }

    class FundStatModel {

        private Integer fundId;

        private String fundCode;

        private String fundName;

        private Double calcAmount;

        private BigDecimal netValue;

        private BigDecimal gszzl;

        private String belongName;

        private BigDecimal earAmount;

        public BigDecimal getNetValue() {
            return netValue;
        }

        public void setNetValue(BigDecimal netValue) {
            this.netValue = netValue;
        }

        public BigDecimal getEarAmount() {
            return earAmount;
        }

        public void setEarAmount(BigDecimal earAmount) {
            this.earAmount = earAmount;
        }

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
