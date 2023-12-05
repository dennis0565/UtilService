package com.xck.util.kafka;

import lombok.Getter;
import lombok.Setter;
import org.apache.kafka.clients.consumer.ConsumerConfig;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

@Getter
@Setter
public class KafkaReaderConf {

    /**
     * 服务IP和端口
     */
    @Getter
    private String bootstrapServers;

    @Getter
    private String groupId;

    @Getter
    private String topic;

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
    final private String keyDeserializer = "org.apache.kafka.common.serialization.StringDeserializer";

    final private String valueDeserializer = "org.apache.kafka.common.serialization.StringDeserializer";

    public Map<String, Object> getConfigs() {
        Map<String, Object> configs = new HashMap<>();
        configs.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
        configs.put(ConsumerConfig.GROUP_ID_CONFIG, groupId);
        configs.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, keyDeserializer);
        configs.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, valueDeserializer);
        configs.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, autoOffsetReset);
        //自动提交
        configs.put(ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG, true);
        //自动提交间隔
        configs.put(ConsumerConfig.AUTO_COMMIT_INTERVAL_MS_CONFIG, 5000);
        //每次拉取最大条数
        configs.put(ConsumerConfig.MAX_POLL_RECORDS_CONFIG, 200);
        configs.put(ConsumerConfig.MAX_POLL_INTERVAL_MS_CONFIG,800000);
        return configs;
    }

    @Override
    public int hashCode() {
        return Objects.hash(bootstrapServers, groupId, topic, partitions, poolTimeout, keyDeserializer, valueDeserializer);
    }
}
