package mapper;


import bean.User;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @Description:
 * @Author: lihaoming
 * @Date: 2020/6/29 16:06
 */
public interface UserMapper {

    List<User> queryAll(@Param("index") Integer index, @Param("pageSize") Integer pageSize);


    List<User> queryMoneys(@Param("index") Integer index, @Param("pageSize") Integer pageSize);

    void save(User user);

    void insertBatch(@Param("list") List<User> list);

    List<User> queryMoney();

    Long countMoney();
}
