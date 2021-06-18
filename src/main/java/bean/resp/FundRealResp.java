package bean.resp;

import bean.Fund;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

/**
 * @Description:
 * @Author: lihaoming
 * @Date: 2021/6/11 上午11:52
 */
@Data
public class FundRealResp {

    private BigDecimal todayAmount;//今天收益，若为null则说明没有收益

    private List<Fund> fundList;//列表

    private Integer upT;//升

    private Integer downT;//跌


}
