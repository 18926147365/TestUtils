package controller;

import bean.Fund;
import bean.FundLog;
import bean.FundStatistics;
import bean.FundTalkConf;
import bean.resp.*;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import jdk.nashorn.api.scripting.ScriptObjectMirror;
import lombok.extern.slf4j.Slf4j;
import mapper.FundLogMapper;
import mapper.FundMapper;
import mapper.FundTalkConfMapper;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import task.FundTask;
import utils.HttpClientUtil;
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
import java.util.concurrent.*;

/**
 * @Description:
 * @Author: lihaoming
 * @Date: 2021/6/10 下午2:45
 */
@RestController
@RequestMapping("fund")
@Slf4j
public class FundController {

    @Autowired
    private FundTask fundTask;

    @Autowired
    private FundMapper fundMapper;

    @Autowired
    private FundTalkConfMapper fundTalkConfMapper;
    @Autowired
    private FundLogMapper fundLogMapper;
    @Autowired
    private RedisLuaUtils redisLuaUtils;

    @RequestMapping("getFundList")
    public List<FundResp> getFundList(String belongId) throws Exception {

        FundTalkConf fundTalkConf = fundTalkConfMapper.queryByBeLongId(belongId);
        String belongName = fundTalkConf.getBelongName();
        this.reloadFund();
        List<FundResp> resultList = new ArrayList<>();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        Date todayNow = sdf.parse(sdf.format(new Date()));
        for (Fund fund : fundMapper.queryByBelongName(belongName)) {
            if (fund.getState() == -1) continue;
            FundResp fundResp = JSONObject.parseObject(JSONObject.toJSONString(fund), FundResp.class);
            if (fundResp.getState() == 0) {
                BigDecimal fundTotalAmount = fund.getCalcAmount().subtract(fund.getPayAmount());
                Date earDate = fundResp.getEarTime();
                //判断收益日是否为今天
                if (earDate != null &&
                        todayNow.getTime() == sdf.parse(sdf.format(earDate)).getTime()) {
                    fundResp.setTodayEarAmount(fund.getEarAmount());
                    fundResp.setTodayEarGszzl(fund.getGszzl());
                    Date calcDate = fund.getCalcTime();
                    if (calcDate != null &&
                            sdf.parse(sdf.format(earDate)).getTime() != sdf.parse(sdf.format(calcDate)).getTime()) {
                        fundTotalAmount = fundTotalAmount.add(fund.getEarAmount());
                    }
                }
                fundResp.setFundTotalAmount(fundTotalAmount);
            }
            resultList.add(fundResp);
        }
        return resultList;
    }

    @RequestMapping("/getFundUser")
    public FundUserResp getFundUser(String belongId) throws IOException, ParseException {
        log.info("查询用户余额 belongId:"+belongId);
        FundTalkConf fundTalkConf = fundTalkConfMapper.queryByBeLongId(belongId);
        String belongName = fundTalkConf.getBelongName();
        this.reloadFund();
        BigDecimal totalAmount = fundMapper.totalAmount(belongName);//总余额
        BigDecimal totalCalcAMount = fundMapper.totalCalcAmount(belongName);//总收益
        BigDecimal dealAmount = fundMapper.totalDealAmount(belongName);//正在交易的金额
        BigDecimal awaitAmount = fundMapper.totalAwaitAmount(belongName);//待确认购入的金额
        Fund balanceFund = fundMapper.queryBalance(belongName);//未购买基金的金额
        BigDecimal todayEarAmount = fundMapper.todayEarAmount(belongName);//当天收益

        if (awaitAmount == null) {
            awaitAmount = new BigDecimal("0");
        }
        if (totalCalcAMount == null) {
            totalCalcAMount = new BigDecimal("0");
        }
        if (todayEarAmount == null) {
            todayEarAmount = new BigDecimal("0");
        }
        FundUserResp fundUserResp = new FundUserResp();
        fundUserResp.setUserName(belongName);
        fundUserResp.setCapitalAmount(fundTalkConf.getAmount());//本金
        fundUserResp.setTotalAmount(totalAmount);
        fundUserResp.setBalanceAmount(balanceFund.getPayAmount());
        fundUserResp.setDealAmount(dealAmount);
        fundUserResp.setTotalCalcAmount(totalCalcAMount);
        fundUserResp.setAwaitAmount(awaitAmount);
        fundUserResp.setTodayEarAmount(todayEarAmount);
        return fundUserResp;
    }

    @RequestMapping("/queryRealFundList")
    public FundRealResp queryRealFundList(String belongId) {

        this.reloadFund();
        FundTalkConf fundTalkConf = fundTalkConfMapper.queryByBeLongId(belongId);
        String belongName = fundTalkConf.getBelongName();
        List<Fund> fundList = fundMapper.queryByBelongName(belongName);
        BigDecimal todayEarAmount = fundMapper.todayEarAmount(belongName);
        FundRealResp realResp = new FundRealResp();
        realResp.setTodayAmount(todayEarAmount);
        if (todayEarAmount == null) {
            return realResp;
        }

        realResp.setFundList(fundList);
        int upT = 0;
        int downT = 0;
        for (Fund fund : fundList) {
            if (fund.getState() == 0) {
                if (fund.getEarAmount() != null) {
                    if (fund.getEarAmount().doubleValue() > 0) {
                        upT++;
                    } else if (fund.getEarAmount().doubleValue() < 0) {
                        downT++;
                    }
                }
            }
        }
        realResp.setUpT(upT);
        realResp.setDownT(downT);
        return realResp;
    }

