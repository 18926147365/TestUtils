package utils;

import cn.hutool.core.swing.RobotUtil;
import cn.hutool.core.thread.ThreadUtil;
import com.sun.jna.platform.win32.User32;
import com.sun.jna.platform.win32.WinDef;

import java.awt.*;

/**
 * @author lihaoming
 * @date 2023/3/28 15:30
 * @description
 */
public class WinDefUtils {

    public static void main(String[] args) {
        // 获取窗口句柄
        WinDef.HWND hwnd = User32.INSTANCE.FindWindow(null, "红手指专业版");
        if (hwnd != null) {
            // 获取窗口大小
            WinDef.RECT rect = new WinDef.RECT();
            User32.INSTANCE.GetWindowRect(hwnd, rect);
            ThreadUtil.sleep(1500);
            User32.INSTANCE.SetCursorPos(rect.left+622,rect.top+173);
            ThreadUtil.sleep(1500);
            RobotUtil.click();
            ThreadUtil.sleep(1500);
            User32.INSTANCE.SetCursorPos(rect.left+768,rect.top+230);
            ThreadUtil.sleep(1500);
            RobotUtil.click();

        } else {
            System.out.println("找不到窗口");
        }
    }



}
