package system;

/**
 * @Description:
 * @Author: lihaoming
 * @Date: 2021/1/27 上午11:26
 */
public class Singleton {
    private static volatile Singleton singleton = null;

    private static final Singleton singleton2 = new Singleton();
    public Singleton() {
    }

    public void getString(){
        try {
            Thread.sleep(100);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
    public static Singleton getSingleton(){
        return  singleton2;
    }
}
