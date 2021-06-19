package controller;

import bean.Fund;
import bean.FundStatistics;
import bean.FundTalkConf;
import bean.resp.FundRealResp;
import bean.resp.FundResp;
import bean.resp.FundUserResp;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import lombok.extern.slf4j.Slf4j;
import mapper.FundLogMapper;
import mapper.FundMapper;
import mapper.FundTalkConfMapper;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import task.FundTask;
import utils.RedisLuaUtils;

import java.io.IOException;
import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
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
        if(todayEarAmount == null){
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
    public List<FundStatistics> statisticsFundList (String belongId){
        this.reloadFund();
        FundTalkConf fundTalkConf = fundTalkConfMapper.queryByBeLongId(belongId);
        List<FundStatistics> list = fundLogMapper.statisticsFund(fundTalkConf.getBelongName());
        return list;
    }


    static volatile Date lastUploadDate = new Date();

    private synchronized void reloadFund() {
        try {
            if (new Date().getTime() - lastUploadDate.getTime() >1000 * 60) {
                lastUploadDate = new Date();
                fundTask.execute(-1);
            }
        } catch (ParseException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
