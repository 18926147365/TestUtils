package utils;

import bean.NettyMsg;
import com.alibaba.fastjson.JSONObject;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.*;
import io.netty.handler.timeout.IdleState;
import io.netty.handler.timeout.IdleStateEvent;

/**
 * @Description:
 * @Author: lihaoming
 * @Date: 2020/10/13 3:04 下午
 */
public class NettyClientHandler extends ChannelInboundHandlerAdapter implements ChannelHandler {
    public NettyClientHandler() {


    }
    //当服务器5秒内没有发生读的事件时，会触发这个事件
    @Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
        if (evt instanceof IdleStateEvent) {
            IdleState state = ((IdleStateEvent) evt).state();
            if (state == IdleState.READER_IDLE) { //当事件为读事件触发时发生异常，或者中断
                throw new Exception("idle exception");//将通道进行关闭
            }
        }else {
            super.userEventTriggered(ctx, evt);
        }
    }

    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        //与服务端建立连接后
        System.out.println("client channelActive..");
        byte[] req = "客户端请求数据".getBytes();
        ByteBuf msg= ctx.alloc().buffer(1024);
        msg.writeBytes(req);
        ctx.writeAndFlush(msg);
    }

    public void channelRead(ChannelHandlerContext ctx, Object msg)
            throws Exception {
        //服务端返回消息后
        ByteBuf buf = (ByteBuf) msg;
        byte[] req = new byte[buf.readableBytes()];
        buf.readBytes(req);
        String body = new String(req, "UTF-8");
        //解密
        String msgSign = RSAUtils.decryptByPrivateKey(body,RSAUtils.privateKey);
        NettyMsg nettyMsg = JSONObject.parseObject(msgSign,NettyMsg.class);
        if(nettyMsg.getMsgType().equals(NettyMsg.NOTIFY)){
            JSONObject msgJson = JSONObject.parseObject(nettyMsg.getMsg());
            String title = msgJson.getString("title");
            String title1 = msgJson.getString("title1");
            String title2 = msgJson.getString("title2");
            Runtime.getRuntime().exec("sh /Users/lihaoming/data/shell/notify.sh "+title+" " + title1 + " " + title2);
//            Runtime.getRuntime().exec("sh /Users/lihaoming/data/shell/notify.sh "+title+" " + tipBuilder + " " + downUpTip);
        }


        System.out.println("Now is :" + body);
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause)
            throws Exception {
        System.out.println("client exceptionCaught..");

        ctx.close();
    }


}
