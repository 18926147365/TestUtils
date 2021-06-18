package bean.resp;

import bean.Fund;
import lombok.Data;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;

/**
 * @Description:
 * @Author: lihaoming
 * @Date: 2021/6/11 上午10:35
 */
@Data
public class FundResp extends Fund {

    private BigDecimal fundTotalAmount;//累计收益

    private BigDecimal todayEarAmount;//今天收益

    private BigDecimal todayEarGszzl;//今天涨跌幅度

    private String payTimeStr;

    public String getPayTimeStr() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy年MM月dd日");
        if(getPayTime() !=null){
            return sdf.format(getPayTime());
        }
        return payTimeStr;
    }


}
