package mapper;


import bean.Model;
import bean.User;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @Description:
 * @Author: lihaoming
 * @Date: 2020/6/29 16:06
 */
public interface ModelMapper {

    Model queryByUa(@Param("uaModel") String uaModel);
}
