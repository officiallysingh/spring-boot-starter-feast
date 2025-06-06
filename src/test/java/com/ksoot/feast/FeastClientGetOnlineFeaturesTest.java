package com.ksoot.feast;

import ch.qos.logback.classic.Level;
import com.ksoot.feast.config.FeastClientConfiguration;
import com.ksoot.feast.dto.serving.OnlineFeatures;
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
@TestPropertySource("classpath:application-test.properties")
class FeastClientGetOnlineFeaturesTest {

  @Autowired private FeastClient feastClient;

  @BeforeAll
  public static void setUp() {
    ch.qos.logback.classic.Logger root =
        (ch.qos.logback.classic.Logger) LoggerFactory.getLogger(FeastServingFeignClient.class);
    root.setLevel(Level.DEBUG);
  }

  @Test
  @DisplayName("Test Get Online Features")
  void testGetOnlineFeatures() {
    final OnlineFeatures onlineFeatures =
        this.feastClient.onlineFeatures("driver_activity_batch_v1").entity("driver_id", 1001).get();
    System.out.println(onlineFeatures);
  }
}
