package bean.resp;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;

/**
 * @Description:
 * @Author: lihaoming
 * @Date: 2021/5/31 上午9:12
 */
@Data
public class FundLogResp {

    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
    private Date calcDate;
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd")
    private Date calcDay;

    private BigDecimal gszzl;

    private BigDecimal totalAmount;

    private BigDecimal earAmount;

    private BigDecimal netValue;//净值

    private int tag;//1:涨 2:跌 3:买入点

    private BigDecimal payAmount;//购入金额




}
