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
import javassist.ClassPool;
import javassist.CtClass;
import javassist.CtMethod;
import javassist.LoaderClassPath;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Marker;

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
    private static final String filterPackage="com.omen";
    private static final Map<String, LogModel> LOG_MODEL_MAP = new ConcurrentHashMap<>();

    static {
        try {
            InetAddress address = InetAddress.getLocalHost();
            NetworkInterface ni = NetworkInterface.getByInetAddress(address);
            ni.getInetAddresses().nextElement().getAddress();
            HOST_IP = findPrivateHosts(false).toString();
            HOST_NAME = initHostname();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 根据域名获取到对应的ip
     * @param host  域名或者ip
     * @return
     */
    public static String resolve(String host) {
        try {
            return InetAddress.getByName(host).getHostAddress();
        } catch (UnknownHostException e) {
            throw new IllegalStateException("解析失败" + host, e);
        }
    }
    public static long ipToLong(InetAddress ip) {
        byte[] octets = ip.getAddress();
        long result = 0;
        for (byte octet : octets) {
            result <<= 8;
            result |= octet & 0xff;
        }
        return result;
    }

    public static long ipToLong(String ip) {
        InetAddress ipAddr = InetAddresses.forString(ip);
        return ipToLong(ipAddr);
    }


    /**
     * 私有IP范围: 10.0.0.0 - 10.255.255.255, 172.16.0.0 - 172.31.255.255, 192.168.0.0 - 192.168.255.255
     * 判断一个ip是否在某段范围
     *
     * @param current 给定的ip
     * @param from 范围起始地址
     * @param to 范围结束地址
     * @return
     */
    public static boolean range(String current, String from, String to) {
        long fromIp = ipToLong(from);
        long toIp = ipToLong(to);
        long currentIp = ipToLong(current);
        return fromIp <= currentIp && currentIp <= toIp;
    }
    /**
     * 判断给定的ip是否是内网ip
     *
     * @param host
     * @return 是就返回true, 反则false
     */
    public static boolean isPrivateIp(String host) {
        String ip = resolve(host);
        try {
            return range(ip, "10.0.0.0", "10.255.255.255") || range(ip, "172.16.0.0", "172.31.255.255")
                    || range(ip, "192.168.0.0", "192.168.255.255");
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * 获取内网ip
     *
     * @param includeLoopback 是否包含127.0.0.1
     * @return 内网ip集合
     */
    public static Collection<String> findPrivateHosts(boolean includeLoopback) {
        List<String> ips = Lists.newArrayListWithExpectedSize(4);
        try {
            Enumeration<NetworkInterface> interfaces = NetworkInterface.getNetworkInterfaces();
            while (interfaces.hasMoreElements()) {
                Enumeration<InetAddress> addresses = interfaces.nextElement().getInetAddresses();
                while (addresses.hasMoreElements()) {
                    String address = addresses.nextElement().getHostAddress();
                    if (isPrivateIp(address)) {
                        ips.add(address);
                    } else if ("127.0.0.1".equals(address) && includeLoopback) {
                        ips.add(address);
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return ips;
    }


    private static String initHostname() {
        String hostname;
        try {
            hostname = InetAddress.getLocalHost().getHostName();
        } catch (UnknownHostException e) {
            hostname = "UNKNOWN";
            e.printStackTrace();
        }
        return hostname;
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
                        if (!StringUtils.isBlank(className) && className.startsWith(filterPackage)) {
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
