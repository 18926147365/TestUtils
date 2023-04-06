package utils.line;

import cn.hutool.core.swing.RobotUtil;
import cn.hutool.core.thread.ThreadUtil;
import org.sikuli.script.Match;
import utils.SikuliUtils;
import utils.WinDefUtils;
import vo.LocalVo;

public class SanJieQiYuanLine {

    public static void run() throws Exception {
        System.out.println("三界奇缘-开始");
        SikuliUtils.findAndClickAct("日常活动-三界奇缘");
        ThreadUtil.sleep(3000);
        LocalVo localVo = WinDefUtils.getLocalVo();
        RobotUtil.mouseMove(localVo.getFblx() + 500, localVo.getFbly() + 300);
        ThreadUtil.sleep(2000);
        for (int i = 0; i < 25; i++) {
            RobotUtil.click();
            ThreadUtil.sleep(1000);
            Match done = SikuliUtils.exists("三界奇缘答题完成");
            if (done != null) {
                System.out.println("三界奇缘答题完成");
                ThreadUtil.sleep(2000);
                SikuliUtils.escClick();
                ThreadUtil.sleep(2000);
                return;
            }
        }
    }

    public static void main(String[] args) throws Exception {
        run();
    }
}
