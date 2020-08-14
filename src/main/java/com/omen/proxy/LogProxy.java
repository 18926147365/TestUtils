package com.omen.proxy;

import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.classic.spi.IThrowableProxy;
import ch.qos.logback.classic.spi.StackTraceElementProxy;
import com.google.common.hash.BloomFilter;
import com.google.common.hash.Funnels;
import com.omen.bean.LogModel;
import javassist.ClassPool;
import javassist.CtClass;
import javassist.CtMethod;
import javassist.LoaderClassPath;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Marker;

import java.net.InetAddress;
import java.net.NetworkInterface;
import java.nio.charset.Charset;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author 李浩铭
 * @date 2020/8/14 11:48
 * @descroption
 */
public class LogProxy {


    static {
        System.out.println("日志代理类修改开始");
        ClassPool classPool = ClassPool.getDefault();
        ClassPool pool = ClassPool.getDefault();
        classPool.appendClassPath(new LoaderClassPath(Thread.currentThread().getContextClassLoader()));
        CtClass cc = null;
        try {
            cc = pool.get("ch.qos.logback.classic.Logger");
            CtClass[] ctClasses = new CtClass[1];
            ctClasses[0] = pool.get("ch.qos.logback.classic.spi.ILoggingEvent");
            CtMethod cm = cc.getDeclaredMethod("callAppenders", ctClasses);
            cm.insertBefore("com.omen.proxy.LogProxy.errorEvent(event);");
            cc.toClass();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    private static SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
    private static String HOST_NAME = "未知主机";
    private static String HOST_IP = "未知IP";
    private static final Map<String, LogModel> LOG_MODEL_MAP = new ConcurrentHashMap<>();

    static {
        try {
            InetAddress address = InetAddress.getLocalHost();
            NetworkInterface ni = NetworkInterface.getByInetAddress(address);
            ni.getInetAddresses().nextElement().getAddress();
            HOST_IP = address.getHostAddress();
            HOST_NAME = (System.getProperty("user.name"));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public static void errorEvent(ILoggingEvent event) {
        try {
            if (event == null || !"ERROR".equals(event.getLevel().levelStr.toUpperCase())) {
                return;
            }

            String loggName = event.getLoggerName();
            if (!StringUtils.isBlank(loggName) && loggName.startsWith("com.omen")) {
                //错误日志扩展


                LogModel logModel = getLogModel(event.getMessage());
                String ocurrTime="首次发生";
                if (logModel.getBirthTime() != 0) {
                    ocurrTime=((double)(event.getTimeStamp() - logModel.getBirthTime())/1000)+"s";
                }
                logModel.setBirthTime(event.getTimeStamp());
                StringBuilder stackTraceMsg = new StringBuilder();
                String errorConet = String.format("所在主机地址: %s(%s) \n已发生次数: %s\n距上次发生时间: %s \n%s %s --- %s : %s",
                        HOST_IP,
                        HOST_NAME,
                        logModel.getOccurCount(),
                        ocurrTime,
                        dateFormat.format(new Date(event.getTimeStamp())),
                        event.getLevel().levelStr,
                        event.getLoggerName(),
                        event.getFormattedMessage());
                stackTraceMsg.append(errorConet);
                stackTraceMsg.append("\n");
                IThrowableProxy iThrowableProxy = event.getThrowableProxy();
                if (iThrowableProxy != null) {
                    stackTraceMsg.append(iThrowableProxy.getClassName());
                    stackTraceMsg.append(" : ");
                    stackTraceMsg.append(iThrowableProxy.getMessage());
                    stackTraceMsg.append("\n");

                    for (StackTraceElementProxy elementProxy : iThrowableProxy.getStackTraceElementProxyArray()) {
                        String className = elementProxy.getStackTraceElement().getClassName();
                        if (!StringUtils.isBlank(className) && className.startsWith("com.omen")) {
                            stackTraceMsg.append("    " + elementProxy.getSTEAsString());
                        }
                    }
                }
                //TODO 钉钉响应告警
                System.out.println(stackTraceMsg.toString());


            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    private synchronized static LogModel getLogModel(String msg) {
        LogModel model = LOG_MODEL_MAP.get(msg);
        if (model == null) {
            model = new LogModel(0, 0, 0);
            LOG_MODEL_MAP.put(msg, model);
        }
        long occurCount = model.getOccurCount();
        model.setOccurCount(occurCount + 1);
        return model;
    }
}
