package cn.bridgeli.netty.demo.handler;

import cn.bridgeli.netty.demo.codec.Decoder;
import cn.bridgeli.netty.demo.codec.DelimiterBasedFrameDecoder;
import cn.bridgeli.netty.demo.codec.Encoder;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.timeout.ReadTimeoutHandler;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

/**
 * @author bridgeli
 */
@Component
@Qualifier("somethingChannelInitializer")
public class SomethingChannelInitializer extends ChannelInitializer<SocketChannel> {
    @Resource(name = "somethingServerHandler")
    private ChannelInboundHandlerAdapter somethingServerHandler;

    @Value("${connection.readTimeout}")
    private int readTimeout;
    @Value("${rcvbuf.maximum}")
    private int maxFrameLength;

    @Override
    protected void initChannel(SocketChannel socketChannel) throws Exception {
        ChannelPipeline pipeline = socketChannel.pipeline();
        pipeline.addLast(new ReadTimeoutHandler(readTimeout));
        byte[] array = {0x5d};
        ByteBuf delimiter = Unpooled.copiedBuffer(array);
        pipeline.addLast(new DelimiterBasedFrameDecoder(maxFrameLength, false, delimiter));
        pipeline.addLast(new Decoder());
        pipeline.addLast(new Encoder());
        pipeline.addLast(somethingServerHandler);
    }

}
