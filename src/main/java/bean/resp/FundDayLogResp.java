package bean.resp;

import bean.FundDayLog;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

/**
 * @Description:
 * @Author: lihaoming
 * @Date: 2021/6/29 上午9:55
 */
@Data
public class FundDayLogResp extends FundDayLog{
    private int tag;//1：买入点
    private BigDecimal total;
}
