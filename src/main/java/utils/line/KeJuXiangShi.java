package utils.line;

import cn.hutool.core.swing.RobotUtil;
import cn.hutool.core.thread.ThreadUtil;
import org.sikuli.script.Match;
import utils.RandomUtil;
import utils.SikuliUtils;
import utils.WinDefUtils;
import vo.LocalVo;

public class KeJuXiangShi {

    public static void main(String[] args) throws Exception {
        run();
    }

    public static void run() throws Exception {
        SikuliUtils.findAndClickAct("日常活动-科举乡试");
        for (int i = 0; i < 10; i++) {
            LocalVo localVo = WinDefUtils.getLocalVo();
            int x = 460;
            int y = 400;
            RobotUtil.mouseMove(localVo.getFblx()+x+ RandomUtil.rand(10),localVo.getFbly()+y+ RandomUtil.rand(10));
            ThreadUtil.sleep(1000);
            RobotUtil.click();
            ThreadUtil.sleep(2000);
        }
        ThreadUtil.sleep(1000);
        SikuliUtils.escClick();
        System.out.println("日常活动-科举乡试 完成");

    }
}
