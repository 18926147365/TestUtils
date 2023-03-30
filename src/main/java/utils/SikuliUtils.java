package utils;

import cn.hutool.core.swing.RobotUtil;
import cn.hutool.core.thread.ThreadUtil;
import org.sikuli.script.*;
import vo.LocalVo;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.function.Consumer;
import java.util.function.Function;

/**
 * @author lihaoming
 * @date 2023/3/30 17:46
 * @description
 */
public class SikuliUtils {

    public static final String PATH = "C:\\Users\\49949\\Desktop\\img\\";
    public static final String PATH_F = ".png";

    public static void main(String[] args) throws Exception {
        LocalVo localVo = WinDefUtils.getLocalVo("梦幻西游：时空");
        Match match = exists("抓鬼任务按钮");

        if(match!=null){
            RobotUtil.mouseMove(match.x+match.w/2,match.y+match.h/2);
//            RobotUtil.click();
//            ThreadUtil.sleep(1000);
//            RobotUtil.click();
        }
        System.out.println(match);
    }




    public static void cyclicIfExists(String fileName,long millis, Consumer<Match> consumer){
        cyclicIfExists(fileName,1,millis,consumer);
    }
    public static void cyclicIfExists(String fileName,int cyclicCount,long millis, Consumer<Match> consumer){
        int i = 0;
        while (true){
            Match math = exists(fileName);
            if(math!=null){
                i++;
                ThreadUtil.sleep(500);
                consumer.accept(math);
            }
            if(i>=cyclicCount){
                break;
            }
            ThreadUtil.sleep(millis);
        }
    }






    public static void waitMoveAndClick( Match match,int sleepMillis) {
        ThreadUtil.sleep(1000);
        RobotUtil.mouseMove(match.x+match.w/2,match.y+match.h/2);
        ThreadUtil.sleep(1000);
        RobotUtil.click();
        ThreadUtil.sleep(sleepMillis);
    }

    public static void waitMoveAndClick(String fileName, int waitSecond,int sleepMillis) {
        Match match = null;
        try {
            match = wait(fileName, waitSecond);
            waitMoveAndClick(match,sleepMillis);
            System.out.println(fileName+" 点击完成");
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public static Match wait(String fileName,double timeout) throws Exception {
        Screen screen = new Screen();
//        Region region = Region.grow(new Location(vo.getX(), vo.getY()), vo.getWidth(), vo.getHeight());
        Match wait = screen.wait(PATH + fileName+PATH_F,timeout);
        return wait;
    }

    public static Match exists(String fileName){
        Screen screen = new Screen();

//        Region screen = Region.grow(new Location(vo.getX(), vo.getY()), vo.getWidth(), vo.getHeight());
        Match wait = screen.exists(PATH + fileName+PATH_F);
        return wait;
    }


}
