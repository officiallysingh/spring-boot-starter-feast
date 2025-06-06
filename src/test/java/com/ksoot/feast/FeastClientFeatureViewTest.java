package com.ksoot.feast;

import com.ksoot.feast.config.FeastClientConfiguration;
import com.ksoot.feast.dto.registry.Feature;
import feast.proto.types.ValueProto;
import java.time.Duration;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;

@Disabled
@SpringJUnitConfig(classes = {FeastClientConfiguration.class, TestConfiguration.class})
@TestPropertySource("classpath:application-test.properties")
class FeastClientFeatureViewTest {

  @Autowired private FeastClient feastClient;

  @Test
  @DisplayName("Test Apply Feature View")
  void testFeatureViewApply() {
    ValueProto.ValueType.Enum valueType = ValueProto.ValueType.Enum.FLOAT;
    this.feastClient
        .featureView("driver_hourly_stats_batch")
        .entities("driver")
        .features(
            Feature.name("conv_rate")
                .valueType(ValueProto.ValueType.Enum.FLOAT)
                .description("For testing")
                .tag("team", "driver_performance")
                .build(),
            Feature.name("acc_rate").valueType(valueType).description("For testing").build(),
            Feature.name("avg_daily_trips").valueType(valueType).build())
        .dataSource("driver_stats_batch_source")
        .ttl(Duration.ofDays(1))
        .online()
        .description("Driver hourly stats feature view with Batch source for testing")
        .owner("Rajveer Singh")
        .tag("team", "driver_performance")
        .apply();
  }
}
