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
        SikuliUtils.moveAndClick(250, 438);//点击第一关
        SikuliUtils.waitMoveAndClick("秘境降妖挑战按钮", 10, 3000);
        SikuliUtils.moveAndClick(882, 321);//点击第一关
        ThreadUtil.execute(new Runnable() {
            @Override
            public void run() {
                for (int i = 0; i < 10; i++) {
                    Match match = SikuliUtils.exists("秘境降妖进入战斗");
                    if (match != null) {
                        i = 0;
                        try {
                            SikuliUtils.waitMoveAndClick(match, 1000);
                        } catch (Exception e) {
                        }
                    }
                    ThreadUtil.sleep(30000);
                }
            }
        });

        SikuliUtils.waitMoveAndClick("秘境降妖一层打完", 1000, 1000);
        SikuliUtils.escClick();
        ThreadUtil.sleep(1000);
        //如何判断打完
        System.out.println("日常活动-秘境降妖一层完成");
    }
}
