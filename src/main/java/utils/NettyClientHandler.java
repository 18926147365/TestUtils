package utils;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerAdapter;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;

/**
 * @Description:
 * @Author: lihaoming
 * @Date: 2020/10/13 3:04 下午
 */
public class NettyClientHandler extends ChannelInboundHandlerAdapter implements ChannelHandler {

    public NettyClientHandler() {


    }

    @Override
    public void channelRegistered(ChannelHandlerContext ctx) throws Exception {
        //
        String channelId = ctx.channel().id().asLongText();
        System.out.println("Registered    channelId:"+channelId);
        super.channelRegistered(ctx);
    }

    @Override
    public void channelUnregistered(ChannelHandlerContext ctx) throws Exception {
        //
        String channelId = ctx.channel().id().asLongText();
        System.out.println("Unregistered  channelId:"+channelId);
        super.channelUnregistered(ctx);
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        //与服务端建立连接后
        String channelId = ctx.channel().id().asLongText();
        System.out.println("客户端连接成功 服务端channelId:"+channelId);
        byte[] req = "客户端请求数据".getBytes();
        ByteBuf msg= ctx.alloc().buffer(1024);
        msg.writeBytes(req);
        ctx.writeAndFlush(msg);
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg)
            throws Exception {
        System.out.println("client channelRead..");
        //服务端返回消息后
        ByteBuf buf = (ByteBuf) msg;
        byte[] req = new byte[buf.readableBytes()];
        buf.readBytes(req);
        String body = new String(req, "UTF-8");
        System.out.println("Now is :" + body);
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause)
            throws Exception {
        System.out.println("client exceptionCaught..");
        ctx.close();
    }
}
