package bean.resp;

import lombok.Data;

import java.math.BigDecimal;

/**
 * @Description:
 * @Author: lihaoming
 * @Date: 2021/6/11 上午11:52
 */
@Data
public class FundUserResp {

    private String userName; //用户名

    private BigDecimal totalAmount;//当前余额

    private BigDecimal capitalAmount;//本金

    private BigDecimal totalCalcAmount;//总收益

    private BigDecimal balanceAmount;//未购买基金的金额

    private BigDecimal awaitAmount;//待确认购入的金额

    private BigDecimal dealAmount;//正在交易的金额

    private BigDecimal todayEarAmount;//当天总收益

}
