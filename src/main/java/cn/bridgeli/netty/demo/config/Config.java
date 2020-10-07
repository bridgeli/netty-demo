package cn.bridgeli.netty.demo.config;

import cn.bridgeli.netty.demo.handler.SomethingChannelInitializer;
import cn.bridgeli.netty.demo.server.netty.ChannelRepository;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.AdaptiveRecvByteBufAllocator;
import io.netty.channel.ChannelOption;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.annotation.Resource;
import java.net.InetSocketAddress;

/**
 * @author bridgeli
 */
@Configuration
public class Config {

    @Value("${conn.port}")
    private int tcpPort;

    @Value("${boss.thread.count}")
    private int bossCount;

    @Value("${worker.thread.count}")
    private int workerCount;

    @Value("${so.keepalive}")
    private boolean keepAlive;

    @Value("${so.backlog}")
    private int backlog;

    @Value("${rcvbuf.minimum}")
    private int minimum;

    @Value("${rcvbuf.initial}")
    private int initial;

    @Value("${rcvbuf.maximum}")
    private int maximum;

    @Bean(name = "serverBootstrap")
    public ServerBootstrap bootstrap() {
        ServerBootstrap b = new ServerBootstrap();
        b.group(bossGroup(), workerGroup()).channel(NioServerSocketChannel.class);
        b.handler(new LoggingHandler(LogLevel.DEBUG)).childHandler(somethingChannelInitializer);
        b.option(ChannelOption.SO_KEEPALIVE, keepAlive);
        b.option(ChannelOption.SO_BACKLOG, backlog);
        // 设置传输字节大小
        b.option(ChannelOption.RCVBUF_ALLOCATOR, new AdaptiveRecvByteBufAllocator(minimum, initial, maximum));
        return b;
    }

    @Resource(name = "somethingChannelInitializer")
    private SomethingChannelInitializer somethingChannelInitializer;

    @Bean(name = "bossGroup", destroyMethod = "shutdownGracefully")
    public NioEventLoopGroup bossGroup() {
        return new NioEventLoopGroup(bossCount);
    }

    @Bean(name = "workerGroup", destroyMethod = "shutdownGracefully")
    public NioEventLoopGroup workerGroup() {
        return new NioEventLoopGroup(workerCount);
    }

    @Bean(name = "tcpSocketAddress")
    public InetSocketAddress tcpPort() {
        return new InetSocketAddress(tcpPort);
    }

    @Bean(name = "channelRepository")
    public ChannelRepository channelRepository() {
        return new ChannelRepository();
    }

}