package com.ksoot.feast;

import com.ksoot.feast.config.FeastClientConfiguration;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;

@Disabled
@SpringJUnitConfig(classes = {FeastClientConfiguration.class, TestConfiguration.class})
@TestPropertySource("classpath:application-test.properties")
class FeastClientFeatureServiceTest {

  @Autowired private FeastClient feastClient;

  @Test
  @DisplayName("Test Apply Feature Service")
  void testFeatureServiceApply() {
    this.feastClient
        .featureService("driver_activity_batch_v1")
        .featureReference("driver_hourly_stats_batch", "conv_rate", "acc_rate")
        .description("Driver activity feature service with Batch source for testing")
        .owner("Rajveer Singh")
        .tag("team", "driver_performance")
        .apply();
  }
}
