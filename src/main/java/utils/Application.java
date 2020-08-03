package utils;

import com.sun.management.GarbageCollectorMXBean;
import com.sun.management.GcInfo;
import lombok.extern.slf4j.Slf4j;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.ComponentScans;

import javax.management.MBeanServer;
import java.lang.management.ManagementFactory;
import java.lang.management.MemoryUsage;
import java.util.Map;

/**
 * @author 李浩铭
 * @date 2020/7/7 17:50
 * @descroption
 */
@SpringBootApplication
@ComponentScan({"controller", "service","config","utils"})
@MapperScan("mapper")
@Slf4j
public class Application {

    public static void main(String[] args) {
        System.setProperty("java.awt.headless", "false");
        SpringApplication.run(Application.class, args);
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Thread.sleep(3000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                CopyUtils.listenerCopy();
            }
        });
        thread.setDaemon(true);
//        thread.start();
//


        Thread thread1 = new Thread(new Runnable() {
            @Override
            public void run() {
                while (true) {


                    GcInfo gci = gcMBean.getLastGcInfo();
                    long duration = gci.getDuration();
//                    System.out.println("gc花费时间：" + duration + "ms");

                    Map<String, MemoryUsage> memoryUsageBeforeGc = gci.getMemoryUsageBeforeGc();
                    Map<String, MemoryUsage> memoryUsageAfterGc = gci.getMemoryUsageAfterGc();
                    memoryUsageBeforeGc.forEach((key, memory) -> {

//                        log.info("{} Before GC:{}", key, memory);
//                        MemoryUsage afterMemory = memoryUsageAfterGc.get(key);
//                        log.info("{} After GC:{}", key,afterMemory);
                    });
                    try {
                        Thread.sleep(10000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }

                }
            }
        });
        thread1.setDaemon(true);
//        thread1.start();


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

}
