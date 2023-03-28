package utils;

import com.sun.jna.platform.win32.User32;
import com.sun.jna.platform.win32.WinDef;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;

/**
 * @author lihaoming
 * @date 2023/3/28 11:50
 * @description
 */
public class OcrUtils {


    public static void main(String[] args) {
        // 获取窗口句柄
        WinDef.HWND hwnd = User32.INSTANCE.FindWindow(null, "窗口标题");
        if (hwnd != null) {
            // 获取窗口大小
            WinDef.RECT rect = new WinDef.RECT();
            User32.INSTANCE.GetWindowRect(hwnd, rect);
            int width = rect.right - rect.left;
            int height = rect.bottom - rect.top;
            System.out.println("窗口大小: " + width + "x" + height);

            // 获取窗口坐标
            WinDef.POINT point = new WinDef.POINT();
            User32.INSTANCE.GetCursorPos(point);
            System.out.println("窗口坐标: (" + point.x + ", " + point.y + ")");
        } else {
            System.out.println("找不到窗口");
        }
    }
    public static void c() throws Exception {
        //创建一个robot对象
        Robot robut=new Robot();
        //获取屏幕分辨率
        Dimension d =  Toolkit.getDefaultToolkit().getScreenSize();
        //打印屏幕分辨率
        System.out.println(d);
        //创建该分辨率的矩形对象
        Rectangle screenRect=new  Rectangle(d);
        //根据这个矩形截图
//        screenRect.setRect(10,20,100,100);
        BufferedImage bufferedImage=robut.createScreenCapture(screenRect);
        //保存截图
        File file=new File("/Users/lihaoming/Desktop/截图1.png");
        ImageIO.write(bufferedImage,"png",file);
    }

}
