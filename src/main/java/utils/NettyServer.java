package utils;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.string.StringDecoder;
import io.netty.handler.codec.string.StringEncoder;

/**
 * @Description:
 * @Author: lihaoming
 * @Date: 2020/10/12 2:25 下午
 */
public class NettyServer {
    public static void main(String[] args) {
        NioEventLoopGroup boss=new NioEventLoopGroup(1);
        NioEventLoopGroup worker=new NioEventLoopGroup(3);
        try {
            final ServerBootstrap server=new ServerBootstrap();
            server.group(boss,worker).channel(NioServerSocketChannel.class)
                    .childHandler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        protected void initChannel(SocketChannel ch) throws Exception {
                            ChannelPipeline pipeline = ch.pipeline();
                            pipeline.addLast(new NettyEncoder());
                            pipeline.addLast(new NettyDecoder());
                            pipeline.addLast(new NettyServerHandler());
                        }
                    });
            ChannelFuture future = server.bind(9000).sync();
            future.channel().closeFuture().sync();
        }catch (Exception e){
            e.printStackTrace();
        }
        finally {
            boss.shutdownGracefully();
            worker.shutdownGracefully();
        }
    }
}
