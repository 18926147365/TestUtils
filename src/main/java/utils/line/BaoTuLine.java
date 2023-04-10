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
       // run();
//        run1();
        execute();
        //-4878998

    }

    public static void run()throws Exception {
        SikuliUtils.openMap();
        SikuliUtils.waitMoveAndClick("地图-长安城",30,1000);
        SikuliUtils.clickTab();
        ThreadUtil.sleep(2000);
        SikuliUtils.moveAndClick(631,409);//点击店小二
        ThreadUtil.sleep(1000);
        try{
            SikuliUtils.waitMoveAndClick("宝图去找按钮",5,3000);
        }catch (Exception e){
            System.out.println("未找到，可能已经接了任务");
        }
        SikuliUtils.openRw();
        SikuliUtils.mouseMove(967,237);//点击任务当前
        Match exists = SikuliUtils.exists("任务-宝图任务1");
        if (exists == null) {
            SikuliUtils.waitMoveAndClick("任务-常规任务2",5,1000);
        }
        SikuliUtils.moveAndClick(831,665);//点击马上传送
        ThreadUtil.sleep(3000);
        SikuliUtils.escClick();
        ThreadUtil.sleep(1000);

        //等待打完
        for (int i = 0; i < 10; i++) {
            Match firting = SikuliUtils.exists("战斗中");
            if(firting != null){
                i = 0;
            }
            ThreadUtil.sleep(6000);
        }
        SikuliUtils.escClick();
        SikuliUtils.escClick();
        System.out.println("挖宝战斗结束-开始挖宝");
        ThreadUtil.sleep(3000);

        execute();
    }


    public static void execute() throws Exception {
        SikuliUtils.findBb("背包-宝图");
        ThreadUtil.sleep(3000);
        SikuliUtils.waitMoveAndClick("背包-宝图",30,1000);
        SikuliUtils.moveAndClick(384,498);//宝图-使用按钮
        ThreadUtil.sleep(3000);
        int i = 0;
        while (i < 10){
            int pixelColor = RobotUtil.getRobot().getPixelColor(869, 571).getRGB();//查看是否展示了藏宝图的颜色
            //颜色-4878998
            if(pixelColor == -4878998){
                SikuliUtils.moveAndClick(862,668);//点击使用
                i = 0;
            }else{
                i++;
            }
            ThreadUtil.sleep(6000);
        }
        System.out.println("挖宝战斗结束-结束挖宝");
    }
}
