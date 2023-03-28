package utils;

import cn.hutool.core.date.DateUtil;
import cn.hutool.core.swing.RobotUtil;
import cn.hutool.core.thread.ThreadUtil;
import com.sun.jna.platform.win32.User32;
import com.sun.jna.platform.win32.WinDef;
import net.sourceforge.tess4j.ITesseract;
import net.sourceforge.tess4j.Tesseract;
import net.sourceforge.tess4j.TesseractException;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * @author lihaoming
 * @date 2023/3/28 11:50
 * @description
 */
public class OcrUtils {


    public static void main(String[] args) throws Exception {
        int i =0;
        while (true){
            ThreadUtil.sleep(20000);
            delAll();
            ThreadUtil.sleep(2000);
            红手指截图();
            ThreadUtil.sleep(20000);
            String path = "C:\\Program Files (x86)\\RedFingerPro\\ScreenShot\\32934127\\VM010088150145";
            File delFiles = new File(path);

            for (File file : delFiles.listFiles()) {
                boolean done = 抓鬼一轮完成(file);
                System.out.println(DateUtil.format(new Date(),"yyyy-MM-dd HH:mm:ss")+"第"+(++i)+"次判断一轮抓鬼是否完成:"+done);
                if(done){
                    红手指执行抓鬼();
                    ThreadUtil.sleep(120000);
                    break;
                }
            }
        }

    }
    public static void delAll(){
        String path = "C:\\Program Files (x86)\\RedFingerPro\\ScreenShot\\32934127\\VM010088150145";
        File delFiles = new File(path);
        for (File file : delFiles.listFiles()) {
            file.delete();
        }
    }

    public static void 红手指执行抓鬼(){
        System.out.println("开始执行脚本");
        ThreadUtil.sleep(2500);
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
            System.out.println("执行完成脚本");
        } else {
            System.out.println("找不到窗口");
        }
    }

    public static void 红手指截图(){
        // 获取窗口句柄
        WinDef.HWND hwnd = User32.INSTANCE.FindWindow(null, "V1-3");
        if (hwnd != null) {
            // 获取窗口大小
            WinDef.RECT rect = new WinDef.RECT();
            User32.INSTANCE.GetWindowRect(hwnd, rect);
            User32.INSTANCE.SetCursorPos(rect.right-20,rect.top+100);
            RobotUtil.click();
        } else {
            System.out.println("找不到窗口");
        }
    }

    public static boolean 抓鬼一轮完成(File file) throws Exception {
        String path = "C:\\Users\\Administrator\\Desktop\\抓鬼一轮完成.png";
        BufferedImage image = ImageIO.read(new File(path));
        int x1=438,y1=333,x2=831,y2=333;
        BufferedImage image2 = ImageIO.read(file);
        for(int i =x1;i<x2;i++){
            int rgb = image.getRGB( i, y1);
            int rgb1 = image2.getRGB( i, y1);
            if(rgb1 != rgb){
                return false;
            }
        }
        return true;
    }

    public static String doOCR(BufferedImage image) throws TesseractException {
        //创建Tesseract对象
        ITesseract tesseract = new Tesseract();
        //设置中文字体库路径
        tesseract.setDatapath("D:\\git_work\\TestUtils\\src\\main\\resources\\tessdata");
        //中文识别
        tesseract.setLanguage("chi_sim");
        //执行ocr识别
        String result = tesseract.doOCR(image);
        //替换回车和tal键  使结果为一行
//        result = result.replaceAll("\\r|\\n", "-").replaceAll(" ", "");
        return result;
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

    private void tt(){
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
}
