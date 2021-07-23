package bean.resp;

import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

/**
 * @Description:
 * @Author: lihaoming
 * @Date: 2021/7/1 下午2:12
 */
@Data
public class FundAllResp {

    private String belongName;

    private String belongId;

    private BigDecimal totalEarAmount;

    private BigDecimal totalEarAllAmount;

    private List<FundResp> fundList;


}
