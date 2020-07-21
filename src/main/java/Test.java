
import bean.User;
import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import org.apache.commons.lang.StringUtils;
import org.springframework.util.StopWatch;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author 李浩铭
 * @date 2020/7/3 10:28
 * @descroption
 */
public class Test {



    public static void main(String[] args) throws Exception {
        System.out.println("提交2");
        System.out.println("提交test-2");
        StopWatch stopWatch=new StopWatch();
        stopWatch.start();
        int size=100000;
        CountDownLatch countDownLatch=new CountDownLatch(size);

        ExecutorService executorService=Executors.newFixedThreadPool(100);
        for (int i = 0; i < size; i++) {


            executorService.submit(new Runnable() {
                @Override
                public void run() {
                    System.out.println("123123");
                    try {
                        Thread.sleep(100);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    countDownLatch.countDown();
                }
            });

        }
        System.out.println("提交2-2");
        countDownLatch.await();
        stopWatch.stop();
        System.out.println(stopWatch.getTotalTimeSeconds());

    }


}
