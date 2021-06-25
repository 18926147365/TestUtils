package mapper;

import bean.FundLog;
import bean.FundStatistics;
import bean.FundTodayLog;
import org.apache.ibatis.annotations.Param;

import java.util.Date;
import java.util.List;

/**
 * @Description:
 * @Author: lihaoming
 * @Date: 2021/5/31 上午9:18
 */
public interface FundTodayLogMapper {

    List<FundTodayLog> queryByFundCodeToday(@Param(value = "fundCode") String fundCode);

    FundTodayLog queryByFundCodeAndGztime(@Param(value = "fundCode") String fundCode, @Param(value = "gztime") Date gztime);

    void insert(FundTodayLog fundTodayLog);
}
