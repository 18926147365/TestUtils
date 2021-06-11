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

    List<Fund> queryAll(@Param(value="state") Integer state) ;

    Fund queryById(@Param(value = "id") Integer id);

    void updateFund(@Param(value = "id") Integer id, @Param(value = "gszzl") BigDecimal gszzl, @Param(value = "gztime") Date gztime);

    void updateCalcFund(@Param(value = "id") Integer id, @Param(value = "calcAmount") BigDecimal calcAmount, @Param(value = "calcTime") Date calcTime);

    void updateEarFund(@Param(value = "id") Integer id, @Param(value = "earAmount") BigDecimal earAmount, @Param(value = "earTime") Date earTime);

    List<Fund> queryByBelongName(@Param(value = "belongName") String belongName);

    BigDecimal totalCalcAmount(@Param(value = "belongName") String belongName);

    BigDecimal totalAmount(@Param(value = "belongName") String belongName);

    BigDecimal totalEarAmount(@Param(value = "belongName") String belongName);

    Fund queryBalance(@Param(value = "belongName") String belongName);

    BigDecimal totalDealAmount(@Param(value = "belongName") String belongName);

    BigDecimal totalAwaitAmount(@Param(value = "belongName") String belongName);

    BigDecimal todayEarAmount(@Param(value = "belongName") String belongName);




}
