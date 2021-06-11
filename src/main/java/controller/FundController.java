package controller;

import bean.Fund;
import bean.resp.FundResp;
import bean.resp.FundUserResp;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import lombok.extern.slf4j.Slf4j;
import mapper.FundMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import task.FundTask;

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

    @RequestMapping("getFundList")
    public List<FundResp> getFundList(String belongName) throws Exception {
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
    public FundUserResp getFundUser(String belongName) throws IOException, ParseException {
        this.reloadFund();
        BigDecimal totalAmount = fundMapper.totalAmount(belongName);//总余额
        BigDecimal totalCalcAMount = fundMapper.totalCalcAmount(belongName);//总收益
        BigDecimal dealAmount = fundMapper.totalDealAmount(belongName);//正在交易的金额
        BigDecimal awaitAmount = fundMapper.totalAwaitAmount(belongName);//待确认购入的金额
        Fund balanceFund = fundMapper.queryBalance(belongName);//未购买基金的金额
        BigDecimal todayEarAmount = fundMapper.todayEarAmount(belongName);//当天收益
        FundUserResp fundUserResp = new FundUserResp();
        fundUserResp.setTotalAmount(totalAmount);
        fundUserResp.setBalanceAmount(balanceFund.getPayAmount());
        fundUserResp.setDealAmount(dealAmount);
        fundUserResp.setTotalCalcAmount(totalCalcAMount);
        fundUserResp.setAwaitAmount(awaitAmount);
        fundUserResp.setTodayEarAmount(todayEarAmount);
        return fundUserResp;
    }

    static volatile Date lastUploadDate = new Date();

    private synchronized void reloadFund() {
        try {
            if (new Date().getTime() - lastUploadDate.getTime() > 30 * 1000) {
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
