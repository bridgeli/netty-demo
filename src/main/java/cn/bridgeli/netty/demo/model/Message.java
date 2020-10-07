package cn.bridgeli.netty.demo.model;

import io.netty.buffer.ByteBuf;

/**
 * @author bridgeli
 */
public class Message {
    public static final int MSG_HEAD = 0x5b;

    // 报文中除数据体外，固定的数据长度
    public static final int MSG_FIX_LENGTH = 26;

    // 报文序列号，自增。
    private static int internalMsgNo = 0;
    private int msgId;
    private ByteBuf msgBody;
    // 下行报文标识，值为1时，代表发送的数据；默认为0，代表接收的报文
    // private int downFlag = 0;

    public Message() {
    }

    public Message(int msgId) {
        // 下行报文需要填充报文序列号
        synchronized ((Integer) internalMsgNo) {
            if (internalMsgNo == Integer.MAX_VALUE) {
                internalMsgNo = 0;
            }
        }
        this.msgId = msgId;
    }

    public int getMsgId() {
        return msgId;
    }

    public void setMsgId(int msgId) {
        this.msgId = msgId;
    }

    public ByteBuf getMsgBody() {
        return msgBody;
    }

    public void setMsgBody(ByteBuf msgBody) {
        this.msgBody = msgBody;
    }

}
