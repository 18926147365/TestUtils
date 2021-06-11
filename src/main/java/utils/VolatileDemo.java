package utils;

/**
 * @Description:
 * @Author: lihaoming
 * @Date: 2021/6/11 下午5:28
 */
public class VolatileDemo {

    public static void main(String[] args) {
        MyThread myThread = new MyThread();
        // 开启线程
        myThread.start();

        // 主线程执行
        while (true){
            if (myThread.isFlag()) {
                System.out.println("主线程访问到 flag 变量");
            }
        }
    }
     static class MyThread extends Thread {

        private volatile boolean flag = false;//被volatile修饰的成员属性可以对其他线程可见
//        private  boolean flag = false;

        @Override
        public void run() {
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            // 修改变量值
            flag = true;
            System.out.println("flag = " + flag);
        }

        public boolean isFlag() {
            return flag;
        }

    }

    // volatile 保证可见性和禁止指令重排序
    private static volatile VolatileDemo singleton;

    public static VolatileDemo getInstance() {
        // 第一次检查
        if (singleton == null) {
            // 同步代码块
            synchronized(singleton) {
                // 第二次检查
                if (singleton == null) {
                    // 对象的实例化是一个非原子性操作
                    singleton = new VolatileDemo();
                }
            }
        }
        return singleton;
    }
}
