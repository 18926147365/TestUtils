package utils.line;

import cn.hutool.core.swing.RobotUtil;
import cn.hutool.core.thread.ThreadUtil;
import org.sikuli.script.Match;
import utils.RandomUtil;
import utils.SikuliUtils;
import utils.WinDefUtils;
import vo.LocalVo;

import java.awt.*;

public class BaoTuLine {

    public static void main(String[] args) throws Exception {
        run();
//        execute(10);
    }

    public static void run() throws Exception {
        SikuliUtils.openMap();
        SikuliUtils.waitMoveAndClick("地图-长安城",30,1000);
        SikuliUtils.clickTab();
        ThreadUtil.sleep(3000);
        Match match = SikuliUtils.waitMoveAndClick("地图-找人按钮", 30, 1000);
        if (match != null) {
            RobotUtil.mouseMove(match.x + 10, match.y + 100);
            String name = "地图-店小二";
            Match match1 = SikuliUtils.mouseWheelFind(name, 10);
            SikuliUtils.waitMoveAndClick(name,20,2000);
        }
        ThreadUtil.sleep(1000);
        try{
            SikuliUtils.waitMoveAndClick("宝图去找按钮",10,3000);
        }catch (Exception e){
            System.out.println("未找到，可能已经接了任务");
        }
        SikuliUtils.openRw();
        try{
            SikuliUtils.waitMoveAndClick("任务-当前",10,3000);
        }catch (Exception e){
            System.out.println("未找到，可能已经接了任务");
        }

        Match exists = SikuliUtils.exists("任务-宝图任务1");
        if (exists == null) {
            SikuliUtils.waitMoveAndClick("任务-常规任务2",10,1000);
        }
        ThreadUtil.sleep(3000);
        SikuliUtils.waitMoveAndClick("任务-宝图任务1",10,1000);
        SikuliUtils.waitMoveAndClick("任务-马上传送",10,1000);
        ThreadUtil.sleep(1000);
        SikuliUtils.escClick();
        //等待打完10张
        for (int i = 0; i < 10; i++) {
            Match firting = SikuliUtils.exists("战斗中");
            if(firting != null){
                i=0;
            }
            ThreadUtil.sleep(6000);
        }
        SikuliUtils.escClick();
        SikuliUtils.escClick();
        SikuliUtils.escClick();
        System.out.println("挖宝战斗结束-开始挖宝");
        execute(10);//挖宝10次
    }

    public static void execute(int d) throws Exception {
        SikuliUtils.findBb("背包-宝图");
        SikuliUtils.waitMoveAndClick("背包-宝图",30,1000);
        SikuliUtils.waitMoveAndClick("宝图-使用按钮",30,1000);
        for (int i = 0; i < d; i++) {
            LocalVo localVo = WinDefUtils.getLocalVo();
            int x = 840;
            int y = 650;
            a:while (true){
                int x1 = localVo.getFblx()+x;
                int y1 =  localVo.getFbly()+y;
                Color pixelColor = RobotUtil.getRobot().getPixelColor(x1, y1);
                if(pixelColor.getRGB() == -801435){
                    i=0;
                    RobotUtil.mouseMove(RandomUtil.rand(5)+x1,RandomUtil.rand(5)+y1);
                    ThreadUtil.sleep(1000);
                    RobotUtil.click();
                    break a;
                }
                ThreadUtil.sleep(5000);
            }

        }
        ThreadUtil.sleep(20000);
    }
}
