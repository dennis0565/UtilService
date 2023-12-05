package com.xck.util.kafka;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.clients.consumer.OffsetAndMetadata;
import org.apache.kafka.common.PartitionInfo;
import org.apache.kafka.common.TopicPartition;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.util.*;


/**
 * Created by nyx on 2020/1/24 10:43.
 * Kafka 读取，采集端专用
 */
@Component("kafkaReader")
@Scope("prototype")
@Slf4j
public class KafkaReader {
    private static final Logger KFK_LOGGER = LoggerFactory.getLogger("kafkaInfo");
    private KafkaConsumer kafkaConsumer;

    private KafkaReaderConf conf;

    /**
     * 最后一次读取时间，用于打印 topic 偏移量信息。
     */
    private long lastTime;

    private String topic;

    public boolean open() {
        try {
            Map<String, Object> configs = conf.getConfigs();
            topic = conf.getTopic();
//            KerberosUtil.initKafkaConsumer(configs);
            kafkaConsumer = new KafkaConsumer(configs);
            // 获取 PartitionInfo，如果网络不通，抛出 org.apache.kafka.common.errors.TimeoutException
            List<PartitionInfo> partitionInfoList = kafkaConsumer.partitionsFor(topic, Duration.ofMillis(conf.getPoolTimeout()));
            if (StringUtils.isEmpty(conf.getGroupId())) {
                //如果groupId为空，使用自己的状态文件seek offset
                List list = new ArrayList();
                for (PartitionInfo partitionInfo : partitionInfoList) {
                    //指定读哪个区
                    list.add(new TopicPartition(topic, partitionInfo.partition()));
                }
                if (list.isEmpty()) {
                    log.error("Kafka 分区参数不存在。");
                    return false;
                }

                //如果做为采集端，无论是网络模式还是单机模式，一定是采集全部partition
                kafkaConsumer.assign(list);

                // 一定是先 assign() 再 seek()
                for (PartitionInfo partitionInfo : partitionInfoList) {
                    // 根据配置文件状态，设置偏移
                    kafkaConsumer.seek(new TopicPartition(topic, partitionInfo.partition()), 1);
                }
            } else {
                kafkaConsumer.subscribe(Arrays.asList(topic));
            }
        } catch (org.apache.kafka.common.errors.TimeoutException e) {
            log.error("Kafka 网络连接异常！", e);
            return false;
        } catch (Exception e) {
            log.error("Kafka 参数配置错误。", e);
            return false;
        }
        return true;
    }