    @RequestMapping("/statisticsFundList")
    public List<FundStatistics> statisticsFundList(String belongId) {
        FundTalkConf fundTalkConf = fundTalkConfMapper.queryByBeLongId(belongId);
        List<FundStatistics> list = fundLogMapper.statisticsFund(fundTalkConf.getBelongName());
        for (FundStatistics fundStatistics : list) {
            fundStatistics.setWeekDay(getWeek(fundStatistics.getCalcDate()));
        }
        return list;
    }


    @RequestMapping("/queryFundLogDetail")
    public FundDetailResp queryFundLogDetail(Integer fundId){
        List<FundLog> loglist = fundLogMapper.queryList(fundId);
        Fund fund = fundMapper.queryById(fundId);
        BigDecimal totalAmount = new BigDecimal("0") ;
        List<FundLogResp> fundLogList = new ArrayList<>();
        for (FundLog fundLog : loglist) {
            FundLogResp resp = new FundLogResp();
            resp.setGszzl(fundLog.getGszzl());
            resp.setEarAmount(fundLog.getEarAmount());
            resp.setCalcDate(fundLog.getCalcDate());
            totalAmount = totalAmount.add(fundLog.getEarAmount());
            resp.setTotalAmount(totalAmount);
            fundLogList.add(resp);
        }
        FundDetailResp result = new FundDetailResp();
        result.setFund(fund);
        result.setFundLogList(fundLogList);
        return result;
    }

    private String getWeek(Date today) {
        String week = "";
        Calendar c = Calendar.getInstance();
        c.setTime(today);
        int weekday = c.get(Calendar.DAY_OF_WEEK);
        if (weekday == 1) {
            week = "星期日";
        } else if (weekday == 2) {
            week = "星期一";
        } else if (weekday == 3) {
            week = "星期二";
        } else if (weekday == 4) {
            week = "星期三";
        } else if (weekday == 5) {
            week = "星期四";
        } else if (weekday == 6) {
            week = "星期五";
        } else if (weekday == 7) {
            week = "星期六";
        }
        return week;
    }
    private synchronized void reloadFund() {
//        try {
//            if (new Date().getTime() - lastUploadDate.getTime() >1000 * 60) {
//                lastUploadDate = new Date();
//                fundTask.execute(-1);
//            }
//        } catch (ParseException e) {
//            e.printStackTrace();
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
    }


    @RequestMapping("/clear2")
    public String clearing1(String belongName) throws Exception {
        List<Fund> list = fundMapper.queryByBelongName(belongName);
        List<String> ids = new ArrayList<>();
        for (Fund fund : list) {
            if(fund.getState()==0){
                ids.add(fund.getId()+"");
            }
        }
        return clearing(String.join(",",ids));
    }

    @RequestMapping("/clear")
    public String clearing(String fundIds) throws Exception {
        List<String> fundIdList = Arrays.asList(fundIds.split(","));
        for (String id : fundIdList) {
            Fund fund = fundMapper.queryById(Integer.valueOf(id));
            if(fund.getConfirmTime() == null){
                continue;
            }
            String result = HttpClientUtil.get("http://fund.eastmoney.com/pingzhongdata/" + fund.getFundCode() + ".js");
            ScriptEngineManager manager = new ScriptEngineManager();
            ScriptEngine engine = manager.getEngineByName("js");
            try {
                engine.eval(result);
            } catch (ScriptException e) {
                e.printStackTrace();
            }
            ScriptObjectMirror scriptObjectMirror = (ScriptObjectMirror) engine.get("Data_netWorthTrend");
            BigDecimal calcTemp = new BigDecimal(fund.getPayAmount().doubleValue());
            fundLogMapper.delete(fund.getId());
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
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
                if (fund.getConfirmTime().getTime() <= new Date(datetime.longValue()).getTime()) {
                    BigDecimal temp = calcTemp.multiply(new BigDecimal(equityReturn).multiply(new BigDecimal("0.01")));
                    calcTemp = calcTemp.add(temp);
                    FundLog fundLog = new FundLog();
                    fundLog.setFundCode(fund.getFundCode());
                    fundLog.setGszzl(new BigDecimal(equityReturn));
                    fundLog.setFundId(fund.getId());
                    fundLog.setModifyTime(new Date());
                    fundLog.setEarAmount(temp.setScale(2, RoundingMode.HALF_DOWN));
                    fundLog.setCalcDate(new Date(datetime.longValue()));
                    fundLogMapper.insert(fundLog);
                }
            }
        }
        fundTask.execute(-1);
        return "完成";
    }

}
