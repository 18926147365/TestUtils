package utils.line;

import cn.hutool.core.swing.RobotUtil;
import cn.hutool.core.thread.ThreadUtil;
import org.sikuli.script.Match;
import utils.SikuliUtils;
import utils.WinDefUtils;
import vo.LocalVo;

public class BaoTuLine {

    public static void main(String[] args) throws Exception {
        run();
        execute(10);//挖宝10次
    }

    public static void run() throws Exception {
        SikuliUtils.findAndClickAct("日常活动-宝图任务");
        ThreadUtil.sleep(1000);
        try{
            SikuliUtils.waitMoveAndClick("宝图去找按钮",10,3000);
        }catch (Exception e){
            System.out.println("未找到，可能已经接了任务");
        }
        SikuliUtils.openRw();
        LocalVo localVo = WinDefUtils.getLocalVo();
        RobotUtil.mouseMove(localVo.getFblx() + 960, localVo.getFbly() + 230);
        ThreadUtil.sleep(1000);
        RobotUtil.click();
        ThreadUtil.sleep(1000);
        Match exists = SikuliUtils.exists("任务-宝图任务1");
        if (exists == null) {
            SikuliUtils.waitMoveAndClick("任务-常规任务",10,1000);
        }
        ThreadUtil.sleep(3000);
        SikuliUtils.waitMoveAndClick("任务-宝图任务1",10,1000);
        SikuliUtils.waitMoveAndClick("任务-马上传送",10,1000);
        ThreadUtil.sleep(1000);
        SikuliUtils.escClick();
        //等待打完10张,等待约15分钟
        ThreadUtil.sleep(1000*60*15);


    }

    public static void execute(int d) throws Exception {
        SikuliUtils.findBb("背包-宝图");
        SikuliUtils.waitMoveAndClick("背包-宝图",30,1000);
        SikuliUtils.waitMoveAndClick("宝图-使用按钮",30,1000);
        for (int i = 0; i < d; i++) {
            SikuliUtils.waitMoveAndClick("宝图-藏宝图使用",300,1000);
        }
    }
}
