package utils;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;

/**
 * @Description:
 * @Author: lihaoming
 * @Date: 2020/11/11 9:57 上午
 */
public class NettyEncoder extends MessageToByteEncoder<NettyProtocol> {

    @Override
    protected void encode(ChannelHandlerContext ctx, NettyProtocol msg, ByteBuf out) throws Exception {
        System.out.println("编码");
        out.writeInt(msg.getLength());
        out.writeBytes(msg.getContent());
    }


}