    public List<String> read() {
        List<String> list = new ArrayList<String>();
        try {
            this.printOffset();
            ConsumerRecords<String, String> consumerRecords = kafkaConsumer.poll(Duration.ofMillis(conf.getPoolTimeout()));
            if (consumerRecords.isEmpty()) {
                //consumerRecords 不会返回 null
                //如果为空，就进入下一次循环。不返回false，因为需要永远拉取，直到程序终止。
                return list;
            }
            for (ConsumerRecord<String, String> consumerRecord : consumerRecords) {
                String value = consumerRecord.value();
                if (value == null) {
                    continue;
                }

                //避免kafka被插了一些灵异字符
                value = value.trim();
                if (value.isEmpty()) {
                    continue;
                }

                list.add(value);
            }

            /*String table = "{\"key\":\"test\",\"taskId\":344,\"scipKey\":\"test\",\"pushContentType\":1,\"pushDate\":\"2021-04-28T09:02:26.227Z\",\"value\":{\"dbName\":\"ceshi\",\"tableFullName\":\"etl_source\",\"tableName\":\"etl_source\",\"senTypeId\":0,\"senLevelId\":0,\"aimStrategyIds\":\"1,2\",\"aimElementIds\":\"1,2\",\"rowCount\":100,\"storageSize\":10,\"fields\":[{\"field\":\"source_from\",\"elementIds\":\"1\",\"classId\":0,\"levelId\":0,\"samples\":[\"张三\"],\"hitResults\":[{\"id\":0,\"samples\":[\"张三\"]},{\"id\":1,\"samples\":[\"王五\"]}]}],\"businesssysId\":7015,\"ip\":\"string\",\"port\":0,\"assetType\":1}}";
            String file = "{\"key\":\"test\",\"scipKey\":\"test\",\"pushContentType\":2,\"taskId\":344,\"pushDate\":\"2021-04-28T08:36:52.582Z\",\"value\":{\"dbName\":\"/home/ueba\",\"tableFullName\":\"string\",\"tableName\":\"log.txt\",\"senTypeId\":0,\"senLevelId\":0,\"aimStrategyIds\":\"1,2\",\"aimElementIds\":\"1,2\",\"rowCount\":10,\"storageSize\":100,\"hitInfos\":[{\"index\":0,\"content\":\"test\",\"hitResults\":[{\"id\":0,\"samples\":[\"手机号码\"]},{\"id\":1,\"samples\":[\"身份证号码\"]}]}],\"businesssysId\":0,\"ip\":\"string\",\"port\":0,\"assetType\":1}}";
            String db = "{\"key\":\"test\",\"scipKey\":\"test\",\"pushContentType\":3,\"pushDate\":\"2021-04-28T10:31:35.577Z\",\"value\":{\"scanType\":1,\"sampleNum\":0,\"tableMonitor\":true,\"structureMonitor\":true,\"dbBaseResult\":{\"objName\":\"string\",\"targetName\":\"string\",\"tableTotalQty\":0,\"scanTotalQty\":0,\"scanTableNum\":0,\"senQty\":0,\"referSenQty\":0,\"errTables\":[{\"schemaname\":\"string\",\"tablename\":\"string\",\"fulltablename\":\"string\",\"errormsg\":\"string\"}],\"filterinfos\":[{\"filterid\":0,\"filtername\":\"string\",\"filtervalue\":\"string\",\"reason\":\"string\"}],\"modifiedResult\":{\"deltables\":\"string\",\"upedstructure\":\"string\",\"upedstructureinfos\":[{\"tablecode\":\"string\",\"orgStructInfos\":[\"string\"],\"nowStructInfos\":[\"string\"]}]},\"businesssysId\":7015,\"ip\":\"string\",\"port\":0,\"assetType\":1},\"taskId\":344,\"trackerId\":0,\"taskName\":\"string\",\"taskType\":1,\"execMode\":1,\"state\":0,\"beginDate\":\"2021-04-28T10:31:35.577Z\",\"endDate\":\"2021-04-28T10:31:35.577Z\",\"errorMsg\":\"string\"}}";
            list.add(table);
            list.add(file);
            list.add(db);*/
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
        return list;
    }

    /**
     * 定时打印偏移量日志
     */
    private void printOffset() {
        if (System.currentTimeMillis() - lastTime < 60_000) {
            return;
        }
        lastTime = System.currentTimeMillis();
        try {
            List<PartitionInfo> partitionInfoList = kafkaConsumer.partitionsFor(topic);//每次重新获取分区列表，避免分区调整数量改变
            List<TopicPartition> topicPartitionList = new ArrayList<>();
            for (PartitionInfo partitionInfo : partitionInfoList) {
                topicPartitionList.add(new TopicPartition(partitionInfo.topic(), partitionInfo.partition()));
            }

            Map<TopicPartition, OffsetAndMetadata> committedMap = kafkaConsumer.committed(new HashSet<>(topicPartitionList));
            Map<TopicPartition, Long> startOffsetsMap = kafkaConsumer.beginningOffsets(topicPartitionList);
            Map<TopicPartition, Long> endOffsetsMap = kafkaConsumer.endOffsets(topicPartitionList);

            StringBuilder sj = new StringBuilder("\n");
            for (TopicPartition topicPartition : topicPartitionList) {
                OffsetAndMetadata offsetAndMetadata = committedMap.get(topicPartition);
                if(offsetAndMetadata == null){
                    continue;
                }
                int partition = topicPartition.partition();
                long offset = offsetAndMetadata.offset();
                long start = startOffsetsMap.get(topicPartition);
                long end = endOffsetsMap.get(topicPartition);
                long lag = end - offset;
                sj.append("Topic: ").append(topic).append(", Partition: ").append(partition).append(", Start: ").append(start).append(", End: ").append(end).append(", ").append("Offset: ").append(offset).append(", Lag: ").append(lag);
            }
            KFK_LOGGER.info("Kafka Consumer 偏移信息：\n" + sj.toString());
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
    }

    public void close() {
        kafkaConsumer.close();
    }

    public KafkaReaderConf getConf() {
        return conf;
    }

    public void setConf(KafkaReaderConf conf) {
        this.conf = conf;
    }

    public static void main(String[] args) throws InterruptedException {
        KafkaReaderConf conf=new KafkaReaderConf();
        conf.setBootstrapServers("10.10.25.31:29092");
        conf.setTopic("databus-consumer-upload1");
        conf.setPoolTimeout(1000);
        conf.setGroupId("test2");
        KafkaReader kafkaReader=new KafkaReader();
        kafkaReader.setConf(conf);
        try {
            kafkaReader.open();
            do {
                Thread.sleep(1000);
                List<String> read = kafkaReader.read();
                System.out.println("本次拉取消息数" + read.size());
            } while (true);
        }catch (Exception e){
            e.printStackTrace();
        }finally {
            kafkaReader.close();
        }

    }
}
