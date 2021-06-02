package easyexcel;

import bean.User;
import com.alibaba.excel.context.AnalysisContext;
import com.alibaba.excel.event.AnalysisEventListener;
import mapper.UserMapper;

import java.util.ArrayList;
import java.util.List;

/**
 * @Description:
 * @Author: lihaoming
 * @Date: 2021/5/14 下午2:28
 */
public class DemoDataListener extends AnalysisEventListener<DemoData> {

    private static final int BATCH_COUNT = 10;
    private List<User> userList = new ArrayList<>();

    private UserMapper userMapper;

    public DemoDataListener(UserMapper userMapper) {
        this.userMapper = userMapper;
    }
    public DemoDataListener() {
    }

    @Override
    public void invoke(DemoData demoData, AnalysisContext analysisContext) {
        User user = new User();
        user.setName(demoData.getName());
        user.setMoney(demoData.getMoney());
        userList.add(user);
        if (userList.size() >= BATCH_COUNT) {
            userMapper.insertBatch(userList);
            userList.clear();
        }
    }

    @Override
    public void doAfterAllAnalysed(AnalysisContext analysisContext) {
        if (userList.size() > 0) {
            userMapper.insertBatch(userList);
        }
    }
}
