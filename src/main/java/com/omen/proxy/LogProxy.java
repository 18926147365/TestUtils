package com.omen.proxy;

import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.classic.spi.IThrowableProxy;
import ch.qos.logback.classic.spi.StackTraceElementProxy;
import com.google.common.base.Throwables;
import com.google.common.collect.Lists;
import com.google.common.hash.BloomFilter;
import com.google.common.hash.Funnels;
import com.google.common.net.InetAddresses;
import com.omen.bean.LogModel;
import com.omen.dingtalk.DingMarkDown;
import com.omen.dingtalk.DingTalkSend;
import com.omen.dingtalk.DingText;
import com.omen.utils.InetAddressUtils;
import javassist.ClassPool;
import javassist.CtClass;
import javassist.CtMethod;
import javassist.LoaderClassPath;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Marker;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.view.tiles3.SpringBeanPreparerFactory;

import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.UnknownHostException;
import java.nio.charset.Charset;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author 李浩铭
 * @date 2020/8/14 11:48
 * @descroption
 */
public class LogProxy {



    private static final String applicationName=System.getProperty("app.name");
    private static SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
    private static final String HOST_NAME = InetAddressUtils.getHostname();//主机名称
    private static final String HOST_IP = InetAddressUtils.findPrivateHosts(false).toString();//主机IP
    private static final String filterPackage = "com.omen";
    private static final Map<String, LogModel> LOG_MODEL_MAP = new ConcurrentHashMap<>();

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



    public static void errorEvent(ILoggingEvent event) {
        try {
            if (event == null || !"ERROR".equals(event.getLevel().levelStr.toUpperCase())) {
                return;
            }
            String loggName = event.getLoggerName();
            if (!StringUtils.isBlank(loggName) && loggName.startsWith(filterPackage)) {
                //错误日志扩展
                LogModel logModel = getLogModel(event.getMessage());

                String ocurrTime = "首次发生";

                if (logModel.getBirthTime() != 0) {
                    ocurrTime = ((double) (event.getTimeStamp() - logModel.getBirthTime()) / 1000) + "s";
                }
                logModel.setBirthTime(event.getTimeStamp());

                String errorConet = String.format("项目名称: %s\n所在主机地址: %s(%s) \n已发生次数: %s ( 距上次发生时间:%s ) \n%s %s --- %s : %s",
                        applicationName,
                        HOST_IP,
                        HOST_NAME,
                        logModel.getOccurCount(),
                        ocurrTime,
                        dateFormat.format(new Date(event.getTimeStamp())),
                        event.getLevel().levelStr,
                        event.getLoggerName(),
                        event.getFormattedMessage());

                StringBuilder stackTraceMsg = new StringBuilder();
                IThrowableProxy iThrowableProxy = event.getThrowableProxy();
                if (iThrowableProxy != null) {
                    stackTraceMsg.append(iThrowableProxy.getClassName());
                    stackTraceMsg.append(" : ");
                    stackTraceMsg.append(iThrowableProxy.getMessage());
                    stackTraceMsg.append("\n");
                    for (StackTraceElementProxy elementProxy : iThrowableProxy.getStackTraceElementProxyArray()) {
                        String className = elementProxy.getStackTraceElement().getClassName();
                        if (!StringUtils.isBlank(className) && className.startsWith(filterPackage) ) {
                            stackTraceMsg.append("  "+elementProxy.getSTEAsString());
                        }
                    }
                }



                //钉钉响应告警
                DingMarkDown markDown=new DingMarkDown("日志告警","错误日志告警\n\n");
//
//                DingTalkSend dingTalkSend=new DingTalkSend(markDown);
//                dingTalkSend.setAccessToken("3fc73a1eba534bfe8cbbccc67b5d77dfbbb7ba752bbc7d5842d73149f5653952");
//                System.out.println(dingTalkSend.sendSuccess());

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
