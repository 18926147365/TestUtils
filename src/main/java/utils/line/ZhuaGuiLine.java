package utils.line;

import cn.hutool.core.date.DateUtil;
import cn.hutool.core.swing.RobotUtil;
import cn.hutool.core.thread.ThreadUtil;
import org.sikuli.script.Match;
import utils.SikuliUtils;
import vo.LocalVo;

import java.awt.*;
import java.util.Date;

public class ZhuaGuiLine {

    public static final String PATH = SikuliUtils.PATH;
    public static void main(String[] args) throws Exception {
        run();
    }


    public static void run() throws Exception {
        SikuliUtils.listenClose();
        int size = 20;//共抓多少轮
        SikuliUtils.cyclicIfExists("是否继续抓鬼", size, 10000, math -> {
            System.out.println("第" + math.getCycleIndex() + "次完成抓鬼一轮 时间:" + DateUtil.format(new Date(), "yyyy-MM-dd HH:mm:ss"));
            SikuliUtils.waitMoveAndClick("是否继续抓鬼-确认按钮", 60, 5000);
            SikuliUtils.waitMoveAndClick("抓鬼任务按钮", 60, 2000);
            SikuliUtils.waitMoveAndClick("去抓鬼右边任务栏", 60, 2000);
            SikuliUtils.waitMoveAndClick("去抓鬼右边任务栏", 60, 2000);
        });
    }


}
