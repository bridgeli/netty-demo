package cn.bridgeli.netty.demo.kafka;

import kafka.javaapi.producer.Producer;
import kafka.producer.KeyedMessage;
import kafka.producer.ProducerConfig;
import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

/**
 * @author bridgeli
 */
@Component
public class MsgHandler {
    private static Properties properties = new Properties();
    private static ProducerConfig producerConfig;
    private static Logger LOGGER = LoggerFactory.getLogger(MsgHandler.class);
    private Producer<String, String> producer;
    @Value("${kafka.topic}")
    private String topic;
    @Value("${kafka.isAvailable}")
    private boolean isAvailable;

    public void send(String msg) {
        if (!isAvailable) {
            return;
        }
        String key = DateTime.now().toString("yyMMddHHmmss");
        KeyedMessage<String, String> message = new KeyedMessage<String, String>(topic, key, msg);
        producer.send(message);
    }

    @PostConstruct
    public void init() {
        LOGGER.info("init kafka config...");
        try (InputStream in = this.getClass().getResourceAsStream("/kafka.properties")) {

            properties.load(in);
            if (Boolean.FALSE.toString().equals(properties.getProperty("kafka.isAvailable"))) {
                LOGGER.warn("init kafka is skipped because the properties \"kafka.isAvailable\" is false");
                return;
            }
            producerConfig = new ProducerConfig(properties);
        } catch (IOException e) {
            e.printStackTrace();
        }
        producer = new Producer<>(producerConfig);
    }
}
