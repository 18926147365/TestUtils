package mapper;

import bean.Fund;
import org.apache.ibatis.annotations.Param;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

/**
 * @Description:
 * @Author: lihaoming
 * @Date: 2021/3/10 上午10:06
 */
public interface FundMapper {

    List<Fund> queryAll();

    Fund queryById(@Param(value = "id") Integer id);

    void updateFund(@Param(value = "id") Integer id, @Param(value = "gszzl") BigDecimal gszzl, @Param(value = "gztime") Date gztime);

    void updateCalcFund(@Param(value = "id") Integer id, @Param(value = "calcAmount") BigDecimal calcAmount, @Param(value = "calcTime") Date calcTime);
}