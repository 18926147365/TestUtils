import bean.User;
import org.apache.commons.lang.StringUtils;
import org.springframework.util.StopWatch;

import java.io.File;
import java.util.*;
import java.util.concurrent.*;
import java.util.stream.Collectors;

/**
 * @author 李浩铭
 * @date 2020/7/3 10:28
 * @descroption
 */
public class Test {



    public static class Count implements Callable<Integer> {

        @Override
        public Integer call() throws Exception {
            try {
                Thread.sleep(5000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            return 10;
        }
    }

    public static void main(String[] args) throws Exception {


        FutureTask<Integer> task = new FutureTask<Integer>(new Count());
        new Thread(task).start();
        int num=task.get();
        String f=take();
        System.out.println("!@3123123");
        int num=task.get();
        System.out.println(num+"人点了"+f);

    }


    public static String take(){
        try {
            Thread.sleep(3000);//点餐人选餐花费时间
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return "牛肉";
    }






}
