package utils;

import org.apache.commons.lang.StringUtils;

import java.awt.*;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.util.ArrayList;
import java.util.List;

/**
 * @author 李浩铭
 * @date 2020/7/7 17:22
 * @descroption
 */
public class CopyUtils {



    private static final String logFile="";
    private static final long sleepTime=2000l;

    public static List<String> cacheList=new ArrayList<String>();
    private static long cacheSize=50;//缓存长度
    private static String lastStr="";




    public static void listenerCopy()  {

        try {
            while (true){
                Thread.sleep(sleepTime);
                addCache(getSysClipboardText());
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    private static void addCache(String text){
        if(StringUtils.isBlank(text)){
            return;
        }
        if(lastStr.equals(text)){
            return;
        }
        //若存在则删除旧数据
        if(cacheList.contains(text)){
            cacheList.remove(text);
        }
        lastStr=new String(text);
        cacheList.add(text);
    }
    


    /**
     *1. 从剪切板获得文字。
     */
    public static String getSysClipboardText() {
        String ret = "";
        Clipboard sysClip = Toolkit.getDefaultToolkit().getSystemClipboard();
        // 获取剪切板中的内容
        Transferable clipTf = sysClip.getContents(null);

        if (clipTf != null) {
            // 检查内容是否是文本类型
            if (clipTf.isDataFlavorSupported(DataFlavor.stringFlavor)) {
                try {
                    ret = (String) clipTf
                            .getTransferData(DataFlavor.stringFlavor);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }

        return ret;
    }
}
