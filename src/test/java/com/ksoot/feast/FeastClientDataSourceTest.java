package com.ksoot.feast;

import com.ksoot.feast.config.FeastClientConfiguration;
import com.ksoot.feast.dto.registry.Feature;
import feast.proto.types.ValueProto;
import java.time.Duration;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;

@Disabled
@SpringJUnitConfig(classes = {FeastClientConfiguration.class, TestConfiguration.class})
@TestPropertySource("classpath:application-test.properties")
class FeastClientDataSourceTest {

  @Autowired private FeastClient feastClient;

  @Test
  @DisplayName("Test Apply PostgresDataSource successfully")
  @Order(1)
  void testPostgresDataSourceApply() {
    this.feastClient
        .postgresSqlSource("driver_stats_batch_source")
        .query("select * from driver_hourly_stats")
        .eventTimestampField("event_timestamp")
        .createdTimestampField("created")
        .description("Postgres Batch DataSource for testing")
        .owner("Rajveer Singh")
        .tag("team", "driver_performance")
        .apply();
  }

  @Test
  @DisplayName("Test Apply PushSource successfully")
  @Order(2)
  void testPushSourceApply() {
    this.feastClient
        .pushSource("driver_stats_push_source")
        .batchSource("driver_stats_batch_source")
        .description("PushSource for testing")
        .owner("Rajveer Singh")
        .tag("team", "driver_performance")
        .apply();
  }

  @Test
  @DisplayName("Test KafkaStreamSource successfully")
  @Order(3)
  void testKafkaStreamSourceApply() {
    this.feastClient
        .kafkaSource("driver_stats_stream_source")
        .bootstrapServers("localhost:9092")
        .topicName("driver_stats_topic")
        .schemaJson(
            "driver_id integer, event_timestamp timestamp, conv_rate double, acc_rate double, avg_daily_trips double, created timestamp")
        .watermarkDelayThreshold(Duration.ofMinutes(5))
        .batchSource("driver_stats_batch_source")
        .eventTimestampField("event_timestamp")
        .description("KafkaSource for testing")
        .owner("Rajveer Singh")
        .tag("team", "driver_performance")
        .apply();
  }

  @Test
  @DisplayName("Test RequestSource successfully")
  @Order(4)
  void testRequestSourceApply() {
    ValueProto.ValueType.Enum valueType = ValueProto.ValueType.Enum.INT64;
    this.feastClient
        .requestSource("vals_to_add")
        .schema(
            Feature.name("val_to_add")
                //                .valueType(ValueProto.ValueType.Enum.INT64)
                .valueType(valueType)
                .description("Add value to feature at runtime")
                .tag("team", "driver_performance")
                .build(),
            Feature.of("val_to_add_2", valueType))
        .description("RequestSource for testing")
        .owner("Rajveer Singh")
        .tag("team", "driver_performance")
        .apply();
  }
}
