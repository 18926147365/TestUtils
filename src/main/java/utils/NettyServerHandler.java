package utils;

import com.taobao.api.internal.toplink.channel.ChannelException;
import com.taobao.api.internal.toplink.channel.netty.NettyChannelSender;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.channel.SimpleChannelInboundHandler;
import org.apache.commons.io.IOUtils;

import java.nio.ByteBuffer;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @Description:
 * @Author: lihaoming
 * @Date: 2020/10/12 2:27 下午
 */
public class NettyServerHandler extends SimpleChannelInboundHandler<NettyProtocol> {



    public static  Map<String,ChannelHandlerContext> nettyClientMap=new ConcurrentHashMap<>();

    public static void sendClientMsg(){
        for (String channelId : nettyClientMap.keySet()) {
          ChannelHandlerContext ctx= nettyClientMap.get(channelId);
            ByteBuf newbuf=ctx.alloc().buffer(1024);
            byte[] resp = "服务器响应值111".getBytes();
            newbuf.writeBytes(resp);
            ctx.writeAndFlush(newbuf);
        }
    }

//    @Override
//    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {//msg 其实就是个ByteBuf 对象
//
//        System.out.println("接收到的消息："+msg);
////        ByteBuf buf=(ByteBuf) msg;
////
////        byte[] req = new byte[buf.readableBytes()];
////        buf.readBytes(req);
////        String body = new String(req, "UTF-8");
////        System.out.println("接收到的消息："+body);
////
////        ByteBuf newbuf=ctx.alloc().buffer(1024);
////        byte[] resp = "服务器响应值111".getBytes();
////        newbuf.writeBytes(resp);
////        ctx.writeAndFlush(newbuf);
////        buf.release();
//    }

    @Override
    protected void channelRead0(ChannelHandlerContext channelHandlerContext, NettyProtocol str) throws Exception {
        System.out.println("----------------服务端接收到的消息："+new String(str.getContent(),"UTF-8"));
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        System.out.println("channel active");
    }

    @Override
    public void handlerAdded(ChannelHandlerContext ctx) throws Exception {
        String channelId = ctx.channel().id().asLongText();
        System.out.println("客户端进来，channelId为：" + channelId);
        nettyClientMap.put(channelId,ctx);
    }

    @Override
    public void handlerRemoved(ChannelHandlerContext ctx) throws Exception {
        String channelId = ctx.channel().id().asLongText();
        System.out.println("客户端被移除，channelId为：" + channelId);
        if(nettyClientMap.containsKey(channelId)){
            nettyClientMap.remove(channelId);
        }
    }
    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        cause.printStackTrace();
        ctx.close();
    }
}