package utils;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @Description:
 * @Author: lihaoming
 * @Date: 2020/10/12 2:27 下午
 */
public class NettyServerHandler extends ChannelInboundHandlerAdapter {

    public static Map<String,ChannelHandlerContext> contextMap = new ConcurrentHashMap<>();

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {//msg 其实就是个ByteBuf 对象
        ByteBuf buf=(ByteBuf) msg;

        byte[] req = new byte[buf.readableBytes()];
        buf.readBytes(req);
        String body = new String(req, "UTF-8");
        System.out.println("接收到的消息："+body);

//        ByteBuf newbuf=ctx.alloc().buffer(1024);
//        byte[] resp = "服务器响应值111".getBytes();
//        newbuf.writeBytes(resp);
//        ctx.writeAndFlush(newbuf);
//        buf.release();
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        System.out.println("channel active");

    }

    @Override
    public void handlerAdded(ChannelHandlerContext ctx) throws Exception {
        String channelId = ctx.channel().id().asLongText();
        System.out.println("客户端进来，channelId为：" + channelId);
        contextMap.put(channelId,ctx);
    }

    @Override
    public void handlerRemoved(ChannelHandlerContext ctx) throws Exception {
        String channelId = ctx.channel().id().asLongText();
        System.out.println("客户端被移除，channelId为：" + channelId);
        contextMap.remove(channelId);
    }
    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        cause.printStackTrace();
        ctx.close();
    }
}