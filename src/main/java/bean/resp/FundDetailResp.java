package bean.resp;

import bean.Fund;
import lombok.Data;

import java.util.List;

/**
 * @Description:
 * @Author: lihaoming
 * @Date: 2021/6/24 上午10:53
 */
@Data
public class FundDetailResp {

    private Fund fund;

    private List<FundLogResp> fundLogList;
}
