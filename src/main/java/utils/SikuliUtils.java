package utils;

import cn.hutool.core.date.DateUtil;
import cn.hutool.core.swing.RobotUtil;
import cn.hutool.core.thread.ThreadUtil;
import org.bouncycastle.jcajce.provider.symmetric.SCRYPT;
import org.sikuli.script.*;
import vo.LocalVo;
import vo.MatchVo;

import java.awt.*;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Random;
import java.util.concurrent.CountDownLatch;
import java.util.function.Consumer;
import java.util.function.Function;

/**
 * @author lihaoming
 * @date 2023/3/30 17:46
 * @description
 */
public class SikuliUtils {

    public final static Screen screen = new Screen();
    public static final String PATH = "C:\\Users\\Administrator\\Desktop\\img\\";
    public static final String PATH_F = ".png";


    public static void main(String[] args) throws Exception {
//        背包-宝图.png

    }

    public static void mouseWheel(int amt) {
        int d = amt < 0 ? -amt : amt;
        int f = amt < 0 ? -1 : 1;
        for (int i = 0; i < d; i++) {
            RobotUtil.mouseWheel(f);
            ThreadUtil.sleep(200);
        }
    }

    public static Match findAndClickAct(String name) throws Exception {
        Match match = findAct(name);
        RobotUtil.mouseMove(match.x + match.w - 50, match.y + match.h / 2);
        ThreadUtil.sleep(300);
        RobotUtil.click();
        return match;
    }

    public static Match findBb(String name) throws Exception {
        openBB();
        LocalVo localVo = WinDefUtils.getLocalVo("梦幻西游：时空");
        RobotUtil.mouseMove(localVo.getFblx() + 560, localVo.getFbly() + 300);

        return  mouseWheelFind(name,5);
    }

    public static Match findAct(String name) throws Exception {
        openAct();
        LocalVo localVo = WinDefUtils.getLocalVo("梦幻西游：时空");
        RobotUtil.mouseMove(localVo.getFblx() + 500, localVo.getFbly() + 300);

        return  mouseWheelFind(name,3);
    }

    public static Match mouseWheelFind(String name,int size){
        for (int i = 0; i < size; i++) {

            Match match = exists(name);
            if (match != null) {
                return match;
            }
            ThreadUtil.sleep(500);
            mouseWheel(8);

        }
        for (int i = 0; i < size; i++) {
            Match match = exists(name);
            if (match != null) {
                return match;
            }
            ThreadUtil.sleep(500);
            mouseWheel(-8);

        }
        return null;
    }


    public static void listenClose() {
        ThreadUtil.execute(() -> {
            SikuliUtils.cyclicIfExists("福利标题栏", 100, 3000, match -> {
                SikuliUtils.waitMoveAndClick("福利标题栏-关闭", 10, 1000);
            });
        });
        ThreadUtil.execute(() -> {
            SikuliUtils.cyclicIfExists("监听需要关闭界面2", 100, 3000, match -> {
                SikuliUtils.waitMoveAndClick("监听需要关闭界面2-关闭", 10, 1000);
            });
        });
    }

    public static void openBB() throws AWTException {
        //先判断是否已经打开了任务
        Match math = exists("背包标题栏");
        if (math != null) {
            return;
        }
        Robot ROBOT = RobotUtil.getRobot();
        ROBOT.keyPress(18);
        ROBOT.delay(500);
        ROBOT.keyPress(69);
        ROBOT.delay(500);
        ROBOT.keyRelease(69);
        ROBOT.delay(500);
        ROBOT.keyRelease(18);
    }

    public static void openSystem() throws AWTException {
        //先判断是否已经打开了任务
        Match math = exists("基础设置栏");
        if (math != null) {
            return;
        }
        Robot ROBOT = RobotUtil.getRobot();
        ROBOT.keyPress(18);
        ROBOT.delay(500);
        ROBOT.keyPress(74);
        ROBOT.delay(500);
        ROBOT.keyRelease(74);
        ROBOT.delay(500);
        ROBOT.keyRelease(18);
    }


