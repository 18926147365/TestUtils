package com.omen.proxy;

import ch.qos.logback.classic.spi.ILoggingEvent;
import javassist.ClassPool;
import javassist.CtClass;
import javassist.CtMethod;
import javassist.LoaderClassPath;
import org.apache.commons.lang.StringUtils;

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
            CtClass[] ctClasses=new CtClass[1];
            ctClasses[0]=pool.get("ch.qos.logback.classic.spi.ILoggingEvent");
            CtMethod cm = cc.getDeclaredMethod("callAppenders",ctClasses);
            cm.insertBefore("com.omen.proxy.LogProxy.errorEvent(event);");
            cc.toClass();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public static void errorEvent(ILoggingEvent event){
        try {
            if(event==null || !"ERROR".equals(event.getLevel().levelStr.toUpperCase())){
                return;
            }
            String loggName=event.getLoggerName();
            if(!StringUtils.isBlank(loggName) && loggName.startsWith("com.omen")){
                //错误日志扩展


            }


        } catch (Exception e) {
            e.printStackTrace();
        }

    }
}
