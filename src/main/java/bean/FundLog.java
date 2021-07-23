package bean;

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
public class FundLog {
    private Integer id;

    private Integer fundId;

    private String fundCode;

    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd")
    private Date calcDate;

    private BigDecimal gszzl;

    private BigDecimal netValue;

    private BigDecimal earAmount;

    private Date modifyTime;

}
