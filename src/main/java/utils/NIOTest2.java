package utils;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;

/**
 * @Description:
 * @Author: lihaoming
 * @Date: 2021/1/28 下午3:11
 */
public class NIOTest2 {

    public static void main(String[] args) throws Exception {
        SocketChannel channel = SocketChannel.open();
        Socket socket = channel.socket();
        socket.bind(new InetSocketAddress(8034));
        socket.connect(new InetSocketAddress(8033));


    }
}
