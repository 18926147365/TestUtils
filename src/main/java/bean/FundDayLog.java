package bean;

import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;

/**
 * @Description:
 * @Author: lihaoming
 * @Date: 2021/6/24 下午4:14
 */
@Data
public class FundDayLog {
    private Long id;

    private String  fundCode;

    private Date gztime;

    private BigDecimal gszzl;

}
