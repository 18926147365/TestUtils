package utils;

import cn.hutool.core.swing.RobotUtil;
import cn.hutool.core.thread.ThreadUtil;
import com.sun.jna.platform.win32.User32;
import com.sun.jna.platform.win32.WinDef;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ThreadPoolExecutor;

public class HHTest {


    public static void main(String[] args) throws Exception {
        WinDef.HWND hwnd = User32.INSTANCE.FindWindow(null, "梦幻西游：时空");
        if (hwnd != null) {
            // 获取窗口大小
            WinDef.RECT rect = new WinDef.RECT();
            User32.INSTANCE.GetWindowRect(hwnd, rect);
            int width = rect.right - rect.left;
            int height = rect.bottom - rect.top;
            capture(rect.left, rect.top, width, height);
//            System.out.println(ff());
//            System.out.println(ff1());
            System.out.println("执行完成脚本");
        } else {
            System.out.println("找不到窗口");
        }
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
        List<Integer> ddList = dd();
        List<String> xxList = new ArrayList<>();
        List<Integer> yyList = new ArrayList<>();
        int ik = 0;
        boolean dqw= false;
        for (int i = 0; i < height; i++) {
            for (int j = 0; j < width; j++) {
                int rgb = (bufferedImage.getRGB(j,i));
//                if(ddList.contains(rgb)){
//                    xxList.add(j+","+i);
//                }
                if(rgb == ddList.get(ik)){
                    ik++;
                    if(ik>4){
                        RobotUtil.mouseMove(j+x,i+y);
                    }
                    System.out.println(ik);
                    if(ik == ddList.size()){
                        System.out.println(11111);
                    }
                }else{
                    ik=0;
                }
            }
        }
        System.out.println(ddList.size());


//        //保存截图
        File file = new File("C:\\Users\\Administrator\\Desktop\\1.png");
        ImageIO.write(bufferedImage, "png", file);
    }

    //970 726 1010 743
    public static List<Integer> ff() throws Exception {
        String path = "C:\\\\Users\\\\Administrator\\\\Desktop\\\\2.png";
        BufferedImage image = ImageIO.read(new File(path));
        int height = image.getHeight();
        int width = image.getWidth();
        List<Integer> list = new ArrayList<>();
        for (int i = 718; i < 755; i++) {
            for (int j = 974; j < 1012; j++) {
                list.add(image.getRGB(j, i));
            }
        }
        return list;

    }

    public static List<Integer> ff1() throws Exception {
        String path = "C:\\\\Users\\\\Administrator\\\\Desktop\\\\1.png";
        BufferedImage image = ImageIO.read(new File(path));
        int height = image.getHeight();
        int width = image.getWidth();
        List<Integer> list = new ArrayList<>();
        for (int i = 718; i < 755; i++) {
            for (int j = 974; j < 1012; j++) {
                list.add(image.getRGB(j, i));
            }
        }
        return list;

    }


    public static List<Integer> dd() throws Exception {
        String path = "C:\\Users\\Administrator\\Desktop\\功能图包\\在打架.png";
        BufferedImage image = ImageIO.read(new File(path));
        int height = image.getHeight();
        int width = image.getWidth();
        List<Integer> list = new ArrayList<>();
        for (int i = 0; i < height; i++) {
            for (int j = 0; j < width; j++) {
                list.add(image.getRGB(j, i));
            }
        }
        return list;

    }
}
