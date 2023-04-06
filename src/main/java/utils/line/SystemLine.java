package utils.line;

import cn.hutool.core.convert.Convert;
import cn.hutool.core.swing.RobotUtil;
import cn.hutool.core.thread.ThreadUtil;
import cn.hutool.core.util.CharUtil;
import com.ibm.icu.impl.locale.AsciiUtil;
import org.sikuli.script.Screen;
import utils.RandomUtil;
import utils.SikuliUtils;

import java.awt.*;

public class SystemLine {

    public static void main(String[] args)  throws Exception {
        inputPressString("bb18926147365@163.com");
        inputPressString("");
    }
    public static void inputPressString(String str){
        Screen screen = SikuliUtils.screen;
        for (int i = 0; i < str.length(); i++) {
            String s = str.substring(i, i + 1);
            ThreadUtil.sleep(RandomUtil.rand(500));
            screen.keyDown(s);
            screen.keyUp(s);
        }
    }
    public static void exitAccount() throws Exception {
        SikuliUtils.openSystem();
        SikuliUtils.waitMoveAndClick("基础设置-切换账号",10,3000);
        ThreadUtil.sleep(2000);
        SikuliUtils.waitMoveAndClick("基础设置-切换账号-登出",10,3000);
    }
}
