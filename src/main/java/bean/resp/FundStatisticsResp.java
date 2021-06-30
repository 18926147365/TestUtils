package bean.resp;

import bean.FundStatistics;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;

/**
 * @Description:
 * @Author: lihaoming
 * @Date: 2021/6/18 下午4:12
 */
@Data
public class FundStatisticsResp extends FundStatistics {

  private BigDecimal totalAmount;

}
