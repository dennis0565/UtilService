//package com.xck.util.kafka;
//
//import lombok.extern.slf4j.Slf4j;
//
//import java.io.File;
//import java.io.FileInputStream;
//import java.util.Map;
//import java.util.Objects;
//import java.util.Properties;
//
///**
// * Created by nyx on 2020/11/17 13:37.
// */
//@Slf4j
//public class KerberosUtil {
//
//    public static void initKafkaConsumer(Map<String, Object> configs) {
//        try {
//            Properties properties = initProperties("krb5kafka_consumer.properties");
//            if (properties != null) {
//                System.setProperty("java.security.krb5.conf", (String) properties.get("java.security.krb5.conf"));
//                System.setProperty("java.security.user.keytab", (String) properties.get("java.security.user.keytab"));
//                //jaas文件
//                System.setProperty("java.security.auth.login.config", (String) properties.get("java.security.auth.login.config"));
//                //移除楼上 4 个系统属性
//                properties.remove("kerberos.enable");
//                properties.remove("java.security.krb5.conf");
//                properties.remove("java.security.user.keytab");
//                properties.remove("java.security.auth.login.config");
//                //其它属性加到 kafka 客户端上
//                properties.forEach((k, v) -> configs.put((String) k, v));
//            }
//        } catch (Exception e) {
//            log.error(e.getMessage(), e);
//        }
//    }
//
//    public static void initKafkaProducer(Map<String, Object> configs) {
//        Properties properties = initProperties("krb5kafka_producer.properties");
//        if (properties != null) {
//            System.setProperty("java.security.krb5.conf", (String) properties.get("java.security.krb5.conf"));
//            System.setProperty("java.security.user.keytab", (String) properties.get("java.security.user.keytab"));
//            //jaas文件
//            System.setProperty("java.security.auth.login.config", (String) properties.get("java.security.auth.login.config"));
//            //移除楼上 4 个系统属性
//            properties.remove("kerberos.enable");
//            properties.remove("java.security.krb5.conf");
//            properties.remove("java.security.user.keytab");
//            properties.remove("java.security.auth.login.config");
//            //其它属性加到 kafka 客户端上
//            properties.forEach((k, v) -> configs.put((String) k, v));
//        }
//    }
//
//    private static Properties initProperties(String fileName) {
//        try {
//            File propFile = ArgusFileUtil.getFile(fileName);
//            if (!propFile.exists()) {
//                return null;
//            }
//            FileInputStream fis = new FileInputStream(propFile);
//            Properties properties = new Properties();
//            properties.load(fis);
//            fis.close();
//            //是否启用 kerberos 认证
//            return Objects.equals(properties.get("kerberos.enable"), "true") ? properties : null;
//        } catch (Exception e) {
//            log.error(e.getMessage(), e);
//        }
//        return null;
//    }
//}
