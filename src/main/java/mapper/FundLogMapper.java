package mapper;

import bean.Fund;
import bean.FundLog;
import org.apache.ibatis.annotations.Param;

/**
 * @Description:
 * @Author: lihaoming
 * @Date: 2021/5/31 上午9:18
 */
public interface FundLogMapper {

    FundLog queryById(@Param(value = "fundId") Integer fundId);

    void insert(FundLog fundLog);

    void update(FundLog fundLog);


}
