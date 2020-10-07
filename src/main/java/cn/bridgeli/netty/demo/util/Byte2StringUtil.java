package cn.bridgeli.netty.demo.util;

import io.netty.buffer.ByteBuf;

import java.nio.charset.Charset;

/**
 * @author bridgeli
 */
public class Byte2StringUtil {
    public final static Charset charset = Charset.forName("GBK");

    /**
     * 将netty的字节流转换为字符串
     *
     * @param buff
     * @return String
     */
    public static String getGbkString(ByteBuf buff) {
        return getGbkString(buff, charset);
    }

    /**
     * 将netty的字节流转换为指定编码的字符串
     *
     * @param buff
     * @param charset
     * @return String
     */
    public static String getGbkString(ByteBuf buff, Charset charset) {
        if (null == buff) {
            return null;
        }
        return buff.toString(charset).trim();
    }
}
