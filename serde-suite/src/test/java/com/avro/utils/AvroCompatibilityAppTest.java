package com.avro.utils;

import java.time.Duration;
import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.concurrent.ExecutionException;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.clients.producer.RecordMetadata;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class AvroCompatibilityAppTest {

  DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")
      .withZone(ZoneId.systemDefault());

  private AvroCompatibilityApp avroCompatibilityApp;
  @BeforeEach
  public void setUp(){
    avroCompatibilityApp = new AvroCompatibilityApp();
  }

  private com.avro.producer.TestUser getProducerRecord() {
    return com.avro.producer.TestUser.newBuilder()
        .setName("Test-USER-1.0")
        .setFavoriteColor("BLACK")
        .setAge(19)
        .build();
  }

  private com.avro.fwd.producer.TestUser getProducerRecordFWD() {
    return com.avro.fwd.producer.TestUser.newBuilder()
        .setTimestamp(DATE_TIME_FORMATTER.format(Instant.now()))
        .setName("Test-USER-FWD-1.0")
        .setFavoriteColor("BLACK")
        .setAge(19)
        .setDept("IT")
        .setGender("O")
        .build();
  }

  public void produceUserData() throws InterruptedException, ExecutionException {

    String topic = "user_data_bck_producer";
    var record =
        new ProducerRecord<String, Object>(topic, getProducerRecord());
    RecordMetadata recordMetadata = avroCompatibilityApp.createKafkaProducer().send(record).get();
    System.out.println(recordMetadata.offset());
  }

  
  public void consumeUserData() {
    String topic = "user_data_bck_producer";
    KafkaConsumer<String, com.avro.consumer.TestUser> consumer = avroCompatibilityApp.createConsumer();
    consumer.subscribe(List.of(topic));
    while (true) {
      consumer.poll(Duration.ofMillis(100)).forEach(record -> processEvents(record.value()));
    }
  }


  
  public void produceFWDUserData() throws InterruptedException, ExecutionException {

    String topic = "user_data_fwd_producer";
    var record =
        new ProducerRecord<String,Object>(topic, getProducerRecordFWD());
    RecordMetadata recordMetadata = avroCompatibilityApp.createKafkaProducer().send(record).get();
    System.out.println(recordMetadata.offset());
  }

  
  public void consumeFWDUserData() {
    String topic = "user_data_fwd_producer";
    KafkaConsumer<String, com.avro.fwd.consumer.TestUser> consumer = avroCompatibilityApp.createConsumer();
    consumer.subscribe(List.of(topic));
    while (true) {
      ConsumerRecords<String, com.avro.fwd.consumer.TestUser> records = consumer
          .poll(Duration.ofMillis(100));
      records.forEach(record -> processEvents(record.value()));
    }
  }

  public void processEvents(Object record) {
    System.out.println(record);
  }

}