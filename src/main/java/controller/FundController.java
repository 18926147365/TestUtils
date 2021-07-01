package controller;

import bean.*;
import bean.resp.*;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import jdk.nashorn.api.scripting.ScriptObjectMirror;
import lombok.extern.slf4j.Slf4j;
import mapper.*;
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
@RequestMapping("fdd")
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
    @Autowired
    private FundTodayLogMapper fundTodayLogMapper;

    @Autowired
    private FundDayLogMapper fundDayLogMapper;

    @RequestMapping("/getFundList")
    public List<FundResp> getFundList(String belongId) throws Exception {
        log.info("查询基金列表 belongId:" + belongId);
        FundTalkConf fundTalkConf = fundTalkConfMapper.queryByBeLongId(belongId);
        String belongName = fundTalkConf.getBelongName();
        this.reloadFund();
        List<FundResp> resultList = new ArrayList<>();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        Date todayNow = sdf.parse(sdf.format(new Date()));
        Map<String, List<FundResp>> mergeMap = new HashMap<>();
        for (Fund fund : fundMapper.queryByBelongName(belongName)) {
            if (fund.getState() == -1) continue;
            FundResp fundResp = JSONObject.parseObject(JSONObject.toJSONString(fund), FundResp.class);
            if (fundResp.getState() == 0) {
                BigDecimal fundTotalAmount = fund.getCalcAmount().subtract(fund.getPayAmount());
                Date earDate = fundResp.getEarTime();
                fundResp.setTodayEarAmount(new BigDecimal("0"));
                fundResp.setTodayEarGszzl(new BigDecimal("0"));
//
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
            String mergeKey = fund.getFundCode() + fund.getState();
            if (!mergeMap.containsKey(mergeKey)) {
                mergeMap.put(mergeKey, new ArrayList<>());
            }
            mergeMap.get(mergeKey).add(fundResp);
        }
        //相同合并
        for (String mergeKey : mergeMap.keySet()) {
            List<FundResp> fundRespList = mergeMap.get(mergeKey);
            FundResp resp = JSONObject.parseObject(JSONObject.toJSONString(fundRespList.get(0)), FundResp.class);
            BigDecimal earAmount = new BigDecimal("0");
            BigDecimal calcAmount = new BigDecimal("0");
            BigDecimal payAmount = new BigDecimal("0");
            BigDecimal fundTotalAmount = new BigDecimal("0");
            BigDecimal todayEarAmount = new BigDecimal("0");
            for (FundResp fundResp : fundRespList) {
                if (fundResp.getState() == 0) {
                    earAmount = earAmount.add(fundResp.getEarAmount());
                    todayEarAmount = todayEarAmount.add(fundResp.getTodayEarAmount());
                    fundTotalAmount = fundTotalAmount.add(fundResp.getFundTotalAmount());
                }
                payAmount = payAmount.add(fundResp.getPayAmount());
                calcAmount = calcAmount.add(fundResp.getCalcAmount());
            }
            resp.setFundTotalAmount(fundTotalAmount);
            resp.setTodayEarAmount(todayEarAmount);
            resp.setEarAmount(earAmount);
            resp.setCalcAmount(calcAmount);
            resp.setPayAmount(payAmount);
            resultList.add(resp);
        }
        return resultList;
    }

    @RequestMapping("/getFundUser")
    public FundUserResp getFundUser(String belongId) throws IOException, ParseException {
        log.info("查询用户余额 belongId:" + belongId);
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
    public List<FundStatisticsResp> statisticsFundList(String belongId) {
        FundTalkConf fundTalkConf = fundTalkConfMapper.queryByBeLongId(belongId);
        List<FundStatistics> list = fundLogMapper.statisticsFund(fundTalkConf.getBelongName());
        list.sort(Comparator.comparing(FundStatistics::getCalcDate));
        List<FundStatisticsResp> result = new ArrayList<>();
        BigDecimal totalAmount = new BigDecimal("0");
        for (FundStatistics fundStatistics : list) {
            fundStatistics.setWeekDay(getWeek(fundStatistics.getCalcDate()));
            FundStatisticsResp resp = JSONObject.parseObject(JSONObject.toJSONString(fundStatistics), FundStatisticsResp.class);
            totalAmount = totalAmount.add(fundStatistics.getAmount());
            resp.setTotalAmount(totalAmount);
            result.add(resp);
        }

        result.sort(Comparator.comparing(FundStatistics::getCalcDate).reversed());
        List<FundStatisticsResp> resp = new ArrayList<>();
        int max = 30;
        for (int i = 0; i < result.size(); i++) {
            if (i >= 30) {
                break;
            }
            resp.add(result.get(i));
        }

        return resp;
    }


    @RequestMapping("/queryFundLogDetail")
    public List<FundLogResp> queryFundLogDetail(String fundCode, String belongId) {
        FundTalkConf fundTalkConf = fundTalkConfMapper.queryByBeLongId(belongId);
        List<Fund> fundList = fundMapper.queryByBelongNameAndFundCode(fundTalkConf.getBelongName(), fundCode);

        Map<String, FundLogResp> mergeMap = new TreeMap<>();
        for (Fund fund : fundList) {
            List<FundLog> loglist = fundLogMapper.queryList(fund.getId());
            for (FundLog fundLog : loglist) {
                String mergeKey = fundLog.getCalcDate().getTime() + "";
                if (!mergeMap.containsKey(mergeKey)) {
                    FundLogResp temp = new FundLogResp();
                    temp.setEarAmount(new BigDecimal("0"));
                    temp.setGszzl(fundLog.getGszzl());
                    temp.setCalcDay(fundLog.getCalcDate());
                    mergeMap.put(mergeKey, temp);
                }
                BigDecimal earTotal = mergeMap.get(mergeKey).getEarAmount().add(fundLog.getEarAmount());
                mergeMap.get(mergeKey).setEarAmount(earTotal.setScale(2, RoundingMode.HALF_DOWN));


                if (fund.getConfirmTime() != null) {
                    if (fund.getConfirmTime().getTime() == fundLog.getCalcDate().getTime()) {
                        mergeMap.get(mergeKey).setTag(3);
                        mergeMap.get(mergeKey).setPayAmount(fund.getPayAmount());
                    }
                }

            }
        }
        //排序
        Map<String, FundLogResp> sortMergeMap = new TreeMap<>(new Comparator() {
            @Override
            public int compare(Object o1, Object o2) {
                return ((String) (o1)).compareTo((String) (o2));
            }
        });
        sortMergeMap.putAll(mergeMap);
        BigDecimal totalAmount = new BigDecimal("0");
        List<FundLogResp> fundLogList = new ArrayList<>();
        for (String mergeKey : sortMergeMap.keySet()) {
            FundLogResp resp = sortMergeMap.get(mergeKey);
            totalAmount = totalAmount.add(resp.getEarAmount());
            resp.setTotalAmount(totalAmount);
            if (resp.getTag() != 3) {
                if (totalAmount.doubleValue() > 0) {
                    resp.setTag(1);
                } else {
                    resp.setTag(2);
                }
            }

            fundLogList.add(resp);
        }
        return fundLogList;
    }

    @RequestMapping("/queryFundLogDetailToday")
    public List<FundLogResp> queryFundLogDetailToday(String fundCode) {
        List<FundLogResp> fundLogList = new ArrayList<>();
        List<FundTodayLog> list = fundTodayLogMapper.queryByFundCodeToday(fundCode);
        for (int i = 0; i < list.size(); i++) {
            if (i % 2 != 0 && i != list.size() - 1) {
                continue;
            }
            FundTodayLog fundTodayLog = list.get(i);
            FundLogResp resp = new FundLogResp();
            resp.setGszzl(fundTodayLog.getGszzl());
            resp.setEarAmount(fundTodayLog.getGszzl());
            resp.setTotalAmount(fundTodayLog.getGszzl());
            resp.setCalcDate(fundTodayLog.getGztime());
            fundLogList.add(resp);
        }
        return fundLogList;
    }


    @RequestMapping("/queryFundDetail")
    public FundResp queryFundDetail(String fundCode, String belongId) throws ParseException {
        FundTalkConf fundTalkConf = fundTalkConfMapper.queryByBeLongId(belongId);
        List<Fund> fundList = fundMapper.queryByBelongNameAndFundCode(fundTalkConf.getBelongName(), fundCode);
        FundResp resp = JSONObject.parseObject(JSONObject.toJSONString(fundList.get(0)), FundResp.class);
        BigDecimal earAmount = new BigDecimal("0");
        BigDecimal calcAmount = new BigDecimal("0");
        BigDecimal payAmount = new BigDecimal("0");
        BigDecimal todayEarAmount = new BigDecimal("0");
        BigDecimal fundTotalAmount = new BigDecimal("0");
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        Date todayNow = sdf.parse(sdf.format(new Date()));
        for (Fund fund : fundList) {
            BigDecimal totalTemp = fund.getCalcAmount().subtract(fund.getPayAmount());
            Date earDate = fund.getEarTime();
            //判断收益日是否为今天
            if (earDate != null &&
                    todayNow.getTime() == sdf.parse(sdf.format(earDate)).getTime()) {
                Date calcDate = fund.getCalcTime();
                if (calcDate != null &&
                        sdf.parse(sdf.format(earDate)).getTime() != sdf.parse(sdf.format(calcDate)).getTime()) {
                    totalTemp = totalTemp.add(fund.getEarAmount());
                }
            }
            payAmount = payAmount.add(fund.getPayAmount());
            fundTotalAmount = fundTotalAmount.add(totalTemp);
            calcAmount = calcAmount.add(fund.getCalcAmount());
        }
        resp.setPayAmount(payAmount.setScale(2, RoundingMode.HALF_DOWN));
        resp.setCalcAmount(calcAmount.setScale(2, RoundingMode.HALF_DOWN));
        resp.setFundTotalAmount(fundTotalAmount.setScale(2, RoundingMode.HALF_DOWN));
        return resp;
    }

    @RequestMapping("/queryDayLog")
    public List<FundDayLogResp> queryDayLogl(String fundCode, String belongId) {
        FundTalkConf fundTalkConf = fundTalkConfMapper.queryByBeLongId(belongId);
        List<Fund> fundList = fundMapper.queryByBelongNameAndFundCode(fundTalkConf.getBelongName(), fundCode);
        Set<Long> existsSet = new HashSet<>();
        for (Fund fund : fundList) {
            if (fund.getConfirmTime() == null) continue;
            existsSet.add(fund.getConfirmTime().getTime());
        }
        List<FundDayLogResp> result = new ArrayList<>();
        List<FundDayLog> dayLogs = fundDayLogMapper.queryDayLog(fundCode, 90l);
        dayLogs.sort(Comparator.comparing(FundDayLog::getGztime));
        BigDecimal total = new BigDecimal("0");
        boolean isPay = false;
        for (FundDayLog dayLog : dayLogs) {
            FundDayLogResp resp = JSONObject.parseObject(JSONObject.toJSONString(dayLog), FundDayLogResp.class);
            total = total.add(resp.getGszzl());
            resp.setTag(0);
            if (isPay) {
                if (total.doubleValue() > 0) {
                    resp.setTag(1);
                } else {
                    resp.setTag(2);
                }

            }
            if (existsSet.contains(dayLog.getGztime().getTime())) {
                resp.setTag(3);
                isPay = true;
            }
            resp.setTotal(total);
            result.add(resp);
        }
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
            if (fund.getState() == 0) {
                ids.add(fund.getId() + "");
            }
        }
        return clearing(String.join(",", ids));
    }

    @RequestMapping("/clear")
    public String clearing(String fundIds) throws Exception {
        List<String> fundIdList = Arrays.asList(fundIds.split(","));
        for (String id : fundIdList) {
            Fund fund = fundMapper.queryById(Integer.valueOf(id));
            if (fund.getConfirmTime() == null) {
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

    @RequestMapping("/cail")
    public void cail() {
        fundTask.cailData();
    }

}
