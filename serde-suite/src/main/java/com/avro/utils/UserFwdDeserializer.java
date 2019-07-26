package com.avro.utils;

import com.avro.fwd.consumer.TestUser;
import io.confluent.kafka.serializers.KafkaAvroDeserializer;
import org.apache.avro.Schema;
import org.apache.kafka.common.errors.SerializationException;
import org.apache.kafka.common.serialization.Deserializer;

public class UserFwdDeserializer extends KafkaAvroDeserializer implements Deserializer<Object> {

  public static final Schema DESERIALIZATION_SCHEMA = TestUser.getClassSchema();

  @Override
  public Object deserialize(String topic, byte[] payload) {
    try {
      return deserialize(payload, DESERIALIZATION_SCHEMA);
    } catch (SerializationException e) {
      throw e;
    }
  }
}