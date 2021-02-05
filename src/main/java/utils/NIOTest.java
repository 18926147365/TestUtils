package utils;

import lombok.SneakyThrows;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.util.concurrent.*;

/**
 * @Description:
 * @Author: lihaoming
 * @Date: 2021/1/28 下午3:11
 */
public class NIOTest {

    private static volatile Selector selector;

    public NIOTest() {
        try {
            ServerSocketChannel channel = ServerSocketChannel.open();
            channel.configureBlocking(false);
            ServerSocket serverSocket = channel.socket();
            serverSocket.bind(new InetSocketAddress(8123));
            selector = Selector.open();
            SelectionKey selectionKey = channel.register(selector, SelectionKey.OP_ACCEPT);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void listen() throws IOException {
        while (true) {
            int event = selector.select();
            if (event == SelectionKey.OP_READ) {

            }


        }

    }
    static ExecutorService pool = new ThreadPoolExecutor(2, 2, 500,
            TimeUnit.MILLISECONDS,
            new LinkedBlockingQueue<>(3),
            Executors.defaultThreadFactory(),
            new RejectedExecutionHandler() {
                @SneakyThrows
                @Override
                public void rejectedExecution(Runnable r, ThreadPoolExecutor executor) {
                    int relaseMax = 5;//该值不允许比队列ArrayBlockingQueue初始长度值大，值大小建议:初始长度值/2
                    long relaseMillis = 300;//线程休眠时间
                    while (executor.getQueue().size() >= relaseMax) {
                        Thread.sleep(relaseMillis);
                    }
                    if (!executor.isShutdown()) {
                        r.run();
                    }
                }
            });


    public static void main(String[] args) throws Exception {
        for (int i = 0; i < 1000; i++) {
            pool.submit(new Runnable() {
                @Override
                public void run() {
                    try {
                        Thread.sleep(10000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            });
        }

    }
}
