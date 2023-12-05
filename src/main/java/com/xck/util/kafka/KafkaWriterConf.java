package com.xck.util.kafka;

import lombok.Getter;
import lombok.Setter;
import org.apache.commons.lang3.StringUtils;
import org.apache.kafka.clients.producer.ProducerConfig;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

@Setter
@Getter
public class KafkaWriterConf {

    /**
     * 服务IP和端口
     */
    @Getter
    private String bootstrapServers;

    @Getter
    private String groupId;

    @Getter
    private String topic;

    private String jaasCfg;

    @Getter
    private String autoOffsetReset = "earliest";

    /**
     * 指定读取哪个分区的数据，默认为空，自平衡。
     */
    @Getter
    private List<Integer> partitions;

    @Getter
    private int poolTimeout;

    /**
     * 默认字符串
     */
    final private String keyDeserializer = "org.apache.kafka.common.serialization.StringSerializer";

    final private String valueDeserializer = "org.apache.kafka.common.serialization.StringSerializer";

    public Map<String, Object> getConfigs() {
        Map<String, Object> configs = new HashMap<>();
        // kafka地址
        configs.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
        // leader broker自己写入后就响应，不会等待ISR其他的副本写入(可选择值有0、all)
        configs.put(ProducerConfig.ACKS_CONFIG, "1");
        // 发送消息重试的次数 默认0
        configs.put(ProducerConfig.RETRIES_CONFIG, 0);
        // batch小吞吐量也会小 batch大内存压力会大
        configs.put(ProducerConfig.BATCH_SIZE_CONFIG, 16384);
        // 发送延时 默认是0 0的话不用等batch满就发送
        configs.put(ProducerConfig.LINGER_MS_CONFIG, 1);
        // 发送消息的内存缓冲区大小
        configs.put(ProducerConfig.BUFFER_MEMORY_CONFIG, 33554432);
        configs.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, keyDeserializer);
        configs.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, valueDeserializer);
        // 能够发送最大消息的大小,如果消息很大需要修改它
        //configs.put(ProducerConfig.MAX_REQUEST_SIZE_CONFIG, 1048576);
        if (StringUtils.isNotEmpty(jaasCfg)) {
            configs.put("security.protocol", "SASL_PLAINTEXT");
            configs.put("sasl.mechanism", "PLAIN");
            configs.put("sasl.jaas.config", jaasCfg);
        }
        return configs;
    }

    public void kafkaAuthor(String userName, String password) {
        String jaasTemplate = "org.apache.kafka.common.security.scram.ScramLoginModule required username=\"%s\" password=\"%s\";";
        this.jaasCfg = String.format(jaasTemplate, userName, password);
    }


    @Override
    public int hashCode() {
        return Objects.hash(bootstrapServers, groupId, topic, partitions, poolTimeout, keyDeserializer, valueDeserializer);
    }
}
