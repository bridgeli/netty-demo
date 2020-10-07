package cn.bridgeli.netty.demo;

import cn.bridgeli.netty.demo.server.netty.TCPServer;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.PropertySource;

/**
 * @author bridgeli
 */
@SpringBootApplication
@PropertySource(value = {"classpath:/nettyserver.properties", "classpath:/kafka.properties"})
public class Application {

    public static void main(String[] args) throws Exception {
        ConfigurableApplicationContext context = SpringApplication.run(Application.class, args);
        TCPServer tcpServer = context.getBean(TCPServer.class);
        tcpServer.start();
    }
}