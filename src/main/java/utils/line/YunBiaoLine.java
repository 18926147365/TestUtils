package utils.line;

import cn.hutool.core.swing.RobotUtil;
import cn.hutool.core.thread.ThreadUtil;
import org.sikuli.script.Match;
import org.sikuli.script.Screen;
import utils.SikuliUtils;
import vo.LocalVo;

public class YunBiaoLine {

    public static void main(String[] args) throws Exception {
        run();
    }


    public static void run() throws Exception {
        SikuliUtils.findAndClickAct("日常活动-运镖");
        //打开日常任务面板c
        Thread.sleep(1000);
        SikuliUtils.waitMoveAndClick("押送普通镖银",300,3000);
        SikuliUtils.waitMoveAndClick("押镖确认按钮",300,3000);
        SikuliUtils.waitMoveAndClick("押送普通镖银",300,3000);
        SikuliUtils.waitMoveAndClick("押镖确认按钮",300,3000);
        SikuliUtils.waitMoveAndClick("押送普通镖银",300,3000);
        SikuliUtils.waitMoveAndClick("押镖确认按钮",300,3000);
        System.out.println("押镖完成");
    }


}
