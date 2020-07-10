
import bean.User;
import org.apache.commons.lang.ObjectUtils;
import org.apache.commons.lang.StringUtils;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;


/**
 * @author 李浩铭
 * @date 2020/7/3 10:28
 * @descroption
 */
public class Test {

    private static String cache="";

    public static void main(String[] args) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                getName1();

            }
        }).start();
        new Thread(new Runnable() {
            @Override
            public void run() {
                getName();

            }
        }).start();

    }

    static Boolean lock=false;
    static Boolean lock1=false;
    private static void getName() {
        synchronized ("key"){
            System.out.println("1");
        }
    }

    private static void getName1() {
        synchronized ("key"){
            try {
                Thread.sleep(1500);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            System.out.println("2");
        }
    }
}
