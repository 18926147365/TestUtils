package utils;

import cn.hutool.core.swing.RobotUtil;
import cn.hutool.core.swing.ScreenUtil;
import cn.hutool.core.text.finder.Finder;
import org.sikuli.script.*;


import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.*;
import java.util.List;

/**
 * @author lihaoming
 * @date 2023/3/30 10:44
 * @description
 */
public class QQTest {


    public static void main(String[] args) throws Exception {

//        Set<Integer> set = ff1();
//        System.out.println(set.size());
//        String path = "/Users/lihaoming/Desktop/1.png";
//        BufferedImage image = ImageIO.read(new File(path));
//        int height = image.getHeight();
//        int width = image.getWidth();

            Region region = Region.grow(new Location(1,1),300,400);
            System.out.println(region.exists("/Users/lihaoming/Desktop/55.png"));
            Thread.sleep(2000);


//        Screen screen = new Screen();

//        ScreenImage capture = screen.capture();
//        capture.save("/Users/lihaoming/Desktop");
//        Match match = screen.exists("/Users/lihaoming/Desktop/55.png");
//        System.out.println(match.x+","+match.y);
//        RobotUtil.mouseMove(114,match.y);
//        RobotUtil.mouseMove(match.x,match.y);
//        String path2 = "/Users/lihaoming/Desktop/2.png";
//        BufferedImage image2 = ImageIO.read(new File(path2));
//        ScreenRegion scrReg = new StaticImageScreenRegion(image.getImage());
//        ScreenRegion resReg = scrReg.find(image2);
//        ScreenLocation center = resReg.getCenter();
        //保存截图
//        File file = new File("/Users/lihaoming/Desktop/3.png");
//        ImageIO.write(image.getSubimage(0,0,98*2,72*2), "png", file);
//        for (int i = 72; i < height; i = i + 72) {
//            for (int j = 98; j < width; j = j + 98) {
//                System.out.println(j + "," + i+"命中:"+fq(image, j, i, set));
//            }
//        }
//        RobotUtil.mouseMove(98/2,72/2+24);
    }

    public static int fq(BufferedImage image,int a,int b ,  Set<Integer> set ) {
        Set<Integer> ds = new HashSet<>();
        for (int i = b-72; i < b; i = i + 1) {
            for (int j = a- 98 ; j < a; j = j + 1) {
                int rgb = image.getRGB(j, i);
                if (set.contains(rgb)) {
                    ds.add(rgb);
                }
            }
        }
        return ds.size();
    }


    public static Set<Integer> ff1() throws Exception {
        String path = "/Users/lihaoming/Desktop/2.png";
        BufferedImage image = ImageIO.read(new File(path));
        int height = image.getHeight();
        int width = image.getWidth();
        System.out.println(width);
        System.out.println(height);
        Set<Integer> list = new HashSet<>();
        for (int i = 0; i < height; i++) {
            for (int j = 0; j < width; j++) {
                list.add(image.getRGB(j, i));
            }
        }
        return list;

    }

    public static List<Integer> ff() throws Exception {
        String path = "/Users/lihaoming/Desktop/1.png";
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


    public static void capture(int x, int y, int width, int height) throws Exception {

        //创建一个robot对象
        Robot robot = new Robot();
        RobotUtil.mouseMove(2, 30);
        //获取屏幕分辨率
        Dimension d = Toolkit.getDefaultToolkit().getScreenSize();
        //打印屏幕分辨率
        //创建该分辨率的矩形对象
        Rectangle screenRect = new Rectangle(d);
        //根据这个矩形截图
        screenRect.setRect(x, y, width, height);
        BufferedImage bufferedImage = robot.createScreenCapture(screenRect);
//        //保存截图
        File file = new File("/Users/lihaoming/Desktop/1.png");
        ImageIO.write(bufferedImage, "png", file);
    }

}
