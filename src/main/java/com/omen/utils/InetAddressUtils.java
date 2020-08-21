package com.omen.utils;

import com.google.common.collect.Lists;
import com.google.common.net.InetAddresses;

import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.UnknownHostException;
import java.util.Collection;
import java.util.Enumeration;
import java.util.List;

/**
 * @author 李浩铭
 * @date 2020/8/17 8:51
 * @descroption
 */
public class InetAddressUtils {

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


    public  static String getHostname() {
        String hostname;
        try {
            hostname = InetAddress.getLocalHost().getHostName();
        } catch (UnknownHostException e) {
            hostname = "UNKNOWN";
            e.printStackTrace();
        }
        return hostname;
    }

}
