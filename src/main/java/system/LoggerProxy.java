package system;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

/**
 * @author 李浩铭
 * @date 2020/7/17 14:02
 * @descroption
 */
public class LoggerProxy implements InvocationHandler {

    private Logger logger;

    public LoggerProxy(Class<?> clazz) {
        logger = LoggerFactory.getLogger(clazz);
    }

    public static Logger getLogger(Class<?> clazz) {
        LoggerProxy proxy = new LoggerProxy(clazz);
        return (Logger) Proxy.newProxyInstance(proxy.getClass().getClassLoader(), proxy.logger.getClass().getInterfaces(), proxy);
    }


    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        Object invoke = method.invoke(logger, args);
        return invoke;
    }

}
