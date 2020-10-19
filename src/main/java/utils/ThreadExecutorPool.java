package utils;

import org.checkerframework.checker.units.qual.C;

import java.util.List;
import java.util.Map;
import java.util.concurrent.*;
import java.util.function.Consumer;
import java.util.function.Supplier;

/**
 * @author 李浩铭
 * @date 2020/7/29 11:20
 * @descroption
 */
public class ThreadExecutorPool {

    private static final Map<String,ExecutorService> poolMap=new ConcurrentHashMap<>();

    private static final String defalutPoolName="base_system";

    public static ExecutorService getExecutorService(String poolName,int coreSize,int maxSize){

        ExecutorService executorService=poolMap.get(poolName);
        if(executorService!=null){
            return executorService;
        }

        synchronized (("executors:"+poolName).intern()){
            executorService=poolMap.get(poolName);
            if(executorService!=null){
                return executorService;
            }
            //TODO 可以通过读数据获取配置
            executorService = new ThreadPoolExecutor(coreSize, maxSize, 10000,
                    TimeUnit.MILLISECONDS,
                    new LinkedBlockingQueue<>(),
                    Executors.defaultThreadFactory(),
                    new ThreadPoolExecutor.AbortPolicy());
            //设置空闲回收线程
            ((ThreadPoolExecutor) executorService).allowCoreThreadTimeOut(true);
            poolMap.put(poolName,executorService);
            return executorService;
        }
    }



    public static ExecutorService getExecutorService(String poolName){

     return getExecutorService(poolName,100,100);
    }


    public static ExecutorService setnxExecutorService(String poolName,ExecutorService pool){
        ExecutorService executorService=poolMap.get(poolName);
        if(executorService!=null){
            return executorService;
        }
        synchronized (("executors:"+poolName).intern()) {
            executorService=poolMap.get(poolName);
            if(executorService!=null){
                return executorService;
            }
            poolMap.put(poolName, pool);
            return pool;
        }
    }


    public static <T> Future<T> callFutureTask(String poolName,Callable<T> callable){

        FutureTask<T> futureTask=new FutureTask<>(callable);
        getExecutorService(poolName).submit(futureTask);

        return futureTask;
    }



    public static void start(Runnable r){
        start(defalutPoolName,r);
    }


    public static void start(String poolName,Runnable r){
        ExecutorService executorService= getExecutorService(poolName);
        executorService.submit(new Thread(r,poolName));
    }



}
