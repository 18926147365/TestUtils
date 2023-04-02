package utils.line;

import cn.hutool.core.swing.RobotUtil;
import cn.hutool.core.thread.ThreadUtil;
import org.sikuli.script.Match;
import utils.SikuliUtils;
import utils.WinDefUtils;
import vo.LocalVo;

public class MiJingXIangYaoLine {
    public static void main(String[] args) throws Exception {
        run();
    }

    public static void run() throws Exception {
        SikuliUtils.findAndClickAct("日常活动-秘境降妖");
        ThreadUtil.sleep(1000);
        SikuliUtils.waitMoveAndClick("秘境降妖按钮",30,3000);
        SikuliUtils.openRw();
        LocalVo localVo = WinDefUtils.getLocalVo();
        RobotUtil.mouseMove(localVo.getFblx() + 230, localVo.getFbly() + 420);
        ThreadUtil.sleep(1000);
        RobotUtil.click();
        ThreadUtil.sleep(1000);
        SikuliUtils.waitMoveAndClick("秘境降妖挑战按钮",10,3000);
        SikuliUtils.waitMoveAndClick("秘境降妖第1关挑战",10,3000);
        //如何判断打完
        SikuliUtils.waitMoveAndClick("秘境降妖一层打完",500,1000);
        System.out.println("日常活动-秘境降妖一层完成");
    }
}
