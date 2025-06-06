package com.ksoot.feast;

import ch.qos.logback.classic.Level;
import com.ksoot.feast.config.FeastClientConfiguration;
import java.time.OffsetDateTime;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;

@Disabled
@SpringJUnitConfig(classes = {FeastClientConfiguration.class, TestConfiguration.class})
@TestPropertySource(
    locations = "classpath:application-test.properties",
    properties = "spring.profiles.active=test")
class FeastClientMaterializationTest {

  @Autowired private FeastClient feastClient;

  @BeforeAll
  public static void setUp() {
    ch.qos.logback.classic.Logger root =
        (ch.qos.logback.classic.Logger) LoggerFactory.getLogger(FeastServingFeignClient.class);
    root.setLevel(Level.DEBUG);
  }

  @Test
  @DisplayName("Test Materialize specific feature views successfully")
  void testMaterializeFeatureViews() {
    this.feastClient
        .materializeFrom(OffsetDateTime.now().minusMonths(1))
        .till(OffsetDateTime.now())
        .featureView("driver_hourly_stats_batch")
        .featureView("driver_hourly_stats_push")
        .apply();
  }

  @Test
  @DisplayName("Test Materialize Incremental successfully")
  void testMaterializeIncremental() {
    this.feastClient
        .materializeIncremental(OffsetDateTime.now())
        .featureViews("driver_hourly_stats_batch")
        .apply();
  }

  @Test
  @DisplayName("Test Materialize Incremental Till current timestamp successfully")
  void testMaterializeIncrementalTillNow() {
    this.feastClient
        .materializeIncrementalTillNow()
        .featureViews("driver_hourly_stats_batch")
        .apply();
  }

  @Test
  @DisplayName("Test Materialize successfully")
  void testMaterialize() {
    this.feastClient.materializeFrom(OffsetDateTime.now()).till(OffsetDateTime.now()).apply();
  }
}
