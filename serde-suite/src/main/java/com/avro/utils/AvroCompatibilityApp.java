package com.avro.utils;


import io.confluent.kafka.serializers.AbstractKafkaAvroSerDeConfig;
import io.confluent.kafka.serializers.KafkaAvroDeserializerConfig;
import io.confluent.kafka.serializers.KafkaAvroSerializer;
import java.util.Properties;
import java.util.UUID;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.apache.kafka.common.serialization.StringSerializer;


public class AvroCompatibilityApp {

  private String bootstrapServers = "localhost:9092";
  private String schema_registry_url = "http://localhost:8081";


  public <T> KafkaProducer<String, T> createKafkaProducer() {
    Properties props = new Properties();
    props.setProperty(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
    props.setProperty(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());
    props.setProperty(
        ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, KafkaAvroSerializer.class.getName());
    props.setProperty(ProducerConfig.COMPRESSION_TYPE_CONFIG, "snappy");
    props.setProperty(
        AbstractKafkaAvroSerDeConfig.SCHEMA_REGISTRY_URL_CONFIG, schema_registry_url);

    return new org.apache.kafka.clients.producer.KafkaProducer(props);
  }

  public <T> KafkaConsumer<String, T> createConsumer() {
    Properties props = new Properties();
    props.setProperty(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
    props.setProperty(
        ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getName());
    props.setProperty(
        ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, UserDeserializer.class.getName());
    props.setProperty(ConsumerConfig.GROUP_ID_CONFIG, UUID.randomUUID().toString());
    props.setProperty(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");
    props.setProperty(ConsumerConfig.ISOLATION_LEVEL_CONFIG, "read_committed");
    props.put(KafkaAvroDeserializerConfig.SPECIFIC_AVRO_READER_CONFIG, true);
    props.put(AbstractKafkaAvroSerDeConfig.SCHEMA_REGISTRY_URL_CONFIG, schema_registry_url);
    props.put(AbstractKafkaAvroSerDeConfig.AUTO_REGISTER_SCHEMAS, false);

    return new KafkaConsumer<String, T>(props);

  }
}