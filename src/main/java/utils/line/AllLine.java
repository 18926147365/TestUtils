package utils.line;

import cn.hutool.core.thread.ThreadUtil;
import org.sikuli.script.Match;
import utils.SikuliUtils;

public class AllLine {


    public static void main(String[] args) throws Exception {
        ThreadUtil.execute(new Runnable() {
            @Override
            public void run() {
                SikuliUtils.listenClose();
            }
        });
        //三界奇缘
        SanJieQiYuanLine.run();
//        宝图
        BaoTuLine.run();
        //秘境降妖
        MiJingXIangYaoLine.run();
//        //押镖
        YunBiaoLine.run();
        //科举乡试
        // KeJuXiangShi.run();
    }
}
