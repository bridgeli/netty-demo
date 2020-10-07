package cn.bridgeli.netty.demo.codec;

import cn.bridgeli.netty.demo.model.Message;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToMessageDecoder;

import java.util.List;

/**
 * @author bridgeli
 */
public class Decoder extends MessageToMessageDecoder<ByteBuf> {
    // 头标示1+长度标识4

    @Override
    protected void decode(ChannelHandlerContext ctx, ByteBuf msg, List<Object> out) throws Exception {

        Message message = buildMessage(msg);
        if (null != message) {
            out.add(message);
        }
    }

    private Message buildMessage(ByteBuf buffer) {
        Message msg = new Message();
        msg.setMsgId(buffer.readUnsignedShort());
        return msg;
    }
}
