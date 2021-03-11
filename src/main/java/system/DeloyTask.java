package system;

import java.util.concurrent.Delayed;
import java.util.concurrent.TimeUnit;

/**
 * @Description:
 * @Author: lihaoming
 * @Date: 2021/2/25 下午4:11
 */
public class DeloyTask<T> implements Delayed {

    private long time;  //延时的时间  单位为毫秒

    private T task;     //任务类，也就是之前定义的任务类

    /**
     *  超时时间(毫秒)
     * @param task
     *  任务
     */
    public DeloyTask(T task,long time) {
        super();
        this.time = System.currentTimeMillis() + time;  // 过期时间为当前时间加上延迟时间
        this.task = task;
    }
    public DeloyTask(){}

    @Override
    public int compareTo(Delayed o) {
        long time = getDelay(TimeUnit.NANOSECONDS) - o.getDelay(TimeUnit.NANOSECONDS);
        return (time == 0) ? 0 :((time < 0) ? -1 : 1);
    }

    @Override
    public long getDelay(TimeUnit unit) {
        return unit.convert(this.time - System.currentTimeMillis(), unit);
    }

    public long getTime() {
        return time;
    }

    public void setTime(long time) {
        this.time = time;
    }

    public T getTask() {
        return task;
    }

    public void setTask(T task) {
        this.task = task;
    }
}
