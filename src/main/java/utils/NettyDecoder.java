package utils;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;
import io.netty.handler.codec.MessageToMessageDecoder;

import java.util.List;

/**
 * @Description:
 * @Author: lihaoming
 * @Date: 2020/11/11 9:57 上午
 */
public class NettyDecoder extends ByteToMessageDecoder {


    int length = 0;
    @Override
    protected void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) throws Exception {
        if(in.readableBytes() >= 4) {
            if (length == 0){
                length = in.readInt();
            }
            if (in.readableBytes() < length) {
                System.out.println("当前可读数据不够，继续等待。。");
                return;
            }
            byte[] content = new byte[length];
            if (in.readableBytes() >= length){
                in.readBytes(content);
                //封装成MyMessageProtocol对象，传递到下一个handler业务处理
                NettyProtocol messageProtocol = new NettyProtocol();
                messageProtocol.setLength(length);
                messageProtocol.setContent(content);
                out.add(messageProtocol);
            }
            length = 0;
        }
    }
}
