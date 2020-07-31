package utils;

import com.sun.management.GarbageCollectorMXBean;
import com.sun.management.GcInfo;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Value;

import javax.management.MBeanServer;
import java.lang.management.ManagementFactory;
import java.lang.management.MemoryUsage;
import java.util.Map;

/**
 * @author wenlong.chen@mchuan.com
 */
public class GcStatMonitor implements Monitor {

    /**
     * gc时长警告线, 单位毫秒
     */
    @Value("${app.gc.duration:200}")
    private int gcDuration = 200;

    private Logger logger = LogManager.getLogger();

    private static final String GC_BEAN_NAME =
            "java.lang:type=GarbageCollector,name=ConcurrentMarkSweep";
    private static volatile GarbageCollectorMXBean gcMBean;

    // initialize the GC MBean field
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

    @Override
    public void monitor() {
        GcInfo gci = gcMBean.getLastGcInfo();
        long id = gci.getId();
        long startTime = gci.getStartTime();
        long endTime = gci.getEndTime();
        long duration = gci.getDuration();
        if (startTime == endTime) {
            // no gc
            return;
        }

        if (duration >= this.gcDuration) {
            logger.error("gc时间过长(耗时:{}ms)", duration);
        }

        Map<String, MemoryUsage> memoryUsageBeforeGc = gci.getMemoryUsageBeforeGc();
        Map<String, MemoryUsage> memoryUsageAfterGc = gci.getMemoryUsageAfterGc();
        memoryUsageBeforeGc.forEach((key, memory) -> {
            logger.info("{} Before GC:{}", key, memory);
            MemoryUsage afterMemory = memoryUsageAfterGc.get(key);
            logger.info("{} After GC:{}", key, afterMemory);
        });


    }

}
