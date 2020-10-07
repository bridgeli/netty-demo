package cn.bridgeli.netty.demo.handler;

import cn.bridgeli.netty.demo.kafka.MsgHandler;
import cn.bridgeli.netty.demo.model.GpsData;
import cn.bridgeli.netty.demo.model.Message;
import cn.bridgeli.netty.demo.server.login.ConnectionManager;
import cn.bridgeli.netty.demo.server.netty.ChannelRepository;
import cn.bridgeli.netty.demo.util.Byte2StringUtil;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

/**
 * @author bridgeli
 */
@Component
@Qualifier("somethingServerHandler")
@ChannelHandler.Sharable
public class SomethingServerHandler extends ChannelInboundHandlerAdapter {
    @Autowired
    private ChannelRepository channelRepository;
    @Autowired
    private MsgHandler msgHandler;
    @Autowired
    private ConnectionManager connectionManager;
    private static Logger LOGGER = LoggerFactory.getLogger(SomethingServerHandler.class);

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        ctx.fireChannelActive();
        String channelKey = ctx.channel().remoteAddress().toString();
        if (channelKey.startsWith("/192.")) {
            LOGGER.warn("ip:{} is not allowed connection...", channelKey);
            ctx.close();
        }
        channelRepository.put(channelKey, ctx.channel());
        ctx.writeAndFlush("ipdetail:" + channelKey + "\n\r");
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg0) throws Exception {
        Message msg = null;
        try {
            msg = (Message) msg0;
        } catch (Exception e) {
            LOGGER.error("[channelRead],case Message fail :{}", e);
            return;
        }
        switch (msg.getMsgId()) {
            case 0x1001:
                connectionManager.login(msg, ctx);
                break;
            case 0x1005:
                connectionManager.heartBeat(ctx);
                break;
            case 0x1200:
                dataExchange(ctx, msg);
                break;
            default:
                LOGGER.warn("未case到的请求消息id:{}({})", msg.getMsgId(), String.format("0x%04x", msg.getMsgId()));
                break;
        }
        msg.getMsgBody().clear();
    }

    private void dataExchange(ChannelHandlerContext ctx, Message msg) {
        ByteBuf buf = msg.getMsgBody();
        String plate = Byte2StringUtil.getGbkString(buf.readBytes(21));

        GpsData heartData = new GpsData(plate);

        buf.clear();
        msgHandler.send(heartData.toString());

    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        String msg = cause.getMessage();
        if ("Connection reset by peer".equals(msg)) {
            LOGGER.error("Connection reset by peer,ctx:{},cause:", ctx, cause);
            ctx.close();
            return;
        }
        LOGGER.error("[exceptionCaught]:{}", cause.getMessage());
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) {
        String channelKey = ctx.channel().remoteAddress().toString();
        this.channelRepository.remove(channelKey);
    }

}
