package mapper;

import bean.Fund;
import bean.FundLog;
import org.apache.ibatis.annotations.Param;

import java.util.Date;

/**
 * @Description:
 * @Author: lihaoming
 * @Date: 2021/5/31 上午9:18
 */
public interface FundLogMapper {

    FundLog queryById(@Param(value = "fundId") Integer fundId);

    FundLog queryByIdAndDate(@Param(value = "fundId") Integer fundId, @Param(value = "calcDate") Date calcDate);

    void insert(FundLog fundLog);

    void update(FundLog fundLog);


}