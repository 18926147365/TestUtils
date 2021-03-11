package utils;

import bean.Fund;
import com.sun.management.GarbageCollectorMXBean;
import lombok.extern.slf4j.Slf4j;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.ComponentScans;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.scheduling.annotation.EnableScheduling;
import service.FundService;

import javax.management.MBeanServer;
import java.lang.management.ManagementFactory;
import java.lang.management.MemoryUsage;
import java.util.List;
import java.util.Map;

/**
 * @author 李浩铭
 * @date 2020/7/7 17:50
 * @descroption
 */
@SpringBootApplication
@ComponentScan({"controller", "service","config","utils","task"})
@MapperScan("mapper")
@Slf4j
@EnableScheduling
public class Application implements ApplicationListener<ContextRefreshedEvent> {

    public static void main(String[] args) {
        System.setProperty("java.awt.headless", "false");
        SpringApplication.run(Application.class, args);
    }


    private static volatile GarbageCollectorMXBean gcMBean;

    private static final String GC_BEAN_NAME =
            "java.lang:type=GarbageCollector,name=ConcurrentMarkSweep";


    static {
//        gcMBean = getGCMBean();

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


    @Override
    public void onApplicationEvent(ContextRefreshedEvent event) {
//        FundService fundService = (FundService) event.getApplicationContext().getBean("fundService");
//        FundUtils.runTask(fundService);

    }
}
