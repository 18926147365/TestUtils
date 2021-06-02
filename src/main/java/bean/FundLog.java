package bean;

import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;

/**
 * @Description:
 * @Author: lihaoming
 * @Date: 2021/5/31 上午9:12
 */
@Data
public class FundLog {
    private Integer id;

    private Integer fundId;

    private String fundCode;

    private Date calcDate;

    private BigDecimal gszzl;

    private BigDecimal calcAmount;

    private Date modifyTime;

}
