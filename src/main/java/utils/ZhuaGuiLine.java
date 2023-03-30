package utils;

import cn.hutool.core.swing.RobotUtil;
import cn.hutool.core.thread.ThreadUtil;
import org.sikuli.script.Match;
import vo.LocalVo;

public class ZhuaGuiLine {

    public static final String PATH = SikuliUtils.PATH;


    public static void main(String[] args) throws Exception {
        SikuliUtils.cyclicIfExists("一轮抓鬼完成",10,10000,math->{
            System.out.println("完成一轮抓鬼");
            SikuliUtils.waitMoveAndClick("一轮抓鬼完成按钮",10,5000);
            SikuliUtils.waitMoveAndClick("抓鬼任务按钮",10,2000);
            SikuliUtils.waitMoveAndClick("任务栏抓鬼",10,2000);
            SikuliUtils.waitMoveAndClick("任务栏抓鬼",10,2000);
        });
    }


}
