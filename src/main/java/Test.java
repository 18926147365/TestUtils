import bean.User;
import com.alibaba.fastjson.JSONObject;
import com.sun.management.GcInfo;
import org.apache.commons.lang.ObjectUtils;
import org.apache.commons.lang.SystemUtils;
import utils.ThreadExecutorPool;

import java.io.Console;
import java.lang.management.ManagementFactory;
import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.Callable;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import com.sun.management.GarbageCollectorMXBean;

import javax.management.MBeanServer;

/**
 * @author 李浩铭
 * @date 2020/7/3 10:28
 * @descroption
 */

public class Test {

    private static volatile GarbageCollectorMXBean gcMBean;

    private static final String GC_BEAN_NAME =
            "java.lang:type=GarbageCollector,name=ConcurrentMarkSweep";


    public static void main(String[] args) throws ParseException {

        User user=new User();
        user.setId(1);
        user.setMoney(BigDecimal.valueOf(1));

        User user1=new User();
        user1.setId(1);
        user1.setMoney(BigDecimal.valueOf(1.4));

        User user2=new User();
        user2.setId(1);
        user2.setMoney(BigDecimal.valueOf(1.4));

        User user3=new User();
        user3.setId(2);
        user3.setMoney(BigDecimal.valueOf(1.1));

        User user5=new User();
        user5.setId(2);
        user5.setMoney(BigDecimal.valueOf(1.2));
        List<User> list=new ArrayList<>();
        list.add(user);
//        list.add(user1);
//        list.add(user2);
//        list.add(user3);
//        list.add(user5);

        a:for (User u : list) {
            double money=u.getMoney().doubleValue();
            u.setName("非最高");
            for (User u1 : list) {
                if(u==u1){
                    continue ;
                }
                if(u.getId()!=u1.getId()){
                    continue ;
                }
                if(u1.getMoney().doubleValue()>money){
                    continue a;
                }
            }
            u.setName("最高");
        }

        for (User user4 : list) {
            System.out.println(JSONObject.toJSONString(user4));
        }
        
        
        
        

    }



    static {
        gcMBean = getGCMBean();
    }
    private static GarbageCollectorMXBean getGCMBean() {
        try {
            MBeanServer server = ManagementFactory.getPlatformMBeanServer();
            GarbageCollectorMXBean bean =
                    ManagementFactory.newPlatformMXBeanProxy(server,
                            GC_BEAN_NAME, GarbageCollectorMXBean.class);
            return bean;
        } catch (RuntimeException re) {
            throw re;
        } catch (Exception exp) {
            throw new RuntimeException(exp);
        }
    }
}
