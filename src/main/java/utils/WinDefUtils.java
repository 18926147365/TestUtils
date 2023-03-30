package utils;

import cn.hutool.core.swing.RobotUtil;
import cn.hutool.core.thread.ThreadUtil;
import com.sun.jna.platform.win32.User32;
import com.sun.jna.platform.win32.WinDef;
import vo.LocalVo;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;

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
            LocalVo localVo = new LocalVo();
            localVo.setX(rect.left);
            localVo.setY(rect.top);
            localVo.setWidth(rect.right- rect.left);
            localVo.setHeight(rect.bottom- rect.top);
        } else {
            System.out.println("找不到窗口");
        }
    }

    public static LocalVo getLocalVo(String name){
        // 获取窗口句柄
        WinDef.HWND hwnd = User32.INSTANCE.FindWindow(null, name);
        if (hwnd != null) {
            // 获取窗口大小
            WinDef.RECT rect = new WinDef.RECT();
            User32.INSTANCE.GetWindowRect(hwnd, rect);
            LocalVo localVo = new LocalVo();
            localVo.setX(rect.left);
            localVo.setY(rect.top);
            localVo.setWidth(rect.right- rect.left);
            localVo.setHeight(rect.bottom- rect.top);
            return localVo;
        } else {
            System.out.println("找不到窗口");
        }
        return null;
    }

    public static void capture(int x, int y, int width, int height) throws Exception {
        //创建一个robot对象
        Robot robot = new Robot();
        //获取屏幕分辨率
        Dimension d = Toolkit.getDefaultToolkit().getScreenSize();
        //打印屏幕分辨率
        //创建该分辨率的矩形对象
        Rectangle screenRect = new Rectangle(d);
        //根据这个矩形截图
        screenRect.setRect(x, y, width, height);
        BufferedImage bufferedImage = robot.createScreenCapture(screenRect);
        //保存截图
        File file = new File("C:\\Users\\Administrator\\Desktop\\img\\1.png");
        ImageIO.write(bufferedImage, "png", file);
    }


}
