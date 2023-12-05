package com.xck.util.kafka;

import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.UUID;

/**
 * 写Kafka
 */
@Component("kafkaWriter")
@Scope("prototype")
@Slf4j
public class KafkaWriter {

    private static final Logger LOGGER = LoggerFactory.getLogger(KafkaWriter.class);

    private KafkaWriterConf conf;

    private KafkaProducer kafkaProducer;

    public void open( ) throws Exception {
        try {
            Map<String, Object> configs = conf.getConfigs();
//            KerberosUtil.initKafkaConsumer(configs);
            kafkaProducer = new KafkaProducer(configs);
        } catch (org.apache.kafka.common.errors.TimeoutException e) {
            LOGGER.error("Kafka 网络连接异常", e);
            throw new Exception(e.getMessage());
        } catch (Exception e) {
            LOGGER.error("Kafka 参数配置错误", e);
            throw new Exception(e.getMessage());
        }
    }

    public void writer(String msg) {
        try {
            if (kafkaProducer == null) {
                // 如果连接不存在，则重新创建kafka连接
                open();
            }
            String topic = conf.getTopic();
            ProducerRecord<String, String> record = new ProducerRecord(topic, msg);
            kafkaProducer.send(record);
        } catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
        }
    }

    public void close() {
        if (kafkaProducer != null) {
            kafkaProducer.close();
        }
    }

    public void setConf(KafkaWriterConf conf) {
        this.conf = conf;
    }

    public static KafkaWriter getInstance() {
        return instance;
    }

    private static KafkaWriter instance;

    static {
        if (instance == null) {
            instance = new KafkaWriter();
        }
    }

    public static void main(String[] args) throws InterruptedException {
        KafkaWriterConf conf=new KafkaWriterConf();
        conf.setBootstrapServers("10.10.25.31:29092");
        conf.setTopic("databus-consumer-upload1");
        conf.setPoolTimeout(1000);
        KafkaWriter kafkaWriter=new KafkaWriter();
        kafkaWriter.setConf(conf);

        for (int i = 0; i < 10000; i++) {
            kafkaWriter.writer("{\"sourceId\":\"" + UUID.randomUUID()+
                    "\",\"assetsType\":\" \",\"srcIp\":\"10.221.3.13\",\"model_view\":null,\"msgType\":\"alarmUpload\",\"parser_total\":null,\"alarmTime\":\"2023-11-14 16:08:05\",\"alarmName\":\"僵尸网络\",\"generic_raw_log\":null,\"devName\":\" \",\"uuid\":\"b2cb8c0d-80b2-41fa-b1f7-a3eaa6b1f4d9\",\"alarmGrade\":\"2\",\"alarmDesc\":\"发现威胁情报：i.zaberno.com，情报类型：僵尸网络。\",\"generic_into_time\":\"2023-11-14 16:08:13.383\",\"application\":\" \",\"createTime\":\"2023-11-14 16:08:05\",\"generic_create_time\":\"2023-11-14 16:08:11.691\",\"generic_datasource_type\":\"/安全设备/其他\",\"dstIp\":\"114.114.114.114\",\"parser_count\":null,\"ueba_flow_id\":\"336\",\"generic_device_ip\":\"10.223.226.116\"}");
//            Thread.sleep(1000);
        }

        Thread.sleep(10000);
        kafkaWriter.close();
    }
}
