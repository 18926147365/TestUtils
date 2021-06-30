package mapper;

import bean.FundDayLog;
import bean.FundTodayLog;
import org.apache.ibatis.annotations.Param;

import java.util.Date;
import java.util.List;

/**
 * @Description:
 * @Author: lihaoming
 * @Date: 2021/5/31 上午9:18
 */
public interface FundDayLogMapper {
    //
//    List<FundTodayLog> queryByFundCodeToday(@Param(value = "fundCode") String fundCode);
//
    List<FundDayLog> queryDayLog(@Param(value = "fundCode") String fundCode, @Param(value = "maxSize") Long maxSize);

    //    List<FundTodayLog> queryByFundCodeToday(@Param(value = "fundCode") String fundCode);
    void insert(FundDayLog fundTodayLog);


}
