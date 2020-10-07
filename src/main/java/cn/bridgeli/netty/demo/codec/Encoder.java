package cn.bridgeli.netty.demo.codec;

import cn.bridgeli.netty.demo.model.Message;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToMessageEncoder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

public class Encoder extends MessageToMessageEncoder<Message> {
    private static Logger LOGGER = LoggerFactory.getLogger(Encoder.class);

    @Override
    protected void encode(ChannelHandlerContext ctx, Message msg, List<Object> out) throws Exception {
        ByteBuf buf = buildMessage(msg);
        out.add(buf);
    }

    /**
     * 生成下行报文
     *
     * @param msg
     * @return
     */
    private ByteBuf buildMessage(Message msg) {
        int bodyLength = msg.getMsgBody().capacity();
        ByteBuf buffer = Unpooled.buffer(bodyLength + Message.MSG_FIX_LENGTH);
        buffer.writeByte(Message.MSG_HEAD);
        // --------------数据头----------
        buffer.writeInt(buffer.capacity());
        // --------------数据体----------
        buffer.writeBytes(msg.getMsgBody());
        return buffer;
    }

}