    public static void openJs() throws AWTException {
        Match math = exists("人物标题栏");
        if (math != null) {
            return;
        }
        Robot ROBOT = RobotUtil.getRobot();
        ROBOT.keyPress(18);
        ROBOT.delay(500);
        ROBOT.keyPress(87);
        ROBOT.delay(500);
        ROBOT.keyRelease(87);
        ROBOT.delay(500);
        ROBOT.keyRelease(18);
    }
    public static void openRw() throws AWTException {
        //先判断是否已经打开了任务
        Match math = exists("任务界面");
        if (math != null) {
            return;
        }
        Robot ROBOT = RobotUtil.getRobot();
        ROBOT.keyPress(18);
        ROBOT.delay(500);
        ROBOT.keyPress(89);
        ROBOT.delay(500);
        ROBOT.keyRelease(89);
        ROBOT.delay(500);
        ROBOT.keyRelease(18);
    }

    public static void openAct() throws AWTException {
        //先判断是否已经打开了任务
        Match math = exists("活动界面");
        if (math != null) {
            return;
        }
        Robot ROBOT = RobotUtil.getRobot();
        ROBOT.keyPress(18);
        ROBOT.delay(500);
        ROBOT.keyPress(67);
        ROBOT.delay(500);
        ROBOT.keyRelease(67);
        ROBOT.delay(500);
        ROBOT.keyRelease(18);
    }

    public static void clickTab() throws AWTException {
        Robot ROBOT = RobotUtil.getRobot();
        ROBOT.keyPress(9);
        ROBOT.delay(500);
        ROBOT.keyRelease(9);
    }

    public static void openMap() throws AWTException {
        //先判断是否已经打开了任务
        Match math = exists("地图界面");
        if (math != null) {
            return;
        }
        Robot ROBOT = RobotUtil.getRobot();
        ROBOT.keyPress(18);
        ROBOT.delay(500);
        ROBOT.keyPress(77);
        ROBOT.delay(500);
        ROBOT.keyRelease(77);
        ROBOT.delay(500);
        ROBOT.keyRelease(18);
    }

    public static void escClick() {
        RobotUtil.keyClick(27);
        ThreadUtil.sleep(1000);
    }

    public static void cyclicIfExists(String fileName, long millis, Consumer<MatchVo> consumer) {
        cyclicIfExists(fileName, 1, millis, consumer);
    }

    public static void cyclicIfExists(String fileName, int cyclicCount, long millis, Consumer<MatchVo> consumer) {
        int i = 0;
        while (true) {
            Match math = exists(fileName);
            if (math != null) {
                i++;
                ThreadUtil.sleep(500);
                MatchVo matchVo = new MatchVo();
                matchVo.setMatch(math);
                matchVo.setCycleIndex(i);
                consumer.accept(matchVo);
            }
            if (i >= cyclicCount) {
                break;
            }
            ThreadUtil.sleep(millis);
        }
    }


    public static void waitMoveAndClick(Match match, int sleepMillis) {
        ThreadUtil.sleep(1000);
        int w = match.w / 2;
        int h = match.h / 2;
        w = w + (int) (Math.random() * (w / 3));
        h = h + (int) (Math.random() * (h / 3));
        RobotUtil.mouseMove(match.x + w, match.y + h);
        ThreadUtil.sleep(1000);
        RobotUtil.click();
        ThreadUtil.sleep(sleepMillis);
    }

    public static Match waitMoveAndClick(String fileName, int waitSecond, int sleepMillis) {
        Match match = null;
        try {
            match = wait(fileName, waitSecond);
            waitMoveAndClick(match, sleepMillis);
            System.out.println(fileName + " 点击完成");
        } catch (Exception e) {
            e.printStackTrace();
            try {
                WinDefUtils.capture(DateUtil.format(new Date(), "yyyyMMddHHmmss"));
            } catch (Exception ex) {
                throw new RuntimeException(ex);
            }
        }
        return match;

    }

    public static Match wait(String fileName, double timeout) throws Exception {
        Match wait = screen.wait(PATH + fileName + PATH_F, timeout);
        return wait;
    }

    public static Match exists(String fileName) {
        Match wait = screen.exists(PATH + fileName + PATH_F);
        return wait;
    }


}
