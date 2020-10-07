package cn.bridgeli.netty.demo.server.login;

import cn.bridgeli.netty.demo.model.CurrentSource;
import cn.bridgeli.netty.demo.model.Message;
import cn.bridgeli.netty.demo.util.Byte2StringUtil;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Component;

import java.util.HashMap;

/**
 * @author bridgeli
 */
@Component
@PropertySource("classpath:/login.properties")
public class ConnectionManager {
    private static Logger LOGGER = LoggerFactory.getLogger(ConnectionManager.class);
    @Value("${user.list}")
    private String userList;
    @Value("${conn.whitelist}")
    private String whitelist;
    @Value("${conn.ipfilter}")
    private boolean ipfilter;

    private static HashMap<String, String> userMap = null;

    public void login(Message msg, ChannelHandlerContext ctx) {
        ByteBuf body = msg.getMsgBody();
        int userId = body.readInt();
        String password = Byte2StringUtil.getGbkString(body.readBytes(8));
        String ip = Byte2StringUtil.getGbkString(body.readBytes(32));
        if (!filterIp(ip) || !accessLogin(userId, password)) {
            ctx.close();
            return;
        }
        if (CurrentSource.currentResouce < 1) {
            CurrentSource.currentResouce = userId;
            CurrentSource.remoteAddress = ctx.channel().remoteAddress();
        } else if (userId != CurrentSource.currentResouce && !CurrentSource.remoteAddress.toString().contains(ip)) {
            LOGGER.error("登陆失败,已有链接:{},拒绝连接:userId:{},password:{},ip:{},port:{}", CurrentSource.currentResouce,
                    CurrentSource.remoteAddress, userId, password, ip);
            ctx.close();
            return;
        }
        int port = body.readUnsignedShort();
        LOGGER.info("登陆请求,userId:{},password:{},ip:{},port:{}", userId, password, ip, port);
        Message msgRep = new Message(0x1002);
        ByteBuf buffer = Unpooled.buffer(5);
        buffer.writeByte(0x00);
        buffer.writeInt(1111);
        msgRep.setMsgBody(buffer);
        ctx.channel().writeAndFlush(buildMessage(msgRep));
    }

    private boolean filterIp(String ip) {
        if (!ipfilter) {
            return true;
        }
        if (null == ip || !whitelist.contains(ip.trim())) {
            LOGGER.warn("登陆ip:{}不在白名单:{}列表中", ip, whitelist);
            return false;
        }
        return true;
    }

    private boolean accessLogin(int userId, String password) {

        if (null == userMap) {
            loadUserMap();
        }
        String p = userMap.get(userId + "");
        if (null != p && (p + "").equals(password)) {
            LOGGER.info("userId:{}登陆成功...", userId);
            return true;
        }
        LOGGER.warn("登陆失败,password:{}", password);
        return false;
    }

    private void loadUserMap() {
        String[] userArr = userList.split(",");
        if (userArr.length > 1) {
            userMap = new HashMap<>(userArr.length / 3 * 4);
            for (int i = 0; i < userArr.length; i++) {
                String user = userArr[i];
                int index = user.lastIndexOf("@#");
                String userId0 = user.substring(0, index);
                String password0 = user.substring(index + 2);
                userMap.put(userId0, password0);
            }
        }
    }

    /**
     * 生成下行报文
     *
     * @param msg
     * @return
     */
    private ByteBuf buildMessage(Message msg) {
        ByteBuf buffer = Unpooled.buffer(Message.MSG_FIX_LENGTH);
        buffer.writeBytes(msg.getMsgBody());
        return buffer;
    }

    public void heartBeat(ChannelHandlerContext ctx) {
        Message msgRep = new Message(0x1006);
        ByteBuf buffer = Unpooled.buffer(0);
        msgRep.setMsgBody(buffer);
        ctx.channel().writeAndFlush(buildMessage(msgRep));
    }

}