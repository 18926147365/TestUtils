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

    private BigDecimal gszzl;

    private BigDecimal totalAmount;

    private BigDecimal earAmount;




}
