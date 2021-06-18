package mapper;

import bean.FundTalkConf;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @Description:
 * @Author: lihaoming
 * @Date: 2021/5/31 上午9:17
 */
public interface FundTalkConfMapper {

    List<FundTalkConf> queryAll();

    FundTalkConf queryByBeLongName(@Param(value = "belongName") String belongName);

    FundTalkConf queryByBeLongId(@Param(value = "belongId") String belongId);


}
